package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.TextProperties;
import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.common.util.StringUtil;

public class TextOptionsModel extends OptionsModel {
	public interface ITextOptionsListener {

		void setWidgetsVisible(boolean showFontDetails, boolean isButton);

		void selectSize(int index);

		void selectFont(int index);

		void selectDecimalPlaces(int index);

		void setSecondLineVisible(boolean noDecimals);

		void selectFontStyle(int style);
		void updatePreview();
	}

	private ITextOptionsListener listener;

	private boolean justDisplayFontSize;

	private String[] fonts = { "Sans Serif", "Serif" };
	private App app;
	private Localization loc;

	public TextOptionsModel(App app, ITextOptionsListener listener) {
		this.listener = listener;
		this.app = app;
		loc = app.getLocalization();
	}

	@Override
	public boolean checkGeos() {
		justDisplayFontSize = true;
		return super.checkGeos();

	}

	@Override
	protected boolean isValidAt(int index) {
		GeoElement geo = getGeoAt(index);

		if ((geo instanceof TextProperties && !((TextProperties) geo)
				.justFontSize()) || geo.isGeoButton()) {
			justDisplayFontSize = false;
		}

		if (!(geo.getGeoElementForPropertiesDialog().isGeoText())) {
			if (!geo.isGeoButton()) {
				return false;

			}	

		}
		return true;
	}

	public TextProperties getTextPropertiesAt(int index) {
		return (TextProperties) getObjectAt(index);
	}
	@Override
	public void updateProperties() {
		listener.setWidgetsVisible(!justDisplayFontSize, getGeoAt(0).isGeoButton());

		TextProperties geo0 = getTextPropertiesAt(0);	

		listener.selectSize(GeoText.getFontSizeIndex(geo0
				.getFontSizeMultiplier())); // font
		// size
		// ranges
		// from
		// -6
		// to
		// 6,
		// transform
		// this
		// to
		// 0,1,..,6
		listener.selectFont(geo0.isSerifFont() ? 1 : 0);
		int selItem = -1;

		int decimals = geo0.getPrintDecimals();
		if (decimals > 0 && decimals < App.decimalsLookup.length
				&& !geo0.useSignificantFigures())
			selItem = App.decimalsLookup[decimals];

		int figures = geo0.getPrintFigures();
		if (figures > 0 && figures < App.figuresLookup.length
				&& geo0.useSignificantFigures())
			selItem = App.figuresLookup[figures];

		listener.selectDecimalPlaces(selItem);
		listener.setSecondLineVisible(getGeoAt(0).isIndependent()
				|| (geo0 instanceof GeoList));



		listener.selectFontStyle(geo0.getFontStyle());

	}
	public void applyFontSizeFromString(String percentStr) {
		double multiplier;
		if (percentStr == null) {
			// Cancel
			return
					;
		}
		percentStr = percentStr.replaceAll("%", "");

		try {
			multiplier = StringUtil.parseDouble(percentStr) / 100;

			if (multiplier < 0.01) {
				multiplier = 0.01;
			} else if (multiplier > 100) {
				multiplier = 100;
			}
		} catch (NumberFormatException e2) {
			app.showError("InvalidInput");
			return;
		}	
		applyFontSize(multiplier);

	}
	public void applyFontSizeFromIndex(int index) {
		applyFontSize(GeoText.getRelativeFontSize(index));
	}
	
	public void applyFontSize(double value) {
		for (int i = 0; i < getGeosLength(); i++) {
			TextProperties text = getTextPropertiesAt(i);
			text.setFontSizeMultiplier(value);
			getGeoAt(i).updateVisualStyleRepaint();
		}

		listener.updatePreview();
	}

	public String[] getFonts() {
		return fonts;
	}

	public String[] getFontSizes() {
		return loc.getFontSizeStrings();
	}

	public void applyFont(boolean isSerif) { 
		for (int i = 0; i < getGeosLength(); i++) {
			TextProperties text = getTextPropertiesAt(i);
			text.setSerifFont(isSerif);
			getGeoAt(i).updateVisualStyleRepaint();
		}
		listener.updatePreview();
	}

	public void applyDecimalPlaces(int decimals) {
		for (int i = 0; i < getGeosLength(); i++) {
			TextProperties text = getTextPropertiesAt(i);
			if (decimals < 8) // decimal places
			{
				// Application.debug("decimals"+roundingMenuLookup[decimals]+"");
				text.setPrintDecimals(
						App.roundingMenuLookup[decimals], true);
			} else // significant figures
			{
				// Application.debug("figures"+roundingMenuLookup[decimals]+"");
				text.setPrintFigures(App.roundingMenuLookup[decimals],
						true);
			}
			((GeoElement) text).updateRepaint();
		}
			listener.updatePreview();
		
	}
	
	public void applyFontStyle(boolean isBold, boolean isItalic) {
		int style = 0;
		if (isBold)
			style += 1;
		if (isItalic)
			style += 2;

		for (int i = 0; i < getGeosLength(); i++) {
			TextProperties text = getTextPropertiesAt(i);
			text.setFontStyle(style);
			((GeoElement) text).updateVisualStyleRepaint();
		}
		
		listener.updatePreview();
		
	}
}
