package org.jboss.seam.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.seam.navigation.Pages;
import org.jboss.seam.util.EnumerationEnumeration;

/**
 * The Seam resource bundle which searches for resources in delegate resource
 * bundles specified in pages.xml, and a configurable list of delegate resource
 * bundles specified in components.xml.
 * 
 * @see ResourceLoader
 * @author Gavin King
 * 
 */
public class SeamResourceBundle extends java.util.ResourceBundle
{
   private Map<Locale, List<ResourceBundle>> bundleCache = new ConcurrentHashMap<Locale, List<ResourceBundle>>();

   /**
    * Get an instance for the current Seam Locale
    * 
    * @see Locale
    * 
    * @return a SeamResourceBundle
    */
   public static java.util.ResourceBundle getBundle()
   {
      return java.util.ResourceBundle.getBundle(SeamResourceBundle.class.getName(),
               org.jboss.seam.core.Locale.instance());
   }

   private List<java.util.ResourceBundle> getBundlesForCurrentLocale()
   {
      Locale instance = org.jboss.seam.core.Locale.instance();
      if (bundleCache.get(instance) == null)
      {
         bundleCache.put(instance, loadBundlesForCurrentLocale());
      }
      return bundleCache.get(instance);

   }

   private List<ResourceBundle> loadBundlesForCurrentLocale()
   {
      List<ResourceBundle> bundles = new ArrayList<ResourceBundle>();
      ResourceLoader resourceLoader = ResourceLoader.instance();
      for (String bundleName : resourceLoader.getBundleNames())
      {
         ResourceBundle bundle = resourceLoader.loadBundle(bundleName);
         if (bundle != null) bundles.add(bundle);
      }
      ResourceBundle bundle = resourceLoader.loadBundle("ValidatorMessages");
      if (bundle != null)
      {
         bundles.add(bundle);
      }
      bundle = resourceLoader
               .loadBundle("org/hibernate/validator/resources/DefaultValidatorMessages");
      if (bundle != null) bundles.add(bundle);
      bundle = resourceLoader.loadBundle("javax.faces.Messages");
      if (bundle != null) bundles.add(bundle);
      return Collections.unmodifiableList(bundles);
   }

   @Override
   public Enumeration<String> getKeys()
   {
      List<java.util.ResourceBundle> pageBundles = getPageResourceBundles();
      List<ResourceBundle> bundles = getBundlesForCurrentLocale();
      Enumeration<String>[] enumerations = new Enumeration[bundles.size() + pageBundles.size()];
      int i = 0;
      for (; i < pageBundles.size(); i++)
      {
         enumerations[i++] = pageBundles.get(i).getKeys();
      }
      for (; i < bundles.size(); i++)
      {
         enumerations[i] = bundles.get(i).getKeys();
      }
      return new EnumerationEnumeration<String>(enumerations);
   }

   @Override
   protected Object handleGetObject(String key)
   {
      List<java.util.ResourceBundle> pageBundles = getPageResourceBundles();
      for (java.util.ResourceBundle pageBundle : pageBundles)
      {
         try
         {
            return interpolate(pageBundle.getObject(key));
         }
         catch (MissingResourceException mre) {}
      }

      for (java.util.ResourceBundle littleBundle : getBundlesForCurrentLocale())
      {
         try
         {
               return interpolate( littleBundle.getObject(key) );
         }
         catch (MissingResourceException mre) {}
      }

      return null; // superclass is responsible for throwing MRE
   }

   private Object interpolate(Object message)
   {
      return message!=null && message instanceof String ?
               Interpolator.instance().interpolate( (String) message ) :
               message;
   }

   private List<java.util.ResourceBundle> getPageResourceBundles()
   {
      // TODO: oops! A hard dependency to JSF!
      String viewId = Pages.getCurrentViewId();
      if (viewId != null)
      {
         // we can't cache these bundles, since the viewId
         // may change in the middle of a request
         return Pages.instance().getResourceBundles(viewId);
      }
      else
      {
         return Collections.EMPTY_LIST;
      }
   }
}