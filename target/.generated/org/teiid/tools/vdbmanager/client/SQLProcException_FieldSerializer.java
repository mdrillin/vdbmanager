package org.teiid.tools.vdbmanager.client;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class SQLProcException_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getSqlDetail(org.teiid.tools.vdbmanager.client.SQLProcException instance) /*-{
    return instance.@org.teiid.tools.vdbmanager.client.SQLProcException::sqlDetail;
  }-*/;
  
  private static native void setSqlDetail(org.teiid.tools.vdbmanager.client.SQLProcException instance, java.lang.String value) 
  /*-{
    instance.@org.teiid.tools.vdbmanager.client.SQLProcException::sqlDetail = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.teiid.tools.vdbmanager.client.SQLProcException instance) throws SerializationException {
    setSqlDetail(instance, streamReader.readString());
    
    com.google.gwt.user.client.rpc.core.java.lang.Exception_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.teiid.tools.vdbmanager.client.SQLProcException instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.teiid.tools.vdbmanager.client.SQLProcException();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.teiid.tools.vdbmanager.client.SQLProcException instance) throws SerializationException {
    streamWriter.writeString(getSqlDetail(instance));
    
    com.google.gwt.user.client.rpc.core.java.lang.Exception_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.teiid.tools.vdbmanager.client.SQLProcException_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.teiid.tools.vdbmanager.client.SQLProcException_FieldSerializer.deserialize(reader, (org.teiid.tools.vdbmanager.client.SQLProcException)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.teiid.tools.vdbmanager.client.SQLProcException_FieldSerializer.serialize(writer, (org.teiid.tools.vdbmanager.client.SQLProcException)object);
  }
  
}
