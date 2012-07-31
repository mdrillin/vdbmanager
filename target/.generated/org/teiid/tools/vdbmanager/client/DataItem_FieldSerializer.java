package org.teiid.tools.vdbmanager.client;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class DataItem_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getData(org.teiid.tools.vdbmanager.client.DataItem instance) /*-{
    return instance.@org.teiid.tools.vdbmanager.client.DataItem::data;
  }-*/;
  
  private static native void setData(org.teiid.tools.vdbmanager.client.DataItem instance, java.lang.String value) 
  /*-{
    instance.@org.teiid.tools.vdbmanager.client.DataItem::data = value;
  }-*/;
  
  private static native java.lang.String getType(org.teiid.tools.vdbmanager.client.DataItem instance) /*-{
    return instance.@org.teiid.tools.vdbmanager.client.DataItem::type;
  }-*/;
  
  private static native void setType(org.teiid.tools.vdbmanager.client.DataItem instance, java.lang.String value) 
  /*-{
    instance.@org.teiid.tools.vdbmanager.client.DataItem::type = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.teiid.tools.vdbmanager.client.DataItem instance) throws SerializationException {
    setData(instance, streamReader.readString());
    setType(instance, streamReader.readString());
    
  }
  
  public static org.teiid.tools.vdbmanager.client.DataItem instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.teiid.tools.vdbmanager.client.DataItem();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.teiid.tools.vdbmanager.client.DataItem instance) throws SerializationException {
    streamWriter.writeString(getData(instance));
    streamWriter.writeString(getType(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.teiid.tools.vdbmanager.client.DataItem_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.teiid.tools.vdbmanager.client.DataItem_FieldSerializer.deserialize(reader, (org.teiid.tools.vdbmanager.client.DataItem)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.teiid.tools.vdbmanager.client.DataItem_FieldSerializer.serialize(writer, (org.teiid.tools.vdbmanager.client.DataItem)object);
  }
  
}
