package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.PointProperties;
import geogebra.common.plugin.EuclidianStyleConstants;

public class PointStyleModel extends OptionsModel {
	private IComboListener listener;
	
	public PointStyleModel(IComboListener listener) {
		this.listener = listener;
	}
	@Override
	public void updateProperties() {
		PointProperties geo0 = (PointProperties) getGeoAt(0);

		if (geo0.getPointStyle() == -1) {
			// select default button
			listener.setSelectedIndex(EuclidianStyleConstants.POINT_STYLE_DOT);
		} else {
			// select custom button and set combo box selection
			listener.setSelectedIndex(geo0.getPointStyle());
		}

	}
	
	public void applyChanges(int value) {
		PointProperties point;
		for (int i = 0; i < getGeosLength(); i++) {
			point = (PointProperties) getGeoAt(0);
			point.setPointStyle(value);
			point.updateRepaint();
		}

	}

	@Override
	public boolean checkGeos() {
		boolean geosOK = true;
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			if (geo.isGeoElement3D()
					|| // TODO add point style to 3D points
					(!geo.getGeoElementForPropertiesDialog().isGeoPoint() && (!(geo
							.isGeoList() && ((GeoList) geo)
							.showPointProperties())))) {
				geosOK = false;
				break;
			}
		}
		return geosOK;

	}

}