package org.teiid.tools.vdbmanager.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class PropertyChangedEvent extends GwtEvent<PropertyChangedEventHandler> {

	public static Type<PropertyChangedEventHandler> TYPE = new Type<PropertyChangedEventHandler>();
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<PropertyChangedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(PropertyChangedEventHandler handler) {
		handler.onEvent(this);
	}

}
