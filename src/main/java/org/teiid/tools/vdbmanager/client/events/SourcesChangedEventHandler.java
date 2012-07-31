package org.teiid.tools.vdbmanager.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface SourcesChangedEventHandler extends EventHandler {
	
	void onEvent(SourcesChangedEvent event);

}
