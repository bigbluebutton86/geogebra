package geogebra.touch.gui.elements.stylebar;

import geogebra.touch.model.TouchModel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;

class CaptionBar extends FlowPanel {

	private Button[] buttons = { new Button("stylebar_Hidden"),
			new Button("Name"), new Button("NameAndValue"), new Button("Value") };
	private final FlowPanel buttonPanel;

	CaptionBar(final TouchModel touchModel) {
		this.addStyleName("captionBar");

		this.buttonPanel = new FlowPanel();
		this.buttonPanel.setStyleName("styleBarButtonPanel");

		for (int i = 0; i < this.buttons.length; i++) {
			final int index = i;
			this.buttons[i].setText(touchModel.getKernel().getApplication()
					.getLocalization().getPlain(this.buttons[i].getText()));
			this.buttons[i].addDomHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					touchModel.setCaptionMode(index);
				}
			}, ClickEvent.getType());
			this.buttonPanel.add(this.buttons[i]);
		}

		this.add(this.buttonPanel);
	}
}
