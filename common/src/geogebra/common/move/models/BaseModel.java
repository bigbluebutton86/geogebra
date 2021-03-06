package geogebra.common.move.models;

import geogebra.common.move.events.BaseEvent;

import java.util.ArrayList;

/**
 * @author gabor
 * Base for all Models (eg. data handling) in Common
 * Sometimes not needed at all.
 */
public class BaseModel {
	
	
	/**
	 * list of used models (for child classes)
	 */
	protected ArrayList modelComponents = null;

	/**
	 * A handler for events. Can be overwritten in derived classes to handle specific events.
	 * 
	 * @param event the event that was triggered.
	 */
	public void onEvent(BaseEvent event) {
		// No default action
	}

}
