package geogebra.web.cas.view;

import geogebra.common.cas.view.CASInputHandler;
import geogebra.common.cas.view.MarbleRenderer;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RowHeaderWidget extends VerticalPanel implements MarbleRenderer {
	private Image marble;
	private boolean oldValue;

	public RowHeaderWidget(CASTableW casTableW, int n, GeoCasCell cell, AppW app) {
		Label label = new Label();
		label.setText(n + "");
		marble = new Image(AppResources.INSTANCE.hidden());
		oldValue = false;
		add(label);
		add(marble);
		if (cell != null)
			CASInputHandler.handleMarble(cell,this);
		marble.addClickHandler(new MarbleClickHandler(cell,this));
		addDomHandler(new RowHeaderHandler(app, casTableW, this), MouseUpEvent.getType());
	}
	
	public void setLabel(String text){
		((Label)(getWidget(0))).setText(text);
	}
	
	public int getIndex(){
		return Integer.parseInt(((Label)(getWidget(0))).getText()) - 1;
	}
	

	public void setMarbleValue(boolean value) {
		if (value == oldValue)
			return;
		marble.setUrl(value ? AppResources.INSTANCE.shown().getSafeUri()
		        : AppResources.INSTANCE.hidden().getSafeUri());
		oldValue = value;

	}

	public void setMarbleVisible(boolean visible) {
		marble.setVisible(visible);

	}

	protected class MarbleClickHandler implements ClickHandler {
		private GeoCasCell cell;
		private RowHeaderWidget rowHeaderWidget;

		protected MarbleClickHandler(GeoCasCell cell, RowHeaderWidget rowHeaderWidget) {
			this.cell = cell;
			this.rowHeaderWidget = rowHeaderWidget;
		}

		public void onClick(ClickEvent event) {
			cell.toggleTwinGeoEuclidianVisible();
			CASInputHandler.handleMarble(cell,rowHeaderWidget);
			event.stopPropagation();
		}
	}

}
