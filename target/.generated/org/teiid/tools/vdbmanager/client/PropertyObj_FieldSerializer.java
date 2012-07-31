package org.teiid.tools.vdbmanager.client;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class PropertyObj_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getDisplayName(org.teiid.tools.vdbmanager.client.PropertyObj instance) /*-{
    return instance.@org.teiid.tools.vdbmanager.client.PropertyObj::displayName;
  }-*/;
  
  private static native void setDisplayName(org.teiid.tools.vdbmanager.client.PropertyObj instance, java.lang.String value) 
  /*-{
    instance.@org.teiid.tools.vdbmanager.client.PropertyObj::displayName = value;
  }-*/;
  
  private static native boolean getIsHidden(org.teiid.tools.vdbmanager.client.PropertyObj instance) /*-{
    return instance.@org.teiid.tools.vdbmanager.client.PropertyObj::isHidden;
  }-*/;
  
  private static native void setIsHidden(org.teiid.tools.vdbmanager.client.PropertyObj instance, boolean value) 
  /*-{
    instance.@org.teiid.tools.vdbmanager.client.PropertyObj::isHidden = value;
  }-*/;
  
  private static native boolean getIsModifiable(org.teiid.tools.vdbmanager.client.PropertyObj instance) /*-{
    return instance.@org.teiid.tools.vdbmanager.client.PropertyObj::isModifiable;
  }-*/;
  
  private static native void setIsModifiable(org.teiid.tools.vdbmanager.client.PropertyObj instance, boolean value) 
  /*-{
    instance.@org.teiid.tools.vdbmanager.client.PropertyObj::isModifiable = value;
  }-*/;
  
  private static native boolean getIsRequired(org.teiid.tools.vdbmanager.client.PropertyObj instance) /*-{
    return instance.@org.teiid.tools.vdbmanager.client.PropertyObj::isRequired;
  }-*/;
  
  private static native void setIsRequired(org.teiid.tools.vdbmanager.client.PropertyObj instance, boolean value) 
  /*-{
    instance.@org.teiid.tools.vdbmanager.client.PropertyObj::isRequired = value;
  }-*/;
  
  private static native java.lang.String getName(org.teiid.tools.vdbmanager.client.PropertyObj instance) /*-{
    return instance.@org.teiid.tools.vdbmanager.client.PropertyObj::name;
  }-*/;
  
  private static native void setName(org.teiid.tools.vdbmanager.client.PropertyObj instance, java.lang.String value) 
  /*-{
    instance.@org.teiid.tools.vdbmanager.client.PropertyObj::name = value;
  }-*/;
  
  private static native java.lang.String getValue(org.teiid.tools.vdbmanager.client.PropertyObj instance) /*-{
    return instance.@org.teiid.tools.vdbmanager.client.PropertyObj::value;
  }-*/;
  
  private static native void setValue(org.teiid.tools.vdbmanager.client.PropertyObj instance, java.lang.String value) 
  /*-{
    instance.@org.teiid.tools.vdbmanager.client.PropertyObj::value = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.teiid.tools.vdbmanager.client.PropertyObj instance) throws SerializationException {
    setDisplayName(instance, streamReader.readString());
    setIsHidden(instance, streamReader.readBoolean());
    setIsModifiable(instance, streamReader.readBoolean());
    setIsRequired(instance, streamReader.readBoolean());
    setName(instance, streamReader.readString());
    setValue(instance, streamReader.readString());
    
  }
  
  public static org.teiid.tools.vdbmanager.client.PropertyObj instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.teiid.tools.vdbmanager.client.PropertyObj();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.teiid.tools.vdbmanager.client.PropertyObj instance) throws SerializationException {
    streamWriter.writeString(getDisplayName(instance));
    streamWriter.writeBoolean(getIsHidden(instance));
    streamWriter.writeBoolean(getIsModifiable(instance));
    streamWriter.writeBoolean(getIsRequired(instance));
    streamWriter.writeString(getName(instance));
    streamWriter.writeString(getValue(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.teiid.tools.vdbmanager.client.PropertyObj_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.teiid.tools.vdbmanager.client.PropertyObj_FieldSerializer.deserialize(reader, (org.teiid.tools.vdbmanager.client.PropertyObj)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.teiid.tools.vdbmanager.client.PropertyObj_FieldSerializer.serialize(writer, (org.teiid.tools.vdbmanager.client.PropertyObj)object);
  }
  
}
