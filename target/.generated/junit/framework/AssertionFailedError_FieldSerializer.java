package junit.framework;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class AssertionFailedError_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, junit.framework.AssertionFailedError instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.java.lang.Error_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static junit.framework.AssertionFailedError instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new junit.framework.AssertionFailedError();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, junit.framework.AssertionFailedError instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.java.lang.Error_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return junit.framework.AssertionFailedError_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    junit.framework.AssertionFailedError_FieldSerializer.deserialize(reader, (junit.framework.AssertionFailedError)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    junit.framework.AssertionFailedError_FieldSerializer.serialize(writer, (junit.framework.AssertionFailedError)object);
  }
  
}
