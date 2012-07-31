package org.teiid.tools.vdbmanager.client;

import com.google.gwt.user.client.rpc.impl.RemoteServiceProxy;
import com.google.gwt.user.client.rpc.impl.ClientSerializationStreamWriter;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.impl.RequestCallbackAdapter.ResponseReader;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.RpcToken;
import com.google.gwt.user.client.rpc.RpcTokenException;
import com.google.gwt.core.client.impl.Impl;
import com.google.gwt.user.client.rpc.impl.RpcStatsContext;

public class TeiidMgrService_Proxy extends RemoteServiceProxy implements org.teiid.tools.vdbmanager.client.TeiidMgrServiceAsync {
  private static final String REMOTE_SERVICE_INTERFACE_NAME = "org.teiid.tools.vdbmanager.client.TeiidMgrService";
  private static final String SERIALIZATION_POLICY ="D58CBB3122991EDE9B50602A751E0FFC";
  private static final org.teiid.tools.vdbmanager.client.TeiidMgrService_TypeSerializer SERIALIZER = new org.teiid.tools.vdbmanager.client.TeiidMgrService_TypeSerializer();
  
  public TeiidMgrService_Proxy() {
    super(GWT.getModuleBaseURL(),
      "teiid", 
      SERIALIZATION_POLICY, 
      SERIALIZER);
  }
  
  public void addSourceAndModel(java.lang.String vdbName, java.lang.String sourceName, java.lang.String templateName, java.lang.String translatorName, java.util.Map propsMap, com.google.gwt.user.client.rpc.AsyncCallback callback) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("TeiidMgrService_Proxy", "addSourceAndModel");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 5);
      streamWriter.writeString("java.lang.String/2004016611");
      streamWriter.writeString("java.lang.String/2004016611");
      streamWriter.writeString("java.lang.String/2004016611");
      streamWriter.writeString("java.lang.String/2004016611");
      streamWriter.writeString("java.util.Map");
      streamWriter.writeString(vdbName);
      streamWriter.writeString(sourceName);
      streamWriter.writeString(templateName);
      streamWriter.writeString(translatorName);
      streamWriter.writeObject(propsMap);
      helper.finish(callback, ResponseReader.STRING);
    } catch (SerializationException ex) {
      callback.onFailure(ex);
    }
  }
  
  public void addViewModel(java.lang.String vdbName, java.lang.String viewModelName, java.util.Map propsMap, com.google.gwt.user.client.rpc.AsyncCallback callback) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("TeiidMgrService_Proxy", "addViewModel");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 3);
      streamWriter.writeString("java.lang.String/2004016611");
      streamWriter.writeString("java.lang.String/2004016611");
      streamWriter.writeString("java.util.Map");
      streamWriter.writeString(vdbName);
      streamWriter.writeString(viewModelName);
      streamWriter.writeObject(propsMap);
      helper.finish(callback, ResponseReader.STRING);
    } catch (SerializationException ex) {
      callback.onFailure(ex);
    }
  }
  
  public void createVDB(java.lang.String vdbName, com.google.gwt.user.client.rpc.AsyncCallback callback) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("TeiidMgrService_Proxy", "createVDB");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 1);
      streamWriter.writeString("java.lang.String/2004016611");
      streamWriter.writeString(vdbName);
      helper.finish(callback, ResponseReader.OBJECT);
    } catch (SerializationException ex) {
      callback.onFailure(ex);
    }
  }
  
  public void deleteVDB(java.lang.String vdbName, com.google.gwt.user.client.rpc.AsyncCallback callback) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("TeiidMgrService_Proxy", "deleteVDB");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 1);
      streamWriter.writeString("java.lang.String/2004016611");
      streamWriter.writeString(vdbName);
      helper.finish(callback, ResponseReader.OBJECT);
    } catch (SerializationException ex) {
      callback.onFailure(ex);
    }
  }
  
  public void getDataSourceTemplates(com.google.gwt.user.client.rpc.AsyncCallback callback) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("TeiidMgrService_Proxy", "getDataSourceTemplates");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 0);
      helper.finish(callback, ResponseReader.OBJECT);
    } catch (SerializationException ex) {
      callback.onFailure(ex);
    }
  }
  
  public void getDynamicVDBNames(com.google.gwt.user.client.rpc.AsyncCallback callback) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("TeiidMgrService_Proxy", "getDynamicVDBNames");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 0);
      helper.finish(callback, ResponseReader.OBJECT);
    } catch (SerializationException ex) {
      callback.onFailure(ex);
    }
  }
  
  public void getPropertyDefns(java.lang.String templateName, com.google.gwt.user.client.rpc.AsyncCallback callback) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("TeiidMgrService_Proxy", "getPropertyDefns");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 1);
      streamWriter.writeString("java.lang.String/2004016611");
      streamWriter.writeString(templateName);
      helper.finish(callback, ResponseReader.OBJECT);
    } catch (SerializationException ex) {
      callback.onFailure(ex);
    }
  }
  
  public void getPropertyNames(java.lang.String templateName, com.google.gwt.user.client.rpc.AsyncCallback callback) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("TeiidMgrService_Proxy", "getPropertyNames");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 1);
      streamWriter.writeString("java.lang.String/2004016611");
      streamWriter.writeString(templateName);
      helper.finish(callback, ResponseReader.OBJECT);
    } catch (SerializationException ex) {
      callback.onFailure(ex);
    }
  }
  
  public void getTranslatorNames(com.google.gwt.user.client.rpc.AsyncCallback callback) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("TeiidMgrService_Proxy", "getTranslatorNames");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 0);
      helper.finish(callback, ResponseReader.OBJECT);
    } catch (SerializationException ex) {
      callback.onFailure(ex);
    }
  }
  
  public void getVDBModelInfo(java.lang.String vdbName, com.google.gwt.user.client.rpc.AsyncCallback callback) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("TeiidMgrService_Proxy", "getVDBModelInfo");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 1);
      streamWriter.writeString("java.lang.String/2004016611");
      streamWriter.writeString(vdbName);
      helper.finish(callback, ResponseReader.OBJECT);
    } catch (SerializationException ex) {
      callback.onFailure(ex);
    }
  }
  
  public void initApplication(int serverPort, java.lang.String userName, java.lang.String password, com.google.gwt.user.client.rpc.AsyncCallback callback) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("TeiidMgrService_Proxy", "initApplication");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 3);
      streamWriter.writeString("I");
      streamWriter.writeString("java.lang.String/2004016611");
      streamWriter.writeString("java.lang.String/2004016611");
      streamWriter.writeInt(serverPort);
      streamWriter.writeString(userName);
      streamWriter.writeString(password);
      helper.finish(callback, ResponseReader.OBJECT);
    } catch (SerializationException ex) {
      callback.onFailure(ex);
    }
  }
  
  public void isRunningOnOpenShift(com.google.gwt.user.client.rpc.AsyncCallback callback) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("TeiidMgrService_Proxy", "isRunningOnOpenShift");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 0);
      helper.finish(callback, ResponseReader.OBJECT);
    } catch (SerializationException ex) {
      callback.onFailure(ex);
    }
  }
  
  public void removeModels(java.lang.String vdbName, java.util.List removeModelNameList, com.google.gwt.user.client.rpc.AsyncCallback callback) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("TeiidMgrService_Proxy", "removeModels");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 2);
      streamWriter.writeString("java.lang.String/2004016611");
      streamWriter.writeString("java.util.List");
      streamWriter.writeString(vdbName);
      streamWriter.writeObject(removeModelNameList);
      helper.finish(callback, ResponseReader.OBJECT);
    } catch (SerializationException ex) {
      callback.onFailure(ex);
    }
  }
  @Override
  public SerializationStreamWriter createStreamWriter() {
    ClientSerializationStreamWriter toReturn =
      (ClientSerializationStreamWriter) super.createStreamWriter();
    if (getRpcToken() != null) {
      toReturn.addFlags(ClientSerializationStreamWriter.FLAG_RPC_TOKEN_INCLUDED);
    }
    return toReturn;
  }
  @Override
  protected void checkRpcTokenType(RpcToken token) {
    if (!(token instanceof com.google.gwt.user.client.rpc.XsrfToken)) {
      throw new RpcTokenException("Invalid RpcToken type: expected 'com.google.gwt.user.client.rpc.XsrfToken' but got '" + token.getClass() + "'");
    }
  }
}
