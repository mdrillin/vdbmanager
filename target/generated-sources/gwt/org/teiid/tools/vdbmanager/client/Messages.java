package org.teiid.tools.vdbmanager.client;

/**
 * Interface to represent the messages contained in resource bundle:
 * 	/home/mdrillin/workspace/vdbmanager/src/main/resources/org/teiid/tools/vdbmanager/client/Messages.properties'.
 */
public interface Messages extends com.google.gwt.i18n.client.Messages {
  
  /**
   * Translated "Query Web".
   * 
   * @return translated "Query Web"
   */
  @DefaultMessage("Query Web")
  @Key("appTitle.txt")
  String appTitle_txt();

  /**
   * Translated "Enter your name".
   * 
   * @return translated "Enter your name"
   */
  @DefaultMessage("Enter your name")
  @Key("nameField")
  String nameField();

  /**
   * Translated "Send".
   * 
   * @return translated "Send"
   */
  @DefaultMessage("Send")
  @Key("sendButton")
  String sendButton();
}
