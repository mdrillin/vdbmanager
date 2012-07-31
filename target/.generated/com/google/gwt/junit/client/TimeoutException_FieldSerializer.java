package com.google.gwt.junit.client;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class TimeoutException_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, com.google.gwt.junit.client.TimeoutException instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.java.lang.RuntimeException_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static com.google.gwt.junit.client.TimeoutException instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.google.gwt.junit.client.TimeoutException();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.google.gwt.junit.client.TimeoutException instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.java.lang.RuntimeException_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.google.gwt.junit.client.TimeoutException_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.google.gwt.junit.client.TimeoutException_FieldSerializer.deserialize(reader, (com.google.gwt.junit.client.TimeoutException)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.google.gwt.junit.client.TimeoutException_FieldSerializer.serialize(writer, (com.google.gwt.junit.client.TimeoutException)object);
  }
  
}
