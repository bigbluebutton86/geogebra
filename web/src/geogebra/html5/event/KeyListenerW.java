package geogebra.html5.event;

import geogebra.common.euclidian.event.KeyHandler;
import geogebra.common.main.App;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;

public class KeyListenerW implements KeyPressHandler {
	private KeyHandler handler;
	
	public KeyListenerW(KeyHandler handler) {
		this.handler = handler;
	}

	public void onKeyPress(KeyPressEvent e) {
		App.debug("implementation needed"); // TODO
		                                    // Auto-generated
		                                    // method stub

		geogebra.html5.event.KeyEvent event = geogebra.html5.event.KeyEvent
		        .wrapEvent(e);
		handler.keyReleased(event);
		event.release();
	}
}
