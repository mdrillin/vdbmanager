package org.teiid.tools.vdbmanager.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class SourcesChangedEvent extends GwtEvent<SourcesChangedEventHandler> {

	public static Type<SourcesChangedEventHandler> TYPE = new Type<SourcesChangedEventHandler>();
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<SourcesChangedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SourcesChangedEventHandler handler) {
		handler.onEvent(this);
	}

}
