package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoFocus;
import geogebra.common.kernel.kernelND.GeoConicND;

/**
 * Focus for 3D conic
 * @author mathieu
 *
 */
public class AlgoFocus3D extends AlgoFocus {

	/**
	 * constructor
	 * @param cons
	 * @param labels
	 * @param c
	 */
	public AlgoFocus3D(Construction cons, String[] labels, GeoConicND c) {
		super(cons, labels, c);
	}
	
	protected void createFocus(Construction cons){
		focus = new GeoPoint3D[2];
		for (int i = 0; i < focus.length; i++) {
			focus[i] = new GeoPoint3D(cons);
		}

	}
	
    @Override
	protected void setCoords(int i, double x, double y){
    	((GeoPoint3D) focus[i]).setCoords(c.getCoordSys().getPoint(x, y));
    }

}
