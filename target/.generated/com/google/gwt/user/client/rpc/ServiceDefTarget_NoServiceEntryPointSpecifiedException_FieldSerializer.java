package com.google.gwt.user.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ServiceDefTarget_NoServiceEntryPointSpecifiedException_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, com.google.gwt.user.client.rpc.ServiceDefTarget.NoServiceEntryPointSpecifiedException instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.InvocationException_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static com.google.gwt.user.client.rpc.ServiceDefTarget.NoServiceEntryPointSpecifiedException instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.google.gwt.user.client.rpc.ServiceDefTarget.NoServiceEntryPointSpecifiedException();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.google.gwt.user.client.rpc.ServiceDefTarget.NoServiceEntryPointSpecifiedException instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.InvocationException_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.google.gwt.user.client.rpc.ServiceDefTarget_NoServiceEntryPointSpecifiedException_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.google.gwt.user.client.rpc.ServiceDefTarget_NoServiceEntryPointSpecifiedException_FieldSerializer.deserialize(reader, (com.google.gwt.user.client.rpc.ServiceDefTarget.NoServiceEntryPointSpecifiedException)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.google.gwt.user.client.rpc.ServiceDefTarget_NoServiceEntryPointSpecifiedException_FieldSerializer.serialize(writer, (com.google.gwt.user.client.rpc.ServiceDefTarget.NoServiceEntryPointSpecifiedException)object);
  }
  
}
