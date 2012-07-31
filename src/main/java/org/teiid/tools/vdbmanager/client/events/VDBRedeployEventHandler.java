package org.teiid.tools.vdbmanager.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface VDBRedeployEventHandler extends EventHandler {
	
	void onEvent(VDBRedeployEvent event);

}
