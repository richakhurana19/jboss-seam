<?xml version="1.0"?>
<html xmlns:jsp="http://java.sun.com/JSP/Page" 
      xmlns:h="http://java.sun.com/jsf/html"
      xmlns:f="http://java.sun.com/jsf/core"
      xmlns="http://www.w3.org/1999/xhtml">
  <jsp:output doctype-root-element="html"
              doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
              doctype-system="http://www.w3c.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"/>
  <jsp:directive.page contentType="text/html"/>
  <head>
    <title>You won!</title>
  </head>
  <body>
    <h1>You won!</h1>
    <f:view>
      Yes, the answer was <h:outputText value="#{numberGuess.currentGuess}" />.
      It took you <h:outputText value="#{numberGuess.guessCount}" /> guesses.
      Would you like to <a href="numberGuess.seam">play again</a>?
    </f:view>
  </body>
</html>
