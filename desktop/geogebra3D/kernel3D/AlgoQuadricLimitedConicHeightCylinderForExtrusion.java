package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoConicND;


/**
 * Extension used for extrusion
 * @author matthieu
 *
 */
public class AlgoQuadricLimitedConicHeightCylinderForExtrusion extends
		AlgoQuadricLimitedConicHeightCylinder implements AlgoForExtrusion{


	/**
	 * 
	 * @param c construction
	 * @param labels labels
	 * @param bottom bottom side
	 * @param height height
	 */
	public AlgoQuadricLimitedConicHeightCylinderForExtrusion(Construction c,
			String[] labels, GeoConicND bottom, NumberValue height) {
		super(c, labels, bottom, height);
	}
	

	private ExtrusionComputer extrusionComputer;
	

	public void setExtrusionComputer(ExtrusionComputer extrusionComputer){
		this.extrusionComputer=extrusionComputer;
	}
	
	
	@Override
	public void compute(){
		super.compute();
		if (extrusionComputer!=null)
			extrusionComputer.compute();
	}


	public GeoElement getGeoToHandle() {
		return getTopFace();
	}

}
