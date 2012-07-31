package org.teiid.tools.vdbmanager.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class VDBRedeployEvent extends GwtEvent<VDBRedeployEventHandler> {

	public static Type<VDBRedeployEventHandler> TYPE = new Type<VDBRedeployEventHandler>();
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<VDBRedeployEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(VDBRedeployEventHandler handler) {
		handler.onEvent(this);
	}

}
