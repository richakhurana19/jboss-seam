/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.seam;

import java.io.Serializable;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.BeginTask;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.EndTask;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;

@Stateful
@Name("ship")
public class ShipAction
    implements Ship,
               Serializable
{
    @In(value="currentUser", required=false)
    Admin admin;

    @PersistenceContext(type=PersistenceContextType.EXTENDED)
    EntityManager em;
    
    @Out(required=false, scope=ScopeType.CONVERSATION)
    Order order;

    @In(required=false)
    Long orderId;

    String track;

    public String getTrack() {
        return track;
    }
    public void setTrack(String track) {
        this.track=track;
    }

    @BeginTask
    public String viewTask() {
        order = em.find(Order.class, orderId);
        return "ship";
    }
    
    @EndTask
    public String ship() {
        if (track == null || track.length()==0) {
            // invalid message
            return null;
        }
        
        order.ship(track);
        
        return "admin";
    }

    @Destroy @Remove
    public void destroy() { }
}
