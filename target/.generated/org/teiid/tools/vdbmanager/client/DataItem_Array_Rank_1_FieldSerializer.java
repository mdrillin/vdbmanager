package org.teiid.tools.vdbmanager.client;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class DataItem_Array_Rank_1_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.teiid.tools.vdbmanager.client.DataItem[] instance) throws SerializationException {
    com.google.gwt.user.client.rpc.core.java.lang.Object_Array_CustomFieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.teiid.tools.vdbmanager.client.DataItem[] instantiate(SerializationStreamReader streamReader) throws SerializationException {
    int size = streamReader.readInt();
    return new org.teiid.tools.vdbmanager.client.DataItem[size];
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.teiid.tools.vdbmanager.client.DataItem[] instance) throws SerializationException {
    com.google.gwt.user.client.rpc.core.java.lang.Object_Array_CustomFieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.teiid.tools.vdbmanager.client.DataItem_Array_Rank_1_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.teiid.tools.vdbmanager.client.DataItem_Array_Rank_1_FieldSerializer.deserialize(reader, (org.teiid.tools.vdbmanager.client.DataItem[])object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.teiid.tools.vdbmanager.client.DataItem_Array_Rank_1_FieldSerializer.serialize(writer, (org.teiid.tools.vdbmanager.client.DataItem[])object);
  }
  
}
