package geogebra3D.euclidian3D;



import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.Hits;
import geogebra.common.euclidian.Previewable;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.euclidian.event.PointerEventType;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.Region;
import geogebra.common.kernel.Matrix.CoordMatrix4x4;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.FromMeta;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumberValue;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.Test;
import geogebra.common.kernel.geos.Transformable;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoConicND.HitType;
import geogebra.common.kernel.kernelND.GeoCoordSys2D;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPlaneND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoQuadricND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.kernel.kernelND.GeoVectorND;
import geogebra.main.AppD;
import geogebra3D.euclidianFor3D.EuclidianControllerFor3D;
import geogebra3D.gui.GuiManager3D;
import geogebra3D.gui.dialogs.DialogManager3D;
import geogebra3D.kernel3D.AlgoIntersectCS1D2D;
import geogebra3D.kernel3D.AlgoIntersectCS1D2D.ConfigLinePlane;
import geogebra3D.kernel3D.AlgoIntersectCS2D2D;
import geogebra3D.kernel3D.GeoConic3D;
import geogebra3D.kernel3D.GeoConicSection;
import geogebra3D.kernel3D.GeoCoordSys1D;
import geogebra3D.kernel3D.GeoPlane3D;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.GeoPolygon3D;
import geogebra3D.kernel3D.GeoQuadric3D;
import geogebra3D.kernel3D.GeoQuadric3DLimited;
import geogebra3D.kernel3D.GeoQuadric3DPart;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Controller for the 3D view
 * @author matthieu
 *
 */
public class EuclidianController3D extends EuclidianControllerFor3D {




	
	
	
	
	
	
	
	
	
	
	/** 3D point that is currently moved */
	//protected GeoPoint3D movedGeoPoint3D = null;
	
	/** min/max values for moving a point */
	private double[] xMinMax, yMinMax, zMinMax;
	
	/** current plane where the movedGeoPoint3D lies */
	protected CoordMatrix4x4 currentPlane = null;
	
	
	/** 3D view controlled by this */
	protected EuclidianView3D view3D; //TODO move to EuclidianViewInterface
	
	
	
	private Point mouseLocOld = new Point();
	private Coords positionOld = new Coords(4);
	
	
	
	/** picking point */
	protected Coords pickPoint;
	
	
	
	/** says that a free point has just been created (used for 3D cursor) */
	private boolean freePointJustCreated = false;
	

	/** says if a rotation of the view occurred (with right-button) */
	private boolean viewRotationOccured = false;
	
	
	/** scale factor for changing angle of view : 2Pi <-> 360 pixels (so 1 pixel = 1° ) */
	static final public double ANGLE_TO_DEGREES = 2*Math.PI/360;
	/** maximum vertical angle */
	static final public int ANGLE_MAX = 90;

	
	/** for animated rotation */
	private double animatedRotSpeed;
	/** used when time is needed */
	private long timeOld;
	/** used to record x information */
	private int xOld;
	
	private Hits3D goodHits;
	
	
	/**
	 * Store infos for intersection curve
	 */
	public class IntersectionCurve{
		protected GeoElement geo1, geo2, result;
		public Drawable3D drawable;
		protected boolean hitted;
		
		/**
		 * constructor
		 * @param geo1 first geo for the intersection
		 * @param geo2 second geo for the intersection
		 * @param result result intersection
		 * @param hitted say if the intersection is hitted
		 * @param drawable drawable for the intersection
		 */
		public IntersectionCurve(GeoElement geo1, GeoElement geo2, GeoElement result, boolean hitted, Drawable3D drawable) {
			this.geo1 = geo1;
			this.geo2 = geo2;
			this.result = result;
			this.hitted = hitted;
			this.drawable = drawable;
		}
		
	}
	
	/**
	 * array list for intersection curves
	 */
	private ArrayList<IntersectionCurve> intersectionCurveList = new ArrayList<IntersectionCurve>();
	//private ArrayList<Drawable3D> intersectionCurves = new ArrayList<Drawable3D>();
	
	//SELECTED GEOS
	/** 2D coord sys (plane, polygon, ...) */
	protected ArrayList<GeoCoordSys2D> selectedCS2D = new ArrayList<GeoCoordSys2D>();
	
	private ArrayList<GeoPolygon3D> selectedPolygons3D = new ArrayList<GeoPolygon3D>();
	
	private ArrayList<GeoPlane3D> selectedPlane = new ArrayList<GeoPlane3D>();
	
	
	
	
	
	/**
	 * common constructor
	 * @param kernel
	 */
	public EuclidianController3D(Kernel kernel) {
		super(kernel);
		
		// inits min max
		xMinMax = new double[2];
		yMinMax = new double[2];
		zMinMax = new double[2];
		
	}
	
	
	/**
	 * sets the view controlled by this
	 * @param view
	 */
	public void setView(EuclidianView3D view) {
		this.view3D = view;
		super.setView(view);
	
	}
	
	
	
	
	
	

	
	
	////////////////////////////////////////////
	// setters movedGeoElement -> movedGeoPoint, ...
	
	private double[] getMinMax(double min, double val, double max){
		
		if (val<min)
			min = val;
		else if (val>max)
			max = val;
		
		return new double[] {min,max};
	}
	
	@Override
	public void setMovedGeoPoint(GeoElement geo){
		
		movedGeoPoint = (GeoPointND) geo;
		Coords coords = movedGeoPoint.getInhomCoordsInD(3);
		
		// sets the min/max values
		double size = movedGeoPoint.getPointSize()*DrawPoint3D.DRAW_POINT_FACTOR/view3D.getScale();
		xMinMax = getMinMax(view3D.getXmin()+size, coords.getX(), view3D.getXmax()-size);
		yMinMax = getMinMax(view3D.getYmin()+size, coords.getY(), view3D.getYmax()-size);
		zMinMax = getMinMax(view3D.getZmin()+size, coords.getZ(), view3D.getZmax()-size);
			

		updateMovedGeoPointStartValues(coords);
		
		view3D.setDragCursor();
	}

	/**
	 * update values needed to move a point
	 * @param coords start point coords
	 */
	protected void updateMovedGeoPointStartValues(Coords coords){
		if (!movedGeoPoint.hasPath() && !movedGeoPoint.hasRegion() ){

			CoordMatrix4x4 plane = CoordMatrix4x4.Identity(); 
			setCurrentPlane(plane);
			//update the moving plane altitude
			getCurrentPlane().set(coords, 4);

		}
	}



	
	////////////////////////////////////////////:
	// moving points
	
	
	
	/**
	 * return the current plane for moving
	 * @return the current plane
	 */
	private CoordMatrix4x4 getCurrentPlane(){
		return currentPlane;
	}

	/**
	 * set the current plane for moving
	 * @param plane a plane
	 */
	private void setCurrentPlane(CoordMatrix4x4 plane){
		currentPlane = plane;
	}

	
	/** set the current plane to the path's moving plane
	 * @param path a path
	 */
	/*
	private void setCurrentPlane(Path3D path){
		Ggb3DMatrix4x4 plane = path.getMovingMatrix(view3D.getToScreenMatrix());			
		view3D.toSceneCoords3D(plane);
		setCurrentPlane(plane);
	}
	*/

	
	/**
	 * moves the point according to the current moving plane and mouse location
	 * @param point the point to move
	 * @param useOldMouse if true, shift the point according to old mouse location
	 */
	private void movePointOnCurrentPlane(GeoPoint3D point, boolean useOldMouse){
		
		// Michael Borcherds
		// move mouse fast, sometimes get mouseLoc = null
		if (mouseLoc == null) return;
		
		//getting current pick point and direction v 
		Coords o;
		if (useOldMouse){
			//if (movePointMode != MOVE_POINT_MODE_XY){
				mouseLocOld = new java.awt.Point(mouseLoc.x,mouseLoc.y);
				positionOld = point.getCoords().copyVector();
				//movePointMode = MOVE_POINT_MODE_XY;
			//}
			o = view3D.getPickFromScenePoint(positionOld,mouseLoc.x-mouseLocOld.x,mouseLoc.y-mouseLocOld.y); 
		}else
			o = view3D.getPickPoint(mouseLoc); 
		view3D.toSceneCoords3D(o);
		
		
		
		//getting new position of the point
		Coords[] projects = o.projectPlaneThruVIfPossible(getCurrentPlane(), view3D.getViewDirection());
		Coords project = projects[0];
		
		
		//min-max x and y values
		checkXYMinMax(project);

	
		point.setCoords(project);
	}
	
	
	private void checkXYMinMax(Coords v){
		//min-max x value
		if (v.getX()>xMinMax[1])
			v.setX(xMinMax[1]);
		else if (v.getX()<xMinMax[0])
			v.setX(xMinMax[0]);
		
		//min-max y value
		if (v.getY()>yMinMax[1])
			v.setY(yMinMax[1]);
		else if (v.getY()<yMinMax[0])
			v.setY(yMinMax[0]);
	}
	
	
	/**
	 * set the mouse information (location and viewing direction in real world coordinates) to the point
	 * @param point a point
	 */
	protected void setMouseInformation(GeoPoint3D point){
		
		// Michael Borcherds
		// move mouse fast, sometimes get mouseLoc = null
		if (mouseLoc == null) return;
		
		Coords o = view3D.getPickPoint(mouseLoc); 
		view3D.toSceneCoords3D(o);
		
		
		point.setWillingCoords(o);
		
			
		point.setWillingDirection(view3D.getViewDirection());
	}
	
	
	
	@Override
	protected void moveTextAbsoluteLocation(){
		Coords o = view3D.getPickPoint(mouseLoc); 
		view3D.toSceneCoords3D(o);
		//o = (o.sub(startPoint3D)).projectPlaneThruVIfPossible(CoordMatrix4x4.IDENTITY, view3D.getViewDirection())[0];
		o = o.projectPlaneThruVIfPossible(CoordMatrix4x4.IDENTITY, view3D.getViewDirection())[0];
		//Application.debug(o);
		//((GeoPoint2) movedGeoText.getStartPoint()).setCoords(o.getX(),o.getY(), 1.0);
		((GeoPoint) movedGeoText.getStartPoint()).setCoords(o.getX()-startPoint3DxOy.getX(),o.getY()-startPoint3DxOy.getY(), 1.0);
	}
	
	
	@Override
	protected void movePoint(boolean repaint, AbstractEvent event){
		
		
		//Application.debug("movePointMode="+movePointMode);
		

		if (movedGeoPoint instanceof GeoPoint3D){
			GeoPoint3D movedGeoPoint3D = (GeoPoint3D) movedGeoPoint;

			if (movedGeoPoint3D.hasPath()){

				setMouseInformation(movedGeoPoint3D);		
				movedGeoPoint3D.doPath();

			}else if (movedGeoPoint3D.hasRegion()){	
				
				setMouseInformation(movedGeoPoint3D);			
				movedGeoPoint3D.doRegion();
				if (movedGeoPoint3D.getRegion()==view3D.getxOyPlane()){
					Coords coords = movedGeoPoint3D.getCoords();
					checkXYMinMax(coords);
					movedGeoPoint3D.setWillingCoords(coords);
					movedGeoPoint3D.setWillingDirection(null);
					movedGeoPoint3D.doRegion();
				}
				view3D.getCursor3D().setMoveNormalDirection(movedGeoPoint3D.getRegionParameters().getNormal());


			}else {


				//if (isShiftDown && mouseLoc != null){ //moves the point along z-axis
				if (movedGeoPoint.getMoveMode() == GeoPointND.MOVE_MODE_Z){ //moves the point along z-axis

					/*
					//getting current pick point and direction v 
					if (movePointMode != MOVE_POINT_MODE_Z){
						mouseLocOld = (Point) mouseLoc.clone();
						positionOld = movedGeoPoint3D.getCoords().copyVector();
						movePointMode = MOVE_POINT_MODE_Z;
					}
					*/
					Coords o = view3D.getPickPoint(mouseLoc); 
					view3D.toSceneCoords3D(o);
					//GgbVector o = view3D.getPickFromScenePoint(positionOld,mouseLoc.x-mouseLocOld.x,mouseLoc.y-mouseLocOld.y);
					//view3D.toSceneCoords3D(o);



					//getting new position of the point
					Coords project = movedGeoPoint3D.getCoords().projectNearLine(o, view3D.getViewDirection(), EuclidianView3D.vz);


					//max z value
					if (project.getZ()>zMinMax[1])
						project.setZ(zMinMax[1]);
					else if (project.getZ()<zMinMax[0])
						project.setZ(zMinMax[0]);


					movedGeoPoint3D.setCoords(project);

					//update the moving plane altitude
					getCurrentPlane().set(movedGeoPoint3D.getCoords(), 4);


				}else{

					movePointOnCurrentPlane(movedGeoPoint3D, false);

				}

				//update point decorations
				view3D.updatePointDecorations(movedGeoPoint3D);
				


			}



			//update 3D cursor coordinates (false : no path or region update)
			view3D.getCursor3D().setCoords(movedGeoPoint3D.getCoords(),false);
			view3D.updateMatrixForCursor3D();

			if (repaint){
				movedGeoPoint3D.updateRepaint();//for highlighting in algebraView
			}else{
				movedGeoPoint3D.updateCascade();//TODO modify movedGeoPoint3D.updateCascade()
			}

			// update previewable
			if (view.getPreviewDrawable() != null) 	
				view.updatePreviewable();
			
			// geo point has been moved
			movedGeoPointDragged = true;
			
		}else{
			Coords o = view3D.getPickPoint(mouseLoc); 
			view3D.toSceneCoords3D(o);
			//TODO do this once
			//GgbVector v = new GgbVector(new double[] {0,0,1,0});
			//view3D.toSceneCoords3D(view3D.getViewDirection());		
			Coords coords = o.projectPlaneThruVIfPossible(CoordMatrix4x4.Identity(), view3D.getViewDirection())[1]; //TODO use current region instead of identity
			xRW = coords.getX(); yRW = coords.getY();
			super.movePoint(repaint,mouseEvent);
			
			view3D.getCursor3D().setCoords(movedGeoPoint.getCoordsInD(3),false);
			
		}
	}





	
	
	
	
	//////////////////////////////////////////////
	// creating a new point
	
	
	@Override
	protected Hits getRegionHits(Hits hits){
		return hits.getHits(Test.REGION3D, tempArrayList);
	}
	
	/**
	 * 
	 * @param point cursor
	 * @return free point from cursor coords
	 */
	private GeoPoint3D getNewPointFree(GeoPointND point){
		GeoPoint3D point3D = (GeoPoint3D) kernel.getManager3D().Point3D(null, 0,0,0, false);
		point3D.setCoords(point);
		point3D.updateCoords();
		view3D.setCursor3DType(EuclidianView3D.PREVIEW_POINT_ALREADY);
		view3D.updateMatrixForCursor3D();
		GeoPoint3D cursor = view3D.getCursor3D(); 
		cursor.setRegion(null);
		cursor.setPath(null);
		cursor.setMoveMode(point3D.getMoveMode());
		if (mode==EuclidianConstants.MODE_POINT || mode==EuclidianConstants.MODE_POINT_ON_OBJECT)
			freePointJustCreated = true;
		return point3D;
	}
	

	
	/**
	 * return a copy of the preview point if one
	 */
	@Override
	protected GeoPointND getNewPoint(Hits hits,
			boolean onPathPossible, boolean inRegionPossible, boolean intersectPossible, 
			 boolean complex) {
				
		GeoPoint3D point = view3D.getCursor3D();
				
		GeoPoint3D point3D;
		GeoPointND ret;
		
		//Application.debug("view3D.getCursor3DType()="+view3D.getCursor3DType());
		
		switch(view3D.getCursor3DType()){		
		case EuclidianView3D.PREVIEW_POINT_FREE:
			pointCreated = true;
			point3D = getNewPointFree(point);
			ret = point3D;
			break;

		case EuclidianView3D.PREVIEW_POINT_PATH:
			pointCreated = true;
			if (onPathPossible){
				Path path = point.getPath();
				if (path.toGeoElement().isGeoElement3D()
						|| (path.toGeoElement().isGeoList() && ((GeoList) path.toGeoElement()).containsGeoElement3D())){
					point3D = (GeoPoint3D) getKernel().getManager3D().Point3D(null,path, false);
					point3D.setWillingCoords(point.getCoords());
					point3D.doPath();
					point3D.setWillingCoords(null);
					point3D.setWillingDirection(null);
					ret = point3D;
				}else{
					Coords coords = point.getCoordsInD(2);
					return createNewPoint2D(null, false, path, coords.getX(), coords.getY(), false, false); 
				}
	
			}else
				return null;
			break;
			
		case EuclidianView3D.PREVIEW_POINT_REGION:
			if (inRegionPossible){
				pointCreated = true;
				Region region = point.getRegion();
				if (region == view3D.getxOyPlane()){
					point3D = getNewPointFree(point);
					ret = point3D;
				}else if (((GeoElement) region).isGeoElement3D()){
					Coords coords = point.getCoords();
					point3D = (GeoPoint3D) getKernel().getManager3D().Point3DIn(null,region,coords,true,false);			
					point3D.doRegion();
					point3D.setWillingCoords(null);
					point3D.setWillingDirection(null);
					ret = point3D;
				}else{
					Coords coords = point.getCoordsInD(2);
					return createNewPoint2D(null, false, region, coords.getX(), coords.getY(), false, false); 
				}
			}else
				return null;
			break;

		case EuclidianView3D.PREVIEW_POINT_DEPENDENT:
			if (intersectPossible){

				pointCreated = true;
				
				//get last intersection preview point
				GeoPointND intersectionPoint = view3D.getIntersectionPoint();
				//add it to construction
				intersectionPoint.getParentAlgorithm().addToConstructionList();
				intersectionPoint.setLabel(null);
				
				//check if it's a 3D point
				if (((GeoElement) intersectionPoint).isGeoElement3D())
					point3D = (GeoPoint3D) intersectionPoint;
				else
					return intersectionPoint;

			}else
				point3D = null;
			return point3D;
			
			
		case EuclidianView3D.PREVIEW_POINT_ALREADY:
			//Application.debug(hits);
			//if (mode==EuclidianView3D.MODE_POINT || mode==EuclidianView3D.MODE_POINT_ON_OBJECT)
			GeoPointND firstPoint = (GeoPointND) hits.getFirstHit(Test.GEOPOINTND);			
			if (firstPoint==null)
				firstPoint = (GeoPointND) getMovedGeoPoint(); //keep current point
			//view3D.getCursor3D().setRegionParameters(firstPoint.getRegionParameters());
			//Application.debug(view3D.getCursor3D().getRegionParameters().getNormal());
			return firstPoint;
		case EuclidianView3D.PREVIEW_POINT_NONE:
		default:
			pointCreated = true;
			return super.getNewPoint(hits, 
					onPathPossible, inRegionPossible, intersectPossible 
					, false);			

		}
		

			
		((GeoElement) ret).update();
		
		//view3D.addToHits3D((GeoElement) ret);

		setMovedGeoPoint(point3D);
		
		view3D.setCursor3DType(EuclidianView3D.PREVIEW_POINT_ALREADY);
		view3D.updateMatrixForCursor3D();
		
		return ret;
	
		

		
	}
	
	
	
	/** put sourcePoint coordinates in point */
	@Override
	protected void createNewPoint(GeoPointND sourcePoint){
		GeoPoint3D point3D = view3D.getCursor3D();
		
		// set coords
		point3D.setCoords(sourcePoint.getCoordsInD(3),false);
		
		// set/remove path/region
		point3D.setPath(sourcePoint.getPath());
		point3D.setRegion(sourcePoint.getRegion());
		

		// update cursor 3D infos
		if (((GeoElement) sourcePoint).isIndependent() || !((GeoElement) sourcePoint).isGeoElement3D())
			point3D.setMoveNormalDirection(EuclidianView3D.vz);
		else if (sourcePoint.hasRegion())
			point3D.setMoveNormalDirection(sourcePoint.getRegionParameters().getNormal());
		view3D.setCursor3DType(EuclidianView3D.PREVIEW_POINT_ALREADY);
		point3D.setMoveMode(sourcePoint.getMoveMode());
		
		//Application.debug("sourcePoint:\n"+sourcePoint.getCoordsInD(3)+"\ncursor:\n"+view3D.getCursor3D().getCoordsInD(3));
	}
	
	/** put intersectionPoint coordinates in point */
	@Override
	protected void createNewPointIntersection(GeoPointND intersectionPoint){
		GeoPoint3D point3D = view3D.getCursor3D();
		point3D.setCoords(intersectionPoint.getCoordsInD(3).getInhomCoords(),false);
		view3D.setCursor3DType(EuclidianView3D.PREVIEW_POINT_DEPENDENT);
		view3D.setIntersectionPoint(intersectionPoint);
		//Application.debug("\nintersectionPoint="+intersectionPoint);
	}
	

	
	/**
	 * create a new free point
	 * or update the preview point
	 */
	@Override
	protected GeoPointND createNewPoint(boolean forPreviewable, boolean complex){
		
		GeoPoint3D point3D;
		
			
		if (!forPreviewable){
			//if there's "no" 3D cursor, no point is created
			if (view3D.getCursor3DType()==EuclidianView3D.PREVIEW_POINT_NONE)
				return null;
			point3D = (GeoPoint3D) kernel.getManager3D().Point3D(null, 0,0,0, false);
		}else{
			point3D = createNewFreePoint(complex);
			if (point3D==null)
				return null;
			point3D.setPath(null);
			point3D.setRegion(null);
			view3D.setCursor3DType(EuclidianView3D.PREVIEW_POINT_FREE);
			return point3D;
		}
		
		setCurrentPlane(CoordMatrix4x4.Identity());
		movePointOnCurrentPlane(point3D, false);	
		
		return point3D;
	}
	
	/**
	 * 
	 * @param complex says if complex coords are wanted
	 * @return new free point (eventually on xOy plane with 2D mouse)
	 */
	protected GeoPoint3D createNewFreePoint(boolean complex){
		return (GeoPoint3D) createNewPoint(true, view3D.getxOyPlane(), complex);
	}
	
	
	/**
	 * create a new path point
	 * or update the preview point
	 */	
	@Override
	protected GeoPointND createNewPoint(boolean forPreviewable, Path path, boolean complex){
			
		GeoPoint3D point3D;
		
		if (!forPreviewable)
			point3D = (GeoPoint3D) getKernel().getManager3D().Point3D(null,path,false);
		else{
			point3D = view3D.getCursor3D();
			point3D.setPath(path);
			point3D.setRegion(null);
			view3D.setCursor3DType(EuclidianView3D.PREVIEW_POINT_PATH);
		}			
		
		setMouseInformation(point3D);
		/*
		if (((GeoElement) path).isGeoList())
			Application.printStacktrace("TODO: path==GeoList");
		else*/
			point3D.doPath();
				
		return point3D;
	}
	
	@Override
	protected boolean createNewPointInRegionPossible(GeoConicND conic){
		
		return conic.getLastHitType() == HitType.ON_FILLING;

	}
	
	/**
	 * create a new region point
	 * or update the preview point
	 */	
	@Override
	protected GeoPointND createNewPoint(boolean forPreviewable, Region region, boolean complex){
		
		GeoPoint3D point3D;
		
		
		point3D = view3D.getCursor3D();			
		point3D.setPath(null);
		point3D.setRegion(region);
		
		setMouseInformation(point3D);
		point3D.doRegion();
		point3D.setMoveNormalDirection(point3D.getRegionParameters().getNormal());
		
		//App.debug(point3D);
		
		if (region==view3D.getxOyPlane()){
			Coords coords = point3D.getInhomCoords();
			if (
					coords.getX()<view3D.getxOyPlane().getXmin()
					||
					coords.getX()>view3D.getxOyPlane().getXmax()
					||
					coords.getY()<view3D.getxOyPlane().getYmin()
					||
					coords.getY()>view3D.getxOyPlane().getYmax()
			){
				view3D.setCursor3DType(EuclidianView3D.PREVIEW_POINT_NONE);
				return null;
			}
		}
		

		view3D.setCursor3DType(EuclidianView3D.PREVIEW_POINT_REGION);


		if (!forPreviewable){
			GeoPoint3D ret = (GeoPoint3D) getKernel().getManager3D().Point3DIn(null,region,false);
			ret.set((GeoElement) point3D);
			//ret.setRegion(region);
			ret.doRegion();
			
			//Application.debug("ici");
			

			return ret;
		}else
			return point3D;
			
	}
	
	/*
	protected void updateMovedGeoPoint(GeoPointND point){
		//movedGeoPoint3D = (GeoPoint3D) point;
		setMovedGeoPoint((GeoPoint3D) point);
	}
	*/
	
	
	
	// tries to get a single intersection point for the given hits
	// i.e. hits has to include two intersectable objects.
	@Override
	protected GeoPointND getSingleIntersectionPoint(Hits hits) {
		//App.debug(hits);

		if (hits.isEmpty() || hits.size() < 2)
			return null;
		
		if(mouseLoc==null)
			return null;
		
		GeoElement a = hits.get(0);
		
		//remove planes containing a (when a is line, conic, or polygon -- notice that a plane containing a line is ever after in hits order)
		
		if (a.isGeoLine()) { // remove planes containing line a
			while (hits.size()>=2) {
				if (hits.get(1).isGeoPlane() &&
						AlgoIntersectCS1D2D.getConfigLinePlane(
						(GeoLineND)a,
						((GeoCoordSys2D)hits.get(1))
								) == ConfigLinePlane.CONTAINED)
					hits.remove(1);
				else
					break;
			}
		} else if (a.isGeoConic()) { // remove planes containing conic a
			while (hits.size()>=2) {
				if (hits.get(1).isGeoPlane() &&
						AlgoIntersectCS2D2D.getConfigPlanePlane(
						(((GeoConicND)a).getCoordSys()),
						(((GeoCoordSys2D)hits.get(1)).getCoordSys())
								) == AlgoIntersectCS2D2D.RESULTCATEGORY_CONTAINED)
					hits.remove(1);
				else
					break;
			}
		} else if (a.isGeoPolygon()) { // remove planes containing polygon a
			while (hits.size()>=2) {
				if (hits.get(1) instanceof GeoCoordSys2D &&
						AlgoIntersectCS2D2D.getConfigPlanePlane(
						(((GeoPolygon)a).getCoordSys()),
						(((GeoCoordSys2D)hits.get(1)).getCoordSys())
								) == AlgoIntersectCS2D2D.RESULTCATEGORY_CONTAINED)
					hits.remove(1);
				else
					break;
			}
		} 
		
		if (hits.size() < 2)
			return null;
		
		//Application.debug(hits.toString());
		GeoElement b = hits.get(1);
		GeoPointND point = null;

		kernel.setSilentMode(true);
		

		// check if a and b are two 2D geos
		if (!a.isGeoElement3D() && !b.isGeoElement3D()){
			//get pick point coords in xOy plane
			Coords project = view3D.getToSceneMatrix().mul(view3D.getPickPoint(mouseLoc))
					.projectPlaneThruVIfPossible(CoordMatrix4x4.IDENTITY, view3D.getViewDirection())[0];
			xRW=project.getX();
			yRW=project.getY();
			
			//apply 2D method
			point = getSingleIntersectionPointFrom2D(a, b, false);	
			
			//App.debug("\npoint="+point+"\nmouse=\n"+project);
		}
		
		//line/line or line/plane  (only one intersection point)
		else if ( (a.isGeoLine() || a instanceof GeoCoordSys2D) && (b.isGeoLine())
    			||(a.isGeoLine() && b instanceof GeoCoordSys2D) ){
    			point = (GeoPoint3D) getKernel().getManager3D().Intersect(null,  a,  b);
    	}
    	//line/conic, line/quadric
    	else if ( a.isGeoLine() ) {
    		if (b.isGeoConic()) {
    			Coords picked = view3D.getPickPoint(mouseLoc);
    			point = getKernel().getManager3D().IntersectLineConicSingle(null, 
    					(GeoLineND)a, (GeoConicND)b, picked.getX() , picked.getY(), view3D.getToScreenMatrix());
    		} else if  (b instanceof GeoQuadric3D) {
    			Coords picked = view3D.getPickPoint(mouseLoc);
    			point = getKernel().getManager3D().IntersectLineQuadricSingle(null, 
    					(GeoLineND)a, (GeoQuadric3D)b, picked.getX() , picked.getY(), view3D.getToScreenMatrix());
    		}
    	} else if ( b.isGeoLine() ) {
    		if (a.isGeoConic()) {
    			Coords picked = view3D.getPickPoint(mouseLoc);
    			point = getKernel().getManager3D().IntersectLineConicSingle(null, 
    					(GeoLineND)b, (GeoConicND)a, picked.getX() , picked.getY(), view3D.getToScreenMatrix());
    		} else if  (a instanceof GeoQuadric3D) {
    			Coords picked = view3D.getPickPoint(mouseLoc);
    			point = getKernel().getManager3D().IntersectLineQuadricSingle(null, 
    					(GeoLineND)b, (GeoQuadric3D)a, picked.getX() , picked.getY(), view3D.getToScreenMatrix());
    		}
    	} else if ( a.isGeoConic() && b.isGeoConic() ) {
    		
    		Coords picked = view3D.getPickPoint(mouseLoc);
			point = getKernel().getManager3D().IntersectConicsSingle(null, 
					(GeoConicND)a, (GeoConicND)b, picked.getX() , picked.getY(), view3D.getToScreenMatrix());
   	
    	}
    	
    	//TODO: conic/plane, conic/quadric
    	
    	
    	kernel.setSilentMode(false);
		
    	//Application.debug("point is defined : "+point.isDefined());
    	
    	if (point==null)
    		return null;
    	
    	
    	
    	

    	if (point.isDefined()){
    		if (((GeoElement) point).isGeoElement3D()){
    			//if the resulting point is defined, but is not around the mouse, discard it. (2011/8/8 Tam)
    			Coords picked = view3D.getPickPoint(mouseLoc);
    			Coords toScreenCoords = view3D.projectOnScreen(((GeoPoint3D) point).getCoords().getCoordsLast1());
 
    			//App.debug("\nmouse="+mouseLoc.x+","+mouseLoc.y+"\npicked=\n"+picked+"\ncoords\n"+toScreenCoords);
        		//App.debug("X: "+Math.abs(picked.getX() - toScreenCoords.getX()) + "\n" +
        		//	"Y: "+Math.abs(picked.getY() - toScreenCoords.getY()));
    			
    			if ( 	Math.abs(picked.getX() - toScreenCoords.getX()) > 15 ||
    					Math.abs(picked.getY() - toScreenCoords.getY()) > 15 ) {
    				return null;
    			}
    		}
        	
    		
    		view3D.setIntersectionThickness(a, b);
    		
    		//Application.printStacktrace("\npoint="+point);
    		
    		point.setCartesian3D();
    		point.update();
    		
    		return point;
    	}
    	
		return null;

	}
	
	
	
	
	
	///////////////////////////////////////
	// creating new objects
	
	
	
	/** return selected points as 3D points
	 * @return selected points
	 */
	final protected GeoPoint3D[] getSelectedPoints3D() {		

		GeoPoint3D[] ret = new GeoPoint3D[selectedPoints.size()];
		getSelectedPointsND(ret);
		
		//Application.printStacktrace("");
		
		return ret;	
	}	
	
	/**
	 * @return selected 3D lines
	 */
	final protected GeoCoordSys1D[] getSelectedLines3D() {
		GeoCoordSys1D[] lines = new GeoCoordSys1D[selectedLines.size()];
		getSelectedLinesND(lines);

		return lines;
	}




	// build polygon	
	/*
	protected void polygon(){
		//check if there is a 3D point
			GeoPointND[] points = getSelectedPointsND();
			
			boolean point3D = false;
			for (int i=0; i<points.length && !point3D; i++)
				point3D = point3D || ((GeoElement) points[i]).isGeoElement3D();
			if (point3D)
				kernel.getManager3D().Polygon3D(null, points);
			else
				kernel.Polygon(null, getSelectedPointsND());
	}
	*/
	
	protected void circleOrSphere(NumberValue num){
		GeoPointND[] points = getSelectedPointsND();	

		getKernel().getManager3D().Sphere(null, points[0], num);
	}
	
	
	/**
	 * get center point and number
	 * @param hits 
	 * @return true if sphere created
	 */
	final protected boolean spherePointRadius(Hits hits) {
		if (hits.isEmpty())
			return false;

		addSelectedPoint(hits, 1, false);		

		// we got the center point
		if (selPoints() == 1) {	
			((DialogManager3D)getDialogManager()).showNumberInputDialogSpherePointRadius(((AppD)app).getMenu(getKernel().getModeText(mode)),
					getSelectedPointsND()[0]);
			return true;
		}
		return false;
	}
	
	
	/**
	 * get center point and number
	 * @param hits 
	 * @return true if cone created
	 */
	final protected boolean coneTwoPointsRadius(Hits hits) {
		if (hits.isEmpty())
			return false;

		addSelectedPoint(hits, 2, false);		

		// we got the center point
		if (selPoints() == 2) {	
			GeoPointND[] points = getSelectedPointsND();
			((DialogManager3D)getDialogManager()).showNumberInputDialogConeTwoPointsRadius(((AppD)app).getMenu(getKernel().getModeText(mode)),
					points[0],points[1]);
			return true;
		}
		return false;
	}
	

	/**
	 * get center point and number
	 * @param hits 
	 * @return true if cylinder created
	 */
	final protected boolean cylinderTwoPointsRadius(Hits hits) {
		if (hits.isEmpty())
			return false;

		addSelectedPoint(hits, 2, false);		

		// we got the center point
		if (selPoints() == 2) {	
			GeoPointND[] points = getSelectedPointsND();
			((DialogManager3D)getDialogManager()).showNumberInputDialogCylinderTwoPointsRadius(((AppD)app).getMenu(getKernel().getModeText(mode)),
					points[0],points[1]);
			return true;
		}
		return false;
	}
	

	
	/**
	 * get point and line or vector;
	// create plane through point orthogonal to line or vector
	 * 
	 * @param hits
	 * @return orthogonal plane
	 */
	final protected GeoElement[] orthogonalPlane(Hits hits) {
		if (hits.isEmpty())
			return null;
		
		boolean hitPoint = (addSelectedPoint(hits, 1, false) != 0);
		if (!hitPoint) {
			if (selLines() == 0) {
				addSelectedVector(hits, 1, false);
			}
			if (selVectors() == 0) {
				addSelectedLine(hits, 1, false);
			}
		}

		if (selPoints() == 1) {
			if (selVectors() == 1) {
				// fetch selected point and vector
				GeoPointND[] points = getSelectedPointsND();
				GeoVectorND[] vectors = getSelectedVectorsND();
				// create new plane
				GeoElement[] ret = { null };
				ret[0] = (GeoPlane3D) getKernel().getManager3D().OrthogonalPlane3D(null, points[0], vectors[0]);
				return ret;

			} else if (selLines() == 1) {
				// fetch selected point and line
				GeoPointND[] points = getSelectedPointsND();
				GeoLineND[] lines = getSelectedLinesND();
				// create new plane
				GeoElement[] ret = { null };
				ret[0] = (GeoPlane3D) getKernel().getManager3D().OrthogonalPlane3D(null, points[0], lines[0]);
				return ret;
			}
		}
		return null;
	}


	/**
	 * get axis and point
	 * create circle with axis and through the point
	 * @param hits 
	 * @return true if circle created
	 * 
	 */
	final protected boolean circleAxisPoint(Hits hits) {
		if (hits.isEmpty())
			return false;
		
		
		addSelectedPoint(hits, 1, false);
		addSelectedLine(hits, 1, false);


		if (selPoints() == 1 && selLines() == 1) {
			getKernel().getManager3D().Circle3D(null, getSelectedLinesND()[0], getSelectedPointsND()[0]);

			return true;

		} 
		
		return false;
		
	}	
	
	/**
	 * get point, direction, enter radius
	 * create circle with center, radius, axis parallel to direction
	 * @param hits 
	 * @return true if circle created
	 * 
	 */
	final protected boolean circlePointRadiusDirection(Hits hits) {
		if (hits.isEmpty())
			return false;
		
		
		if (addSelectedPoint(hits, 1, false)==0)
			addSelectedDirection(hits, 1, false);


		if (selPoints() == 1 && selDirections() == 1) {
			((DialogManager3D) (app).getDialogManager()).showNumberInputDialogCirclePointDirectionRadius(app.getMenu(getKernel().getModeText(mode)),
					getSelectedPointsND()[0],getSelectedDirections()[0]);

			return true;

		} 
		
		return false;
		
	}
	
	private TextDispatcher3D textDispatcher; 
	
	@Override
	protected TextDispatcher3D getTextDispatcher(){
		if(textDispatcher == null){
			textDispatcher = new TextDispatcher3D(kernel, view3D);
		}
		return textDispatcher;
	}
	/**
	 * 
	 * @param hits geos hitted
	 * @return volume of a geo (from hits) that has a volume
	 */
	final protected boolean volume(Hits hits) {
		if (hits.isEmpty())
			return false;
		
		addSelectedGeo(hits.getFiniteVolumeIncludingMetaHits(), 1, false);

		if (selGeos() == 1) {
			GeoElement hasVolume = getSelectedGeos()[0];
			getTextDispatcher().createVolumeText(hasVolume, mouseLoc);

			return true;

		} 
		
		return false;
		
	}

	
	
	
	
	
	/** 
	 * create plane containing polygon / 2 lines / line & point / 3 points
	 * 
	 * @param hits0 hits
	 * @return true if a plane has been created
	 */
	final protected boolean planeContaining(Hits hits0) {
		
		// keep only one type between points/lines/2D coord sys
		Hits hits = hits0.keepFirsts(Test.GEOPOINTND, Test.GEOLINEND, Test.GEOCOORDSYS2DNOTPLANE);
		
		//App.debug("\n=================\n"+hits0+"\n====\n"+hits+"\n=================\n");
		
		if (hits.isEmpty())
			return false;
		
		// first try with polygon, conic, etc.
		if (selPoints() == 0 && selLines() == 0) {
			addSelectedCS2D(hits, 1, false);
		}
		
		if (selCS2D() == 1){
			GeoCoordSys2D[] cs = getSelectedCS2D();
			getKernel().getManager3D().Plane3D(null, cs[0]);
			return true;
		}
		
		
		// then try with points 
		addSelectedPoint(hits, 3, false);

		if (selPoints() == 3) { // 3 points
			GeoPointND[] points = getSelectedPointsND();
			getKernel().getManager3D().Plane3D(null, points[0], points[1], points[2]);
			return true;
		
		}else if (selPoints() == 1) { // try point & line
			// only one line allowed
			addSelectedLine(hits, 1, false);
			if (selLines() == 1) {
				// fetch selected point and line
				GeoPointND[] points = getSelectedPointsND();
				GeoLineND[] lines = getSelectedLinesND();
				// create new plane
				getKernel().getManager3D().Plane3D(null, points[0], lines[0]);
				return true;
			}
			
		}else if (selPoints() == 0){ // maybe two lines
			addSelectedLine(hits, 2, false);
			if (selLines() == 2) {
				// plane containing two lines
				GeoLineND[] lines = getSelectedLinesND();
				getKernel().getManager3D().Plane3D(null, lines[0], lines[1]);
				return true;
			}
		}

		return false;
	}

	/**
	 * process view in front of mode
	 * @param hits
	 * @return false (kernel won't change)
	 */
	final protected boolean viewInFrontOf(Hits hits) {
		if (hits.isEmpty())
			return false;
		
		//Application.debug(hits);
		
		addSelectedGeo(hits.getTopHits(), 1, false);//TODO hits.getTopHits() ?

		

		if (selGeos() == 1){ //clear selection
			GeoElement geo = getSelectedGeos()[0];

			//GeoElement geo = (GeoElement) hits.get(0);
			//Application.debug(view3D.hasMouse());
			Coords vn = geo.getMainDirection();
			if (vn!=null){
				if (view3D.hasMouse())
					view3D.setRotAnimation(view3D.getCursor3D().getDrawingMatrix().getVz());
				else {//doesn't come from 3D view
					view3D.setClosestRotAnimation(vn);
				}
			}
		}
		
		return false;
	}
	
	/** get point and plane;
	 * create line through point parallel to plane
	 * 
	 * @param hits
	 * @return true if a plane has been created
	 */
	final protected boolean parallelPlane(Hits hits) {
		
		//Application.debug(hits.toString());
		
		if (hits.isEmpty())
			return false;

		boolean hitPoint = (addSelectedPoint(hits, 1, false) != 0);
		if (!hitPoint) {
			addselectedCS2D(hits, 1, false);
		}

		if (selPoints() == 1) {
			if (selCoordSys2D() == 1) {
				// fetch selected point and vector
				GeoPointND[] points = getSelectedPointsND();
				GeoCoordSys2D[] cs = getselectedCS2D();//TODO
				// create new plane
				getKernel().getManager3D().Plane3D(null, points[0], cs[0]);
				return true;
			}
		}
		return false;
	}

	
	/** get basis and height;
	 * create prism/cylinder
	 * 
	 * @param hits
	 * @return true if a prism has been created
	 */
	final protected boolean extrusionOrConify(Hits hits) {
		
		
		//App.printStacktrace(hits);
		//App.error(""+selNumberValues());
		
		if (hits.isEmpty())
			return false;


		int basisAdded = addSelectedPolygon(hits, 1, false);
		basisAdded += addSelectedConic(hits, 1, false);
		
		if (basisAdded == 0){ // if polygon/conic has been added, the height will be entered through dialog manager
			addSelectedNumberValue(hits, 1, false);
		}
		
		/*
		s+="\nAprès=\n";
		for (int i=0;i<selectedPolygons.size();i++)
			s+=selectedPolygons.get(i)+"\n";
		s+="\nNumeric=\n";
		for (int i=0;i<selectedNumberValues.size();i++)
			s+=selectedNumberValues.get(i)+"\n";
		
		if (!selectionPreview)
			Application.debug(s);
		*/

		if (selNumberValues() == 1) {
			if (selPolygons() == 1) {
				GeoPolygon[] basis = getSelectedPolygons();
				NumberValue[] height = getSelectedNumberValues();
				if(mode==EuclidianConstants.MODE_EXTRUSION)
					getKernel().getManager3D().Prism(null, basis[0], height[0]);
				else
					getKernel().getManager3D().Pyramid(null, basis[0], height[0]);
				return true;
			}else if (selConics() == 1) {
				GeoConicND[] basis = getSelectedConicsND();
				GeoNumeric[] height = getSelectedNumbers();
				if(mode==EuclidianConstants.MODE_EXTRUSION)
					getKernel().getManager3D().CylinderLimited(null, basis[0], height[0]);
				else
					getKernel().getManager3D().ConeLimited(null, basis[0], height[0]);
				return true;
			}
		}
		return false;
	}
	
	
	
	private GeoPointND[] pyramidBasis = null;
	
	
	/** get basis and top point;
	 * create pyramid
	 * 
	 * @param hits hits
	 * @return true if a prism has been created
	 */
	final protected boolean pyramidOrPrism(Hits hits) {
		
		//if (pyramidBasis!=null) Application.debug(pyramidBasis.length);
		
		if (hits.isEmpty())
			return false;

		if (pyramidBasis==null){ //try to find/create a polygon
			
			if (selPoints() < 2) //already two points : not a polygon for basis
				addSelectedPolygon(hits, 1, false);

			if (selPolygons() == 0) { //try to create a polygon
				// if the first point is clicked again, we create a polygon
				if (selPoints() > 2) {
					// check if first point was clicked again
					boolean finished = !selectionPreview
							&& hits.contains(selectedPoints.get(0));
					if (finished) {
						// store basis 
						((DrawPolygon3D) view3D.getPreviewDrawable()).freezePreview();
						pyramidBasis = getSelectedPointsND();
						return false;
					}
				}

				addSelectedPoint(hits, GeoPolygon.POLYGON_MAX_POINTS, false);
				return false; //no polygon
			}
			

			// there is 1 polygon, look for top point
			addSelectedPoint(hits, 1, false);
			
			if (selPoints() == 1) {
				// fetch selected point and vector
				GeoPolygon[] basis = getSelectedPolygons();
				GeoPointND[] points = getSelectedPointsND();
				// create new pyramid or prism
				switch (mode){
				case EuclidianConstants.MODE_PYRAMID:
					getKernel().getManager3D().Pyramid(null, basis[0], points[0]);
					break;
				case EuclidianConstants.MODE_PRISM:
					getKernel().getManager3D().Prism(null, basis[0], points[0]);
					break;
				}
				return true;
			}
			
			
			
		}else{ //there are points for basis
			
			addSelectedPoint(hits, 1, false);
			
			if (selPoints() == 1) {
				// fetch selected point and vector
				GeoPointND[] points = new GeoPointND[pyramidBasis.length+1];
				for (int i=0; i<pyramidBasis.length; i++)
					points[i]=pyramidBasis[i];
				points[pyramidBasis.length] = getSelectedPointsND()[0];
				// create new pyramid or prism
				switch (mode){
				case EuclidianConstants.MODE_PYRAMID:
					getKernel().getManager3D().Pyramid(null, points);
					break;
				case EuclidianConstants.MODE_PRISM:
					getKernel().getManager3D().Prism(null, points);
					break;
				}				
				pyramidBasis=null;
				return true;
			}
			
			return false;
		}
			
		
		
		

		
		
		return false;
		
	}
	
	
	///////////////////////////////////////////
	// moved GeoElements
	
	@Override
	public GeoElement getMovedGeoPoint(){
		return (GeoElement) movedGeoPoint;
	}
	
	
	
	///////////////////////////////////////////
	// mouse released
	
	@Override
	public void wrapMouseReleased(AbstractEvent e) {
		if (!draggingOccured && !app.isControlDown(e))
			view3D.switchMoveCursor();
		
		super.wrapMouseReleased(e);
	}
	
	@Override
	protected void processReleaseForMovedGeoPoint(boolean rightClick){
		
		
		
		((EuclidianView3D) view).updatePointDecorations(null);
		
		
		if (mode==EuclidianConstants.MODE_POINT 
				|| mode==EuclidianConstants.MODE_POINT_ON_OBJECT
				|| mode==EuclidianConstants.MODE_MOVE
		){
			if(freePointJustCreated)
				//avoid switch if the point is created by a click
				freePointJustCreated=false;
			else{
				//switch the direction of move (xy or z) in case of left-click
				//if (!movedGeoPointDragged){
				if (!draggingOccured && !rightClick){
					movedGeoPoint.switchMoveMode();
					((EuclidianView3D) view).getCursor3D().setMoveMode(movedGeoPoint.getMoveMode());
					//Application.debug(movedGeoPoint+"\nmove mode="+movedGeoPoint.getMoveMode()+"\ncursor move mode="+((EuclidianView3D) view).getCursor3D().getMoveMode());
					((EuclidianView3D) view).setDefaultCursorWillBeHitCursor();
				}
			}
		}
		
		if (movedGeoPoint instanceof GeoPoint3D){
			GeoPoint3D movedGeoPoint3D = (GeoPoint3D) movedGeoPoint;
			movedGeoPoint3D.setWillingCoords(null);
			movedGeoPoint3D.setWillingDirection(null);
		}
		
		super.processReleaseForMovedGeoPoint(rightClick);
		
	}

	
	///////////////////////////////////////////
	// mouse moved
	
	
	protected boolean mouseMoved = false;
	//private boolean mousePressed = false;
	protected AbstractEvent mouseEvent = null;
	
	@Override
	protected void processMouseMoved(AbstractEvent e) {	
		
		view3D.setHits3D(mouseLoc);	
		

		
		//for next mouse move process
		mouseEvent = e;
		mouseMoved = true;
		
		
		
	}
	
	/**
	 * set flag "mouseMoved" true
	 */
	public void setFlagMouseMoved(){
		mouseMoved = true;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		mouseMoved = false;
		//mousePressed = true;
		super.mousePressed(e);
	}
	
	/*
	public void mouseReleased(MouseEvent e) {	
		mousePressed = false;
		super.mouseReleased(e);
	}
	
	public void mouseClicked(MouseEvent e) {	
		mousePressed = false;
		super.mouseClicked(e);
	}
	*/
	
	/**
	 * update mouse moved, 3D mouse values, etc.
	 */
	public void updateInput3D(){
		// no input 3D
	}
	
	/**
	 * update mouse moved after picking
	 */
	public void update(){
		processMouseMoved();
	}
	
	/**
	 * tells to proceed mouseMoved() (for synchronization with 3D renderer)
	 */
	public void processMouseMoved(){
		
		
		if (mouseMoved && view3D.hasMouse()){
			
			((EuclidianView3D) view).updateCursor3D();
			super.processMouseMoved(mouseEvent);
			
			mouseMoved = false;
		}
	}
	
	
	
	@Override
	protected void initNewMode(int mode) {
				
		super.initNewMode(mode);
		
	}

	@Override
	protected Previewable switchPreviewableForInitNewMode(int mode){

		Previewable previewDrawable = null;
		
		//maybe set previously by MODE_INTERSECTION_CURVE
		hideIntersection = false;
		
		//App.debug(mode);
		
		switch (mode) {

		case EuclidianConstants.MODE_SPHERE_TWO_POINTS:
			previewDrawable = view3D.createPreviewSphere(selectedPoints);
			break;
			
		case EuclidianConstants.MODE_EXTRUSION:
			previewDrawable = view3D.createPreviewExtrusion(selectedPolygons,selectedConicsND);
			break;
			
		case EuclidianConstants.MODE_CONIFY:
			previewDrawable = view3D.createPreviewConify(selectedPolygons,selectedConicsND);
			break;
			
		case EuclidianConstants.MODE_PYRAMID:
		case EuclidianConstants.MODE_PRISM:
			previewDrawable = view3D.createPreviewPyramid(selectedPoints);
			break;
			
		case EuclidianConstants.MODE_INTERSECTION_CURVE: // line through two points
			break;
			
		default:
			previewDrawable = super.switchPreviewableForInitNewMode(mode);
			break;
		}
		
		return previewDrawable;

	}

	@Override
	public void clearSelections() {
		clearSelection(selectedCS2D);
		clearSelection(selectedCS1D);
		clearSelection(selectedPolygons3D);
		clearSelection(selectedQuadric);
		super.clearSelections();
	}
	
	//not only moveable hits are selected in move mode
	@Override
	protected boolean getSelectables(Hits hits) {	
		Hits top = hits.getTopHits(1);
		
		super.getSelectables(top);
		
		//display correctly oriented 3D cursor
		GeoPointND point = (GeoPointND) top.getFirstHit(Test.GEOPOINTND);
		if (point!=null)
			view3D.updateCursor3D(hits);

		
		return false;
	}
	
	
	/*
	protected void mouseClickedMode(MouseEvent e, int mode){
		

		switch (mode) {
		case EuclidianView3D.MODE_VIEW_IN_FRONT_OF:
			//Application.debug("ici");
			Hits hits = view.getHits().getTopHits();
			if(!hits.isEmpty()){
				GeoElement geo = (GeoElement) view.getHits().getTopHits().get(0);
				Coords vn = geo.getMainDirection();
				if (vn!=null){
					view3D.setRotAnimation(view3D.getCursor3D().getDrawingMatrix().getVz());
				}
			}
			
			break;
			default:
				super.mouseClickedMode(e,mode);
		}
	}
	*/

	
	//TODO
	@Override
	public void processModeLock(){}

	///////////////////////////////////////////
	// EMPTY METHODS IN EuclidianController USED FOR EuclidianView3D	
	




	/** right-press the mouse makes start 3D rotation */
	@Override
	protected void processRightPressFor3D(){
		
		if (view3D.isRotAnimated()){
			view3D.stopRotAnimation();
			viewRotationOccured = true;
		}
		
		//remembers mouse location
		startLoc = mouseLoc;
		view.rememberOrigins();
		view.setDefaultCursor();
		
		timeOld = System.currentTimeMillis();
		xOld = startLoc.x;
		animatedRotSpeed = 0;

	}
	

	

	
	/** right-drag the mouse makes 3D rotation 
	 * @return true*/
	@Override
	protected boolean processRotate3DView(){

		long time = System.currentTimeMillis();
		int x = mouseLoc.x;
		animatedRotSpeed = (double) (x-xOld)/(time-timeOld);
		timeOld = time; xOld = x;
		//Application.debug("vRot="+vRot);
		view.setCoordSystemFromMouseMove(mouseLoc.x - startLoc.x, 
				mouseLoc.y - startLoc.y, 
				MOVE_ROTATE_VIEW);
		viewRotationOccured = true;
		return true;
	}
	
	
	/** right-release the mouse makes stop 3D rotation 
	 * @return true if a rotation occured */
	@Override
	protected boolean processRightReleaseFor3D(PointerEventType type){
		
		if (viewRotationOccured){
			viewRotationOccured = false;
			view.setHits(mouseLoc, type);
			//Application.debug("hits"+view.getHits().toString());
			((EuclidianView3D) view).updateCursor3D();
			
			view.setHitCursor();
			app.storeUndoInfo();
			

			((EuclidianView3D) view).setRotContinueAnimation(
					System.currentTimeMillis()-timeOld,
					animatedRotSpeed);
			
			//Application.debug("animatedRotSpeed="+animatedRotSpeed);
			
			return true;
		}
		
		return false;
	}
	
	
	///////////////////////////////////////////
	// PROCESS MODE
	
	@Override
	protected boolean switchModeForProcessMode(Hits hits, boolean isControlDown){
		
		boolean changedKernel = false;
		
		GeoElement[] ret = null;
		
		switch (mode) {
		case EuclidianConstants.MODE_INTERSECTION_CURVE:
			changedKernel = intersectionCurve(hits); 
			if (changedKernel){ // remove current intersection curve
				intersectionCurveList.remove(resultedIntersectionCurve);
				view3D.setPreview(null);
			}
			break;
		case EuclidianConstants.MODE_PLANE_THREE_POINTS:
			changedKernel = (threePoints(hits,mode) != null);
			break;
		case EuclidianConstants.MODE_PLANE:
			changedKernel = planeContaining(hits);
			break;		
		
		case EuclidianConstants.MODE_ORTHOGONAL_PLANE:
			changedKernel = (orthogonalPlane(hits) != null);
			break;
			
		case EuclidianConstants.MODE_PARALLEL_PLANE:
			changedKernel = parallelPlane(hits);
			break;
			
		case EuclidianConstants.MODE_EXTRUSION:	
		case EuclidianConstants.MODE_CONIFY:
			changedKernel = extrusionOrConify(hits);
			break;
			
		case EuclidianConstants.MODE_PYRAMID:
		case EuclidianConstants.MODE_PRISM:
			changedKernel = pyramidOrPrism(hits);
			break;
			
		case EuclidianConstants.MODE_SPHERE_TWO_POINTS:	
			changedKernel = (circleOrSphere2(hits, mode) != null);
			break;
		case EuclidianConstants.MODE_SPHERE_POINT_RADIUS:
			changedKernel = spherePointRadius(hits);
			break;
			
		case EuclidianConstants.MODE_CONE_TWO_POINTS_RADIUS:
			changedKernel = coneTwoPointsRadius(hits);
			break;
		
		case EuclidianConstants.MODE_CYLINDER_TWO_POINTS_RADIUS:
			changedKernel = cylinderTwoPointsRadius(hits);
			break;
		
			
			
		case EuclidianConstants.MODE_VIEW_IN_FRONT_OF:
			changedKernel = viewInFrontOf(hits);
			break;

		case EuclidianConstants.MODE_CIRCLE_AXIS_POINT:
			changedKernel = circleAxisPoint(hits);
			break;

		case EuclidianConstants.MODE_CIRCLE_POINT_RADIUS_DIRECTION:
			changedKernel = circlePointRadiusDirection(hits);
			break;
			
		case EuclidianConstants.MODE_MIRROR_AT_PLANE:
			ret = mirrorAtPlane(hits.getTopHits());
			break;

		case EuclidianConstants.MODE_ROTATE_AROUND_LINE:
			ret = rotateAroundLine(hits.getTopHits());
			break;

			
		case EuclidianConstants.MODE_VOLUME:
			changedKernel = volume(hits);
			break;

		default:
			changedKernel = super.switchModeForProcessMode(hits, isControlDown);
			break;
		}
		
		memorizeJustCreatedGeosAfterProcessMode(ret);
		
		if (!changedKernel) {
			return ret != null;
		}
	
		return changedKernel;
		
	}
	
	
	
	/**
	 * for some modes, polygons are not to be removed
	 * @param hits
	 */
	@Override
	protected void switchModeForRemovePolygons(Hits hits){
		
		switch (mode){
		case EuclidianConstants.MODE_PARALLEL_PLANE:
			((Hits3D) hits).removePolygonsIfNotOnlyCS2D();
			break;
		case EuclidianConstants.MODE_PYRAMID:
		case EuclidianConstants.MODE_PRISM:
		case EuclidianConstants.MODE_AREA:
		case EuclidianConstants.MODE_VOLUME:
		case EuclidianConstants.MODE_EXTRUSION:
		case EuclidianConstants.MODE_CONIFY:
			hits.removeAllPolygonsButOne();
			break;
		case EuclidianConstants.MODE_INTERSECTION_CURVE:
			break;
		case EuclidianConstants.MODE_PLANE:
			break;
		default:
			super.switchModeForRemovePolygons(hits);
		}
	}
	
	
	@Override
	protected GeoElement[] switchModeForThreePoints(int mode){
		
		switch (mode) {
		case EuclidianConstants.MODE_PLANE_THREE_POINTS:
			GeoPointND[] points = getSelectedPointsND();
			GeoPlane3D ret0 = (GeoPlane3D)getKernel().getManager3D().Plane3D(null, points[0], points[1], points[2]);
			GeoElement[] ret = { ret0 };
			return ret;
		default:
			return super.switchModeForThreePoints(mode);

		}

	}

	@Override
	protected GeoElement[] switchModeForCircleOrSphere2(int mode){
		
		
		switch (mode) {
		case EuclidianConstants.MODE_SPHERE_TWO_POINTS:
			GeoPointND[] points = getSelectedPointsND();
			GeoElement[] ret = { null };
			ret[0] = getKernel().getManager3D().Sphere(null, points[0], points[1]);
			return ret;
		default:
			return super.switchModeForCircleOrSphere2(mode);
		}
	}

	
	///////////////////////////////////////////
	// MOUSE PRESSED
	
	@Override
	protected void createNewPointForModePoint(Hits hits, boolean complex){
		//super.createNewPointForModePoint(hits, false);
		createNewPoint(hits, true, true, true, true, false);
	}
	
	@Override
	protected void createNewPointForModeOther(Hits hits){
		createNewPoint(hits, true, true, true, true, false);
	}

	
	@Override
	protected void switchModeForMousePressed(AbstractEvent e){

		Hits hits;
		PointerEventType type = e.getType();
		switch (mode) {
		case EuclidianConstants.MODE_PLANE_THREE_POINTS:	
		case EuclidianConstants.MODE_SPHERE_TWO_POINTS:	
		case EuclidianConstants.MODE_SPHERE_POINT_RADIUS:	
		case EuclidianConstants.MODE_CONE_TWO_POINTS_RADIUS:	
		case EuclidianConstants.MODE_CYLINDER_TWO_POINTS_RADIUS:	
			view.setHits(mouseLoc, type);
			hits = view.getHits();hits.removePolygons();
			createNewPoint(hits, true, true, true, true, false);
			break;
			
		case EuclidianConstants.MODE_ORTHOGONAL_PLANE:
			view.setHits(mouseLoc, type);
			hits = view.getHits();hits.removePolygons();
			createNewPoint(hits, false, false, true);
			break;	
			
		case EuclidianConstants.MODE_PLANE:
			view.setHits(mouseLoc, type);
			hits = view.getHits();
			break;	
			
		case EuclidianConstants.MODE_PARALLEL_PLANE:
			view.setHits(mouseLoc, type);
			hits = view.getHits();hits.removePolygons();
			createNewPoint(hits, true, false, false, true, false);
			break;	
			
		case EuclidianConstants.MODE_EXTRUSION:
		case EuclidianConstants.MODE_CONIFY:
			view.setHits(mouseLoc, type);
			hits = view.getHits();
			hits.removeAllPlanes();
			switchModeForRemovePolygons(hits);
			//Application.debug(hits.toString());
			extrusionOrConify(hits);
			view3D.updatePreviewable();
			break;
		
		
		
			
		case EuclidianConstants.MODE_PYRAMID:
		case EuclidianConstants.MODE_PRISM:
			view.setHits(mouseLoc, type);
			hits = view.getHits();
			switchModeForRemovePolygons(hits);
			createNewPoint(hits, true, false, false, true, false);
			break;
			
			
		case EuclidianConstants.MODE_ROTATEVIEW:
			startLoc = mouseLoc; 
			view.rememberOrigins();
			moveMode = MOVE_ROTATE_VIEW;
			break;
			
		case EuclidianConstants.MODE_CIRCLE_AXIS_POINT:	
		case EuclidianConstants.MODE_CIRCLE_POINT_RADIUS_DIRECTION:	
			view.setHits(mouseLoc, type);
			hits = view.getHits();
			hits.removePolygons();
			if (hits.size() == 0)
				createNewPoint(hits, false, true, true);
			break;
		case EuclidianConstants.MODE_INTERSECTION_CURVE:
			//no need to do anything for preview when mouse is pressed
			break;
		case EuclidianConstants.MODE_VOLUME:
			view.setHits(mouseLoc, type);
			hits = view.getHits();
			break;
		default:
			super.switchModeForMousePressed(e);
		}
		
	}
	
	
	
	
	///////////////////////////////////////////
	// MOUSE RELEASED
	
	@Override
	protected boolean switchModeForMouseReleased(int mode, Hits hits, boolean changedKernel, boolean control, PointerEventType type){
		switch (mode) {
		case EuclidianConstants.MODE_PARALLEL_PLANE:
			return true;
		case EuclidianConstants.MODE_EXTRUSION:
			((DrawExtrusionOrConify3D) view3D.getPreviewDrawable()).createPolyhedron();
			return true;
			
		case EuclidianConstants.MODE_CONIFY:
			((DrawExtrusionOrConify3D) view3D.getPreviewDrawable()).createPolyhedron();
			return true;
			
			
		case EuclidianConstants.MODE_PYRAMID:
		case EuclidianConstants.MODE_PRISM:
			return true;
			
		case EuclidianConstants.MODE_MIRROR_AT_PLANE:
		case EuclidianConstants.MODE_ROTATE_AROUND_LINE:
			return true;
			
		case EuclidianConstants.MODE_VIEW_IN_FRONT_OF:
			//Application.debug("hop");
			//TODO implement choose geo
			return true;
		default:
			return super.switchModeForMouseReleased(mode, hits, changedKernel, control, type);
			
		}

	}
	
	@Override
	protected Hits addPointCreatedForMouseReleased(Hits hits){
		
		hits.add(getMovedGeoPoint());	
		return hits;
	}
	
	
	@Override
	public void showDrawingPadPopup(geogebra.common.awt.GPoint mouseLoc){
		((GuiManager3D) app.getGuiManager()).showDrawingPadPopup3D(view, mouseLoc);
	}
	
	///////////////////////////////////////////
	// INTERSECTIONS
	
	///////////////////////////////////////////
	// INTERSECTIONS
	
	/**
	 *  get two objects (lines or conics) and create intersection point 
	 */
	@Override
	protected GeoElement[] intersect(Hits hits) {
		
		//AppD.debug(hits);
		if (hits.isEmpty())
			return null;		
		if (hits.containsGeoPoint()) {
			hits.clear();
			return null;
		}
		hits.removePolygonsIfSidePresent();
		
		if (goodHits == null)
			goodHits = new Hits3D();
		else
			goodHits.clear();
		
		GeoPointND singlePoint = null;
		
		
		if (selGeos() == 0) { //either single intersection point or single highlighting
			singlePoint = getSingleIntersectionPoint(hits);
			
			//single intersection succeeds, select one geo from hits
			if (singlePoint != null) {
				
				hits.clear();
				hits.add(((GeoElement)singlePoint).getParentAlgorithm().getInput()[0]);
				hits.add(((GeoElement)singlePoint).getParentAlgorithm().getInput()[1]);
				
			} else {
				hits.getHits(new Test[] {
						Test.GEOLINEND, Test.GEOCOORDSYS2D,
						Test.GEOQUADRICND}, false, goodHits);
				
				hits = hits.getHits(1);
			}
		} else if (selGeos() == 1) {		

			

			
			// Should have selGeos() == 1. Need to optimize the hits
			// see what we had selected
			
			if (selCS2D()==1 || selQuadric()==1) {//2d geo: can only intersect with 1d ones.
				hits.getHits(new Test[] { Test.GEOCOORDSYS2D, Test.GEOQUADRIC3D },
						true, goodHits);
				
				
			} else { //1d geo: can intersect with 1d or 2d geo
				hits.getHits(new Test[] {
						Test.GEOLINEND, Test.GEOCOORDSYS2D,
						Test.GEOQUADRICND}, false, goodHits);
				
				//does not have to test this. we will select only the top element!
				// we can only have at most one polygon
				/*((Hits3D)goodHits).removeAllPolygonsButOne();
				if (!(goodHits.size()>=2) ||
						!((GeoElement)goodHits.get(0)).isGeoConic() ||
						!((GeoElement)goodHits.get(1)).isGeoConic())
					((Hits3D)goodHits).removeAllPolygonsAndQuadricsButOne();
				*/
				
			}
			
			//remove incidence. TODO: test incidence by construction, instead of numerically.
			GeoElement selected = selectedGeos.get(0);
			if (selected.isGeoLine()) {
				while (goodHits.size()>=1) {
					if (goodHits.get(0).isGeoPlane() &&
							AlgoIntersectCS1D2D.getConfigLinePlane(
							(GeoLineND)selected,
							((GeoCoordSys2D)goodHits.get(0))
									) == ConfigLinePlane.CONTAINED)
						goodHits.remove(0);
					else
						break;
				}
			} else if (selected.isGeoConic()) {
				while (goodHits.size()>=1) {
					if (goodHits.get(0).isGeoPlane() &&
							AlgoIntersectCS2D2D.getConfigPlanePlane(
							(((GeoConicND)selected).getCoordSys()),
							(((GeoCoordSys2D)goodHits.get(0)).getCoordSys())
									) == AlgoIntersectCS2D2D.RESULTCATEGORY_CONTAINED)
						goodHits.remove(0);
					else
						break;
				}
			} else if (selected.isGeoPolygon()) {
				while (goodHits.size()>=1) {
					if (goodHits.get(0) instanceof GeoCoordSys2D &&
							AlgoIntersectCS2D2D.getConfigPlanePlane(
							(((GeoPolygon)selected).getCoordSys()),
							(((GeoCoordSys2D)goodHits.get(0)).getCoordSys())
									) == AlgoIntersectCS2D2D.RESULTCATEGORY_CONTAINED)
						goodHits.remove(0);
					else
						break;
				}
			} 
			
			if (goodHits.size()==0) {
				//return immediately, so that the selected geo is not fetched
				return null;
			}
			
			//we focus on the selected geo and a new one from goodHits
			hits.clear();
			hits.add(selected);
			hits.add(goodHits.get(0));

		}
		
		addSelectedLine(hits, 10, true);
		addSelectedConic(hits, 10, true);
		addSelectedCS2D(hits, 10, true);
		//addSelectedPolygon(hits, 1, true);
		addSelectedQuadric(hits, 1, true);
		

		if (selLines() >= 2) {// two lines	
			GeoLineND[] lines = getSelectedLinesND();
			GeoPointND point = getAlgoDispatcher().IntersectLines(null, lines[0], lines[1]);
			checkCoordCartesian3D(point);
			return new GeoElement[] {(GeoElement) point};

		} else if (selLines() ==1){
			if (selConics()>=1 ) {// line-conic
				GeoLineND line = getSelectedLinesND()[0];
				GeoConicND conic = getSelectedConicsND()[0];
				GeoElement[] ret = new GeoElement[2];

				GeoPointND[] points = getAlgoDispatcher().IntersectLineConic(null, line, conic);
				for(int i=0;i<2; i++)
					ret[i] = (GeoElement) points[i];

				return ret;
			} else if (selQuadric()>=1) { // line-quadric3D
				GeoLineND line = getSelectedLinesND()[0];
				GeoQuadric3D quadric = getSelectedQuadric()[0];
				GeoElement[] ret = new GeoElement[2];
 
				GeoPointND[] points = getKernel().getManager3D().IntersectLineQuadric(null, line, quadric);
				for(int i=0;i<2; i++)
					ret[i] = (GeoElement) points[i];
				return ret;
			}
			else if (selCS2D()>=1) {// line-CS2D
				GeoLineND line = getSelectedLinesND()[0];
				GeoCoordSys2D cs2Ds = getSelectedCS2D()[0];

				if (cs2Ds instanceof GeoPolygon) {
					return getKernel().getManager3D().IntersectionPoint(
								new String[] {null},
								line,
								(GeoPolygon) cs2Ds
								);
				} else {
					GeoElement[] ret = new GeoElement[1];
					ret[0] = getKernel().getManager3D().Intersect(
					null, (GeoElement) line, (GeoElement) cs2Ds);
					return ret;
				}
			}
		} else if (selConics()>=2 ) {// conic-conic
			GeoConicND[] conics = getSelectedConicsND();
			GeoElement[] ret = new GeoElement[4];
			GeoPointND[] points = getKernel().getAlgoDispatcher().IntersectConics(null, conics[0], conics[1]);
			for(int i=0;i<points.length; i++){
				checkCoordCartesian3D(points[i]);
				ret[i] = (GeoElement) points[i];
			}
			return ret;
		} else if (selConics()>=1 && selCS2D()>=1) { // conic-polygon not available
			
			GeoCoordSys2D plane = getSelectedCS2D()[0];
			if (((GeoElement)plane).isGeoPolygon())
				return null;
			GeoConicND conic = getSelectedConicsND()[0];
			GeoElement[] ret = new GeoElement[2];

			GeoPointND[] points = getKernel().getManager3D().IntersectPlaneConic(null, plane, conic);
			for(int i=0;i<2; i++)
				ret[i] = (GeoElement) points[i];

			return ret;
		} else if (selCS2D()>=2)  { // plane-polygon
		
			GeoCoordSys2D[] CS2Ds = getSelectedCS2D();
			if (CS2Ds[0] instanceof GeoPolygon && CS2Ds[1] instanceof GeoPlane3D)
				return getKernel().getManager3D().IntersectionPoint(null, (GeoPlane3D)CS2Ds[1], (GeoPolygon)CS2Ds[0]);
			else if (CS2Ds[1] instanceof GeoPolygon && CS2Ds[0] instanceof GeoPlane3D)
				return getKernel().getManager3D().IntersectionPoint(null, (GeoPlane3D)CS2Ds[0], (GeoPolygon)CS2Ds[1]);
		}
		
		return null;
	}

	//TODO: color should not be created here
	public Color intersectionCurveColorPlanarPlanar = new Color(127, 0, 255);


	/**
	 * ensure that the point will show 3D cartesion coords
	 * @param point point
	 */
	private static void checkCoordCartesian3D(GeoPointND point){
		if (point.getMode() != Kernel.COORD_CARTESIAN_3D){
			point.setCartesian3D();
			point.updateRepaint();
		}
	}
	
	public ArrayList<IntersectionCurve> getIntersectionCurves(){
		return intersectionCurveList;
	}

	/**
	 * 
	 * @param hits
	 * @return true if a curve is created
	 */
	private boolean intersectionCurve(Hits hits) {


		if (hits == null ) {
			resultedGeo=null;
			return false;
		}

		if (hits.isEmpty()) {
			resultedGeo=null;
			return false;	
		}

		//add selected geo into consideration
		//if (selectedGeos.size()==1 && !hits.contains(selectedGeos.get(0)))
		//	hits.addAll(0, selectedGeos);
		
		if(mouseMoved){ //process new intersection only if mouse has moved
			for (int i = 0; i<intersectionCurveList.size(); ++i) {
				intersectionCurveList.get(i).hitted = false;
			}


			//App.debug(hits);
			
			for (int i = 0; i<hits.size(); ++i) {
				for (int j=i+1; j<hits.size(); ++j) {
					this.createIntersectionCurve(hits.get(i), hits.get(j));
				}
			}

			/*debug
			String s = ">>>> BEFORE PICKING";
			for (Drawable3D d:intersectionCurves){
				s+="\n=== geo="+d.getGeoElement()+"\nzPickMin="+d.zPickMin+"\nzPickMax="+d.zPickMax;
			}
			Application.debug(s);
			//end debug*/


			//calls the renderer to pick the curves
			view3D.getRenderer().pickIntersectionCurves();


			/*debug
			s = "AFTER PICKING <<<<";
			for (Drawable3D d:intersectionCurves){
				s+="\n=== geo="+d.getGeoElement()+"\nzPickMin="+d.zPickMin+"\nzPickMax="+d.zPickMax;
			}
			Application.debug(s);
			//end debug*/

			decideIntersection(hits);
		}

		
		if (goodHits!=null) {
			addSelectedCS2D(goodHits, 2, true);
			addSelectedQuadric(goodHits, 2, true);
		}else{
			Hits firstSurface = ((Hits3D) hits).getFirstSurfaceBefore(selectedGeos);
			addSelectedCS2D(firstSurface, 2, false);
			addSelectedQuadric(firstSurface, 2, false);
		}


		if (selCS2D()>=2)  { // cs2D-cs2D

			//Application.debug(selCS2D());

			GeoCoordSys2D[] cs2Ds = getSelectedCS2D();

			int pIndex = 0;
			int npIndex = 0;
			boolean foundP = false;
			boolean foundNp = false;
			for (int i = 0; i<cs2Ds.length; i++){
				if ( cs2Ds[i] instanceof GeoPolygon ) {
					if (!foundP) {
						pIndex = i;
						foundP = true;		
					}
				} else {
					if ( !foundNp ){
						npIndex = i;
						foundNp = true;
					}
				}
				if (foundP && foundNp)
					break;
			}

			if (!foundP) {
				GeoElement[] ret = new GeoElement[1];
				ret[0] = getKernel().getManager3D().IntersectPlanes(null, 
						cs2Ds[0], cs2Ds[1]);
				return ret[0].isDefined();	
			} else if (foundP && foundNp) {
				GeoElement[] ret;
				GeoPlane3D A = (GeoPlane3D) cs2Ds[npIndex];
				GeoPolygon B = (GeoPolygon) cs2Ds[pIndex];
				if (B.getMetasLength() == 1){
					ret = getKernel().getManager3D().IntersectRegion(new String[] {null}, A, B.getMetas()[0], null);
					return ret[0].isDefined();
				}
				//else
				ret = getKernel().getManager3D().IntersectPath(new String[] {null}, A, B);				
				if (ret[0].isDefined()){
					//create also intersect points
					getKernel().getManager3D().IntersectionPoint(new String[] {null},A,B);
					return true;
				}
				return false;
			}
		}


		else if ((selCS2D() >= 1) &&  (selQuadric() >= 1)) { //plane-quadric

			GeoElement plane = (GeoElement) getSelectedCS2D()[0];
			GeoQuadric3D quad = getSelectedQuadric()[0];
			if (quad instanceof GeoQuadric3DPart){
				GeoQuadric3DLimited ql = (GeoQuadric3DLimited) ((GeoQuadric3DPart) quad).getMetas()[0];
				GeoElement[] ret = {kernel.getManager3D().IntersectQuadricLimited( null, (GeoPlaneND) plane, 
						ql)};
				if(!ret[0].isDefined()){
					return false;
				}
				//also compute corner points
				kernel.getManager3D().Corner(null, (GeoConicSection) ret[0]);
				return true;
			}
			//else
			GeoElement[] ret = {kernel.getManager3D().Intersect( null, (GeoPlaneND) plane, quad)};
			return ret[0].isDefined();			
			
		
		// quadric-quadric : intersection circles
		} else if (selQuadric() >= 2) {			
			GeoQuadric3D[] quads = getSelectedQuadric();
			GeoElement[] ret = kernel.getManager3D().IntersectAsCircle( null, quads[0], quads[1]);
			return ret[0].isDefined();	
		} 




		////////////////////////////////////////

		return false;
	}
	
	
	public boolean createIntersectionCurve(GeoElement A, GeoElement B) {
		boolean intersectable = false;

		for (int i=0; i<intersectionCurveList.size(); ++i) {
			IntersectionCurve intersection = intersectionCurveList.get(i);
			if ( intersection.geo1==getMetaIfJustOne(A) && intersection.geo2==getMetaIfJustOne(B)
					|| intersection.geo1==getMetaIfJustOne(B) && intersection.geo2==getMetaIfJustOne(A)) {
				intersection.hitted = true;
				intersection.drawable.setWaitForUpdate();
				return true;
			}
		}

		/* TODO line/polygon preview
		 * if (A.isGeoLine() && B.isGeoPolygon()) {
			//add intersection to tempArrayList} else*/ 
		if (A.isGeoPlane() && B.isGeoPlane()) {
			//add intersection to tempArrayList
			//if intersection of A,B does not exist, create it
			GeoElement[] ret = new GeoElement[1];
			
			//tells the kernel not to record the algo
			boolean oldSilentMode = getKernel().isSilentMode();
			getKernel().setSilentMode(true);
			ret[0] = getKernel().getManager3D().IntersectPlanes(
					(GeoCoordSys2D)A, (GeoCoordSys2D) B);
			getKernel().setSilentMode(oldSilentMode);
			
			Drawable3D d = new DrawLine3D(view3D, (GeoLineND) ret[0]);
			processIntersectionCurve(A, B, ret[0], d);
			intersectable = true;

		// plane - polyhedron
		} else if (A.isGeoPlane() && B.isGeoPolygon()) {
			createIntersectionCurvePlanePolygon(A, (GeoPolygon) B);
		} else if (B.isGeoPlane() && A.isGeoPolygon()) {
			createIntersectionCurvePlanePolygon(B, (GeoPolygon) A);
			
		// plane-quadric
		} else if (A.isGeoPlane() && B instanceof GeoQuadric3D) {
				intersectable = createIntersectionCurvePlaneQuadric(A, B);
		} else if (B.isGeoPlane() && A instanceof GeoQuadric3D) {
			intersectable = createIntersectionCurvePlaneQuadric(B, A);
		

		// quadric-quadric : intersection circles
		} else if (A instanceof GeoQuadricND && B instanceof GeoQuadricND) {
			//add intersection to tempArrayList
			boolean oldSilentMode = getKernel().isSilentMode();
			getKernel().setSilentMode(true);//tells the kernel not to record the algo
			GeoElement ret = kernel.getManager3D().IntersectAsCircle((GeoQuadricND) A, (GeoQuadricND) B)[0];
			Drawable3D d = new DrawConic3D(view3D, (GeoConic3D) ret);
			getKernel().setSilentMode(oldSilentMode);
			processIntersectionCurve(A, B, ret, d);
			intersectable = true;
		} 

		return intersectable;
		
		
	}
	
	private boolean createIntersectionCurvePlanePolygon(GeoElement A, GeoPolygon B) {


		//check first if B is linked to polyhedron
		if (B.getMetasLength() == 1){

			boolean oldSilentMode = getKernel().isSilentMode();
			getKernel().setSilentMode(true);//tells the kernel not to record the algo

			GeoElement polyhedron = B.getMetas()[0];

			GeoElement[] ret = kernel.getManager3D().IntersectRegion((GeoPlaneND) A, polyhedron);

			boolean goAhead = true;
			DrawIntersectionCurve3D drawPolygons = new DrawIntersectionCurve3D(view3D, ret[0]);
			for (int i=0; i<ret.length && goAhead ; i++){
				GeoElement geo = ret[i];
				if (geo instanceof GeoPolygon3D){
					DrawPolygon3D d = new DrawPolygon3D(view3D, (GeoPolygon3D) geo);
					drawPolygons.add(d);
					processIntersectionCurve(A, polyhedron, geo, drawPolygons);
				}else{
					goAhead = false;
				}
			}


			getKernel().setSilentMode(oldSilentMode);

			return true;

		}
		
		//if B is linked to no (or more than one) polyhedron, create intersection segment(s)
		boolean oldSilentMode = getKernel().isSilentMode();
		getKernel().setSilentMode(true);//tells the kernel not to record the algo

		GeoElement[] ret = kernel.getManager3D().IntersectPath((GeoPlaneND) A, B);

		DrawIntersectionCurve3D drawSegments = new DrawIntersectionCurve3D(view3D, ret[0]);
		for (GeoElement geo : ret){
			DrawSegment3D d = new DrawSegment3D(view3D, (GeoSegmentND) geo);
			drawSegments.add(d);
			processIntersectionCurve(A, B, geo, drawSegments);
		}

		getKernel().setSilentMode(oldSilentMode);

		return true;
		
		
	}
	
	private boolean createIntersectionCurvePlaneQuadric(GeoElement A, GeoElement B) {
		//add intersection to tempArrayList
		boolean oldSilentMode = getKernel().isSilentMode();
		getKernel().setSilentMode(true);//tells the kernel not to record the algo
		GeoElement ret;
		Drawable3D d;
		GeoQuadricND quad;
		if (B instanceof GeoQuadric3DPart){
			quad = (GeoQuadric3DLimited) ((GeoQuadric3DPart) B).getMetas()[0];
			ret = kernel.getManager3D().IntersectQuadricLimited((GeoPlaneND) A, quad);
			d = new DrawConicSection3D(view3D, (GeoConicSection) ret);
		}else{
			quad = (GeoQuadric3D) B;
			ret = kernel.getManager3D().Intersect((GeoPlaneND) A, quad);
			d = new DrawConic3D(view3D, (GeoConicND) ret);
		}
		getKernel().setSilentMode(oldSilentMode);
		processIntersectionCurve(A, quad, ret, d);
		return true;
	}
	
	private void processIntersectionCurve(GeoElement A, GeoElement B, GeoElement intersection, Drawable3D d){
		intersection.setLineThickness(3);
		intersection.setIsPickable(false);
		intersection.setObjColor(new geogebra.awt.GColorD(Color.YELLOW));
		intersectionCurveList.add(new IntersectionCurve(A,B,intersection,true,d));
	}
	
	
	
	
	private IntersectionCurve resultedIntersectionCurve;
	
	private static GeoElement getMetaIfJustOne(GeoElement geo){
		if (geo instanceof FromMeta){
			if (geo.getMetasLength() == 1){
				return ((FromMeta) geo).getMetas()[0];
			}
		}
		
		return geo;
	}
	
	private void decideIntersection(Hits hits) {
		
		
			resultedGeo = null;
			
			//find the nearest intersection curve (if exists)
			double zNear = Double.NEGATIVE_INFINITY;
			//App.error(""+intersectionCurveList.size());
			for (IntersectionCurve intersectionCurve : intersectionCurveList){
				Drawable3D d = intersectionCurve.drawable;
				//App.debug(d+"\nz="+d.getZPickNear()+"\ngeo1:"+intersectionCurve.geo1+"\ngeo2:"+intersectionCurve.geo2);
				if (d.getZPickNear()>zNear){
					resultedGeo=d.getGeoElement();
					resultedIntersectionCurve = intersectionCurve;
					zNear=d.getZPickNear();
				}
			}
			
			
			//App.debug("\n"+resultedGeo+"\nz="+zMin+"\n\n");
			/*
			if (resultedIntersectionCurve != null)
				App.debug("\ngeo1:"+resultedIntersectionCurve.geo1+"\ngeo2:"+resultedIntersectionCurve.geo2);
			*/
			
			if (resultedGeo == null) {
				hideIntersection = true;
				view3D.setPreview(null);
				goodHits=null;
				return;
			}
			
			//check if the intersection curve is visible
			int i = 0;
			boolean checking = true;
			while(checking && i<hits.size()){
			//while(checking && i<existingDrawables.size()){
			
				/*
				Drawable3D d = existingDrawables.get(i);
				GeoElement geo = d.getGeoElement();
				*/
				GeoElement geo = hits.get(i);
				Drawable3D d = (Drawable3D) view3D.getDrawableND(geo);
				//App.debug("\nhits("+i+"): "+geo+"\nd="+d);
				if (d != null){
					//App.debug("\nd.getZPickNear()="+d.getZPickNear()+"\nzNear="+zNear);
					if (d.getZPickNear() < zNear){
						//all next drawables are behind the intersection curve
						checking=false;
					}else if(d.getZPickNear() > zNear + 1 //check if existing geo is really over the curve, with 1 pixel tolerance 
							&& (!geo.isRegion() || geo.getAlphaValue() > 0.8f)){
						//only non-region or non-transparent surfaces can hide the curve
						checking=false;
						resultedGeo = null;
						//Application.debug("=== d.zPickMin<z: "+geo+"\nz-d.zPickMin="+(z-d.zPickMin));
					}
				}
				i++;
			}
			
			if (resultedGeo == null) {
				hideIntersection = true;
				view3D.setPreview(null);
				return;
			}
			

			//App.debug("resultedGeo:"+resultedGeo);
			
		
			//App.debug(hits+"\nA="+A+"\nB="+B);
			
			//Application.debug(hits);
			//for (int j=0; j<hits.size(); ++j) {
			//	System.out.print(((GeoElement)hits.get(j)).isPickable()? "pickable " : "non-pickable");
			//	System.out.println(((GeoElement)hits.get(j)).getObjectColor());
			//}
			
			if (hits.size()==0) {
				hideIntersection = true;
				return;
			}
			
			if (goodHits == null)
				goodHits = new Hits3D();
			else
				goodHits.clear();
			
			
			// since resultedGeo!=null, hits contains at least two element.
			// if one of the two is not one of A or B, we say that
			// resultedGeo is blocked by unrelated geo, 
			// and then we just keep the first hit as goodHit and hide the intersection
			if (hits.size()<2 //check first if there are at least 2 geos 
					||
					(
							!(getMetaIfJustOne(hits.get(0))==resultedIntersectionCurve.geo1 && getMetaIfJustOne(hits.get(1))==resultedIntersectionCurve.geo2)
							&&
							!(getMetaIfJustOne(hits.get(0))==resultedIntersectionCurve.geo2 && getMetaIfJustOne(hits.get(1))==resultedIntersectionCurve.geo1)
							)
					) {
				goodHits.add(hits.get(0));
				hideIntersection = true;
				return;
			}
			
		
			//else, we show the intersection, and add A,B to highligtedgeos
			hideIntersection = false;
			
			goodHits.add(hits.get(0));
			goodHits.add(hits.get(1));
			
			view3D.setPreview((Previewable) resultedIntersectionCurve.drawable);
			//resultedGeo.setIsPickable(false);
			
			
		}


	///////////////////////////////////////////
	// POINT CAPTURING
	
	@Override
	public void transformCoords() {
		//TODO point capturing
	}
		
	
	
	
	///////////////////////////////////////////
	// SELECTIONS
	
	/** selected 1D coord sys */
	protected ArrayList<GeoCoordSys1D> selectedCS1D = new ArrayList<GeoCoordSys1D>();
	
	/** add hits to selectedCS1D
	 * @param hits hits
	 * @param max max number of hits to add
	 * @param addMoreThanOneAllowed if adding more than one is allowed
	 * @return TODO
	 */
	final protected int addSelectedCS1D(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		//Application.debug(hits.toString());
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedCS1D, Test.GEOCOORDSYS1D);
	}
	
	/**
	 * return number of selected 1D coord sys
	 * @return number of selected 1D coord sys
	 */
	final int selCS1D() {
		return selectedCS1D.size();
	}	

	/** return selected 1D coord sys
	 * @return selected 1D coord sys
	 */
	final protected GeoCoordSys1D[] getSelectedCS1D() {
		GeoCoordSys1D[] lines = new GeoCoordSys1D[selectedCS1D.size()];
		int i = 0;
		Iterator<GeoCoordSys1D> it = selectedCS1D.iterator();
		while (it.hasNext()) {
			lines[i] = it.next();
			i++;
		}
		clearSelection(selectedCS1D);
		return lines;
	}	
	
	
	///////////////////////////////////////////
	// selectedCS2D list, similar to selectedCS1D 
	
	/** selected 2D coord sys */
	@SuppressWarnings("unchecked")
	
	/** add hits to selectedCS2D
	 * @param hits hits
	 * @param max max number of hits to add
	 * @param addMoreThanOneAllowed if adding more than one is allowed
	 * @return TODO
	 */
	final protected int addSelectedCS2D(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedCS2D, Test.GEOCOORDSYS2D);
	}
	
	
	/**
	 * return number of selected 2D coord sys
	 * @return number of selected 2D coord sys
	 */
	final int selCS2D() {
		return selectedCS2D.size();
	}	
	
	
	/** return selected 2D coord sys
	 * also clear all selected 2D coord sys. 
	 * @return selected 2D coord sys
	 */
	@SuppressWarnings("unchecked")
	final protected GeoCoordSys2D[] getSelectedCS2D() {
		GeoCoordSys2D[] planes = new GeoCoordSys2D[selectedCS2D.size()];
		int i = 0;
		Iterator<GeoCoordSys2D> it = selectedCS2D.iterator();
		while (it.hasNext()) {
			planes[i] = it.next();
			i++;
		}
		clearSelection(selectedCS2D);
		return planes;
	}	
	
	
	
	/** add hits to selectedPlane
	 * @param hits hits
	 * @param max max number of hits to add
	 * @param addMoreThanOneAllowed if adding more than one is allowed
	 * @return TODO
	 */
	final protected int addSelectedPlane(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedPlane, Test.GEOPLANEND);
	}
	
	
	/**
	 * return number of selected planes
	 * @return number of selected planes
	 */
	final int selPlanes() {
		return selectedPlane.size();
	}	
	
	
	/** return selected planes
	 * also clear all selected planes. 
	 * @return selected planes
	 */
	@SuppressWarnings("unchecked")
	final protected GeoPlane3D[] getSelectedPlanes() {
		GeoPlane3D[] planes = new GeoPlane3D[selectedPlane.size()];
		int i = 0;
		Iterator<GeoPlane3D> it = selectedPlane.iterator();
		while (it.hasNext()) {
			planes[i] = it.next();
			i++;
		}
		clearSelection(selectedPlane);
		return planes;
	}	

	
	
	
	///for quadric
	protected ArrayList<GeoQuadric3D> selectedQuadric = new ArrayList<GeoQuadric3D>();	
	
	final int selQuadric() {
		return selectedQuadric.size();
	}	
	
	final protected int addSelectedQuadric(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedQuadric, Test.GEOQUADRIC3D);
	}	
	final protected GeoQuadric3D[] getSelectedQuadric() {
		GeoQuadric3D[] quads = new GeoQuadric3D[selectedQuadric.size()];
		int i = 0;
		Iterator<GeoQuadric3D> it = selectedQuadric.iterator();
		while (it.hasNext()) {
			quads[i] = it.next();
			i++;
		}
		clearSelection(selectedQuadric);
		return quads;
	}	
 	
	
	@Override
	protected GeoElement chooseGeo(ArrayList<GeoElement> geos, boolean includeFixed) {
		return chooseGeo(geos, includeFixed, false);
	}

	@Override
	protected GeoElement chooseGeo(ArrayList<GeoElement> geos, boolean includeFixed, boolean includeConstants) {

			
		//Application.printStacktrace(((Hits) geos).toString());
		
		if (!geos.isEmpty()){
			//if the geo hitted is one of view3D's geos, then chooseGeo return null
			if (!includeConstants && view3D.owns(geos.get(0)))
				return null;
			
			//doesn't use choosing dialog TODO use choosing dialog ?
			//return super.chooseGeo(geos, includeFixed);
			//return first element : ordering done in hits
			GeoElement geo = geos.get(0);
			if (!includeFixed && geo.isFixed())
				return null;
			
			return geo;
		}
	
		return null;
	}
	
	
	
	
	
	
	
	
	
	///////////////////////////////////////////
	//
	

	/*
	public void mouseWheelMoved(MouseWheelEvent e) {
		
		double r = e.getWheelRotation();

		switch (moveMode) {
		case MOVE_VIEW:
		default:
			view3D.setMoveCursor();//setZoomCursor
			view3D.setScale(view3D.getXscale()+r*10);
			view3D.updateMatrix();
			view.setHits(mouseLoc);
			((EuclidianView3D) view).updateCursor3D();
			view3D.setHitCursor();
			//((Kernel3D) getKernel()).notifyRepaint();
			
			break;

		case MOVE_POINT:
		case MOVE_POINT_WHEEL:
			/* TODO
			//p = p + r*vn			
			Ggb3DVector p1 = (Ggb3DVector) movedGeoPoint3D.getCoords().add(EuclidianView3D.vz.mul(-r*0.1)); 
			movedGeoPoint3D.setCoords(p1);
			
			
			


			objSelected.updateCascade();

			
			movedGeoPoint3D.updateRepaint();//for highlighting in algebraView
			//kernel3D.notifyRepaint();
			
			*
			break;	
		
		
		}
	
		
		

	}
	*/
	
	
	
	
	

	//////////////////////////////////////
	// SELECTED GEOS
	//////////////////////////////////////
	
	/**
	 * add selected 2D coord sys
	 * @param hits
	 * @param max
	 * @param addMoreThanOneAllowed
	 * @return if one has been added
	 */
	final protected int addselectedCS2D(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedCS2D, Test.GEOCOORDSYS2D);
	}
	
	/**
	 * @return number of selected 2D coord sys
	 */
	protected final int selCoordSys2D() {
		return selectedCS2D.size();
	}
	
	/**
	 * @return selected 2D coord sys
	 */
	@SuppressWarnings("unchecked")
	final protected GeoCoordSys2D[] getselectedCS2D() {
		GeoCoordSys2D[] cs = new GeoCoordSys2D[selectedCS2D.size()];
		int i = 0;
		Iterator<GeoCoordSys2D> it = selectedCS2D.iterator();
		while (it.hasNext()) {
			cs[i] = it.next();
			i++;
		}
		clearSelection(selectedCS2D);
		return cs;
	}
	
	
	/////////////////////////////////////////////////////
	// 
	// CURSOR
	//
	/////////////////////////////////////////////////////

	/**
	 * @param cursorType type of the cursor
	 * @return if the 3D cursor is visible for current mode
	 */
	public boolean cursor3DVisibleForCurrentMode(int cursorType){

		if (cursorType==EuclidianView3D.PREVIEW_POINT_ALREADY){
			switch(mode){
			//cross arrows for moving point
			case EuclidianConstants.MODE_MOVE:
			case EuclidianConstants.MODE_POINT:
			case EuclidianConstants.MODE_POINT_ON_OBJECT:
				return true;//!mousePressed; //don't display cursor if dragging 
			default:
				return false;			
			}		
		} else if (cursorType==EuclidianView3D.PREVIEW_POINT_DEPENDENT) {
			switch(mode){
			//modes in which the result could be a dependent point 
			case EuclidianConstants.MODE_POINT:
			case EuclidianConstants.MODE_INTERSECT:
				return true;

			default:
				return false;					
			}
		} else{
			switch(mode){
			//modes where point can be created on path/region
			
			case EuclidianConstants.MODE_POINT:
			case EuclidianConstants.MODE_POINT_ON_OBJECT:
				
			case EuclidianConstants.MODE_JOIN:
			case EuclidianConstants.MODE_SEGMENT:
				
			case EuclidianConstants.MODE_RAY:
			case EuclidianConstants.MODE_VECTOR:

			case EuclidianConstants.MODE_VECTOR_FROM_POINT:

			case EuclidianConstants.MODE_POLYGON:
			case EuclidianConstants.MODE_POLYLINE:
			case EuclidianConstants.MODE_CIRCLE_THREE_POINTS:
			case EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS:
			case EuclidianConstants.MODE_PLANE_THREE_POINTS:
			case EuclidianConstants.MODE_SPHERE_TWO_POINTS:
			case EuclidianConstants.MODE_SPHERE_POINT_RADIUS:
			case EuclidianConstants.MODE_CONE_TWO_POINTS_RADIUS:
			case EuclidianConstants.MODE_CYLINDER_TWO_POINTS_RADIUS:
			case EuclidianConstants.MODE_PYRAMID:
			case EuclidianConstants.MODE_PRISM:
				
			case EuclidianConstants.MODE_VIEW_IN_FRONT_OF:
				return true;

			default:
				return false;			
			}
			
		}
		
	}
	





	////////////////////////////////////////
	// HANDLING PARTS OF PREVIEWABLES
	////////////////////////////////////////
	
	private GeoElement handledGeo;
	
	/**
	 * sets the geo as an handled geo (for previewables)
	 * @param geo
	 */
	public void setHandledGeo(GeoElement geo){		
		handledGeo = geo;
		if (handledGeo==null)
			return;
		setStartPointLocation();
		handledGeo.recordChangeableCoordParentNumbers();
	}
	
	
	@Override
	protected boolean viewHasHitsForMouseDragged(){
		//Application.debug(moveMode);
		if (moveMode==MOVE_POINT && view3D.getCursor3DType()==EuclidianView3D.PREVIEW_POINT_ALREADY)
			return view.getHits().containsGeoPoint(); //if a point is under the mouse, don't try to find another hit
		else
			return super.viewHasHitsForMouseDragged();
	}
	
	@Override
	protected void wrapMouseDragged(AbstractEvent event) {
		
		if (handledGeo!=null){
			setMouseLocation(event);
			event.release();
			updateTranslationVector();
			handledGeo.moveFromChangeableCoordParentNumbers(translationVec3D, startPoint3D, view3D.getViewDirection(), null, null);
			//view3D.updatePreviewable();
			kernel.notifyRepaint();
			return;
		}
		
		super.wrapMouseDragged(event);
	}
	
	
	////////////////////////////////////////
	// MOVE OBJECTS
	////////////////////////////////////////
	
	private Coords startPoint3D = new Coords(0,0,0,1);
	private Coords startPoint3DxOy = new Coords(0,0,0,1);
	
	private double zRW;

	protected Coords translationVec3D = new Coords(4);
	
	/**
	 * update translation vector
	 */
	protected void updateTranslationVector(){
		Coords point = view3D.getPickPoint(mouseLoc);
		view3D.toSceneCoords3D(point);
		updateTranslationVector(point);
	}
	
	/**
	 * update translation vector from start point to current mouse pos
	 * @param point current mouse pos
	 */
	protected void updateTranslationVector(Coords point){
		translationVec3D = point.sub(startPoint3D);
	}
	
	@Override
	public void setStartPointLocation(){
		udpateStartPoint();
			
		super.setStartPointLocation();
	}
	
	/**
	 * update start point to current mouse coords
	 */
	protected void udpateStartPoint(){
		if (mouseLoc==null)//case that it's algebra view calling
			return;
		
		updateStartPoint(view3D.getPickPoint(mouseLoc));

	}

	/**
	 * update start point to p coords
	 * @param p coords
	 */
	final protected void updateStartPoint(Coords p){
		
		startPoint3D.set(p);
		view3D.toSceneCoords3D(startPoint3D);
		
		//App.debug("\n"+startPoint3D);

		//project on xOy
		startPoint3DxOy = startPoint3D.projectPlaneThruVIfPossible(CoordMatrix4x4.IDENTITY, view3D.getViewDirection())[0];
	}
	
	@Override
	public void setStartPointLocationWithOrigin(double x, double y) {
		udpateStartPoint();
		//sub origin
		startPoint3DxOy.setX(startPoint3DxOy.getX()-x);
		startPoint3DxOy.setY(startPoint3DxOy.getY()-y);
		
		super.setStartPointLocationWithOrigin(x, y);
	}
	
	@Override
	protected void calcRWcoords() {
		Coords point =  view3D.getPickPoint(mouseLoc);
		view3D.toSceneCoords3D(point);
		xRW = point.getX();
		yRW = point.getY();
		zRW = point.getZ();
	}

	@Override
	protected void moveDependent(boolean repaint) {

		updateTranslationVector();
		GeoElement.moveObjects(translateableGeos, translationVec3D, startPoint3D, view3D.getViewDirection());	
	
		kernel.notifyRepaint();
	}
	
	@Override
	protected void moveMultipleObjects(boolean repaint) {	
		/*
		translationVec.setX(xRW - startPoint.x);
		translationVec.setY(yRW - startPoint.y);
		startPoint.setLocation(xRW, yRW);
		startLoc = mouseLoc;

		// move all selected geos
		GeoElement.moveObjects(app.getSelectedGeos(), translationVec, new GgbVector(xRW, yRW, 0));									
			*/
		
		AppD.debug("TODO");
	}	
	
	final protected int addSelectedPolygon3D(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedPolygons3D, Test.GEOPOLYGON3D);
	}
	final protected GeoPolygon[] getSelectedPolygons3D() {				
		GeoPolygon[] ret = new GeoPolygon[selectedPolygons3D.size()];
		for (int i = 0; i < selectedPolygons3D.size(); i++) {		
			ret[i] = selectedPolygons3D.get(i);
		}
		clearSelection(selectedPolygons3D);
		return ret;
	}

	protected final int selPolygons3D() {
		return selectedPolygons3D.size();
	}

	
	
	@Override
	final protected GeoElement[] orthogonal(Hits hits) {

		if (hits.isEmpty())
			return null;

		boolean hitPoint = (addSelectedPoint(hits, 1, false) != 0);
		
		if (!hitPoint) {
			if (selCoordSys2D() == 0)
				addSelectedLine(hits, 1, false);
			if (selLines() == 0) 
				addselectedCS2D(hits, 1, false);
		}

		if (selPoints() == 1) {
			if (selCoordSys2D() == 1) {
				// fetch selected point and plane
				GeoPointND[] points = getSelectedPointsND();
				GeoCoordSys2D[] cs = getselectedCS2D();
				// create new line
				return new GeoElement[] {(GeoElement) getKernel().getManager3D().OrthogonalLine3D(null, points[0], cs[0])};
			}else if (selLines() == 1){
				// fetch selected point and line
				GeoPointND[] points = getSelectedPointsND();
				GeoLineND[] lines = getSelectedLinesND();
				// create new line
				return new GeoElement[] {(GeoElement) getKernel().getManager3D().OrthogonalLine3D(null, points[0], lines[0], kernel.getSpace())};				
			}
		}
		
		
		return null;
	}

	
	private final GeoElement[] rotateAroundLine(Hits hits) {
		if (hits.isEmpty()) {
			return null;
		}
	
		// Transformable
		int count = 0;
		if (selGeos() == 0) {
			Hits rotAbles = hits.getHits(Test.TRANSFORMABLE, tempArrayList);
			count = addSelectedGeo(rotAbles, 1, false);
		}
	
		// polygon
		if (count == 0) {
			count = addSelectedPolygon(hits, 1, false);
		}
	
		// rotation axis
		if (count == 0) {
			addSelectedLine(hits, 1, false);
		}
	
		// we got the rotation center point
		if ((selLines() == 1) && (selGeos() > 0)) {
	
			GeoElement[] selGeos = getSelectedGeos();
			
	
			((DialogManager3D) getDialogManager())
					.showNumberInputDialogRotate(
							l10n.getMenu(getKernel().getModeText(mode)),
							getSelectedPolygons(), getSelectedLinesND(), selGeos,
							this);
	
			return null;
	
		}
	
		return null;
	}

	
	public GeoElement[] rotateAroundLine(GeoElement geoRot, GeoNumberValue phi, GeoLineND line) {
		
		return kernel.getManager3D().Rotate3D(null, geoRot, phi, line);
	}
	
	/**
	 * 
	 * @param clockwise user's choice
	 * @param line rotation axis
	 * @return correct clockwise orientation resp. view/line
	 */
	public boolean viewOrientationForClockwise(boolean clockwise, GeoLineND line){
		
		if(line.getDirectionInD3().dotproduct(view3D.getViewDirection()) > 0)
			return !clockwise;

		return clockwise;
	}

	
	
	
	
	
	
	
	
	
	private final GeoElement[] mirrorAtPlane(Hits hits) {
		if (hits.isEmpty()) {
			return null;
		}
	
		// Transformable
		int count = 0;
		if (selGeos() == 0) {
			Hits mirAbles = hits.getHits(Test.TRANSFORMABLE, tempArrayList);
			count = addSelectedGeo(mirAbles, 1, false);
		}
	
		// polygon
		if (count == 0) {
			count = addSelectedPolygon(hits, 1, false);
		}
	
		// plane = mirror
		if (count == 0) {
			addSelectedPlane(hits, 1, false);
		}
	
		// we got the mirror plane
		if (selPlanes() == 1) {
			if (selPolygons() == 1) {
				GeoPolygon[] polys = getSelectedPolygons();
				GeoPlane3D[] planes = getSelectedPlanes();
				checkZooming(); 

				return kernel.getManager3D().Mirror3D(null, polys[0], planes[0]);
			} else if (selGeos() > 0) {
				// mirror all selected geos
				GeoElement[] geos = getSelectedGeos();
				GeoPlane3D plane = getSelectedPlanes()[0];
				ArrayList<GeoElement> ret = new ArrayList<GeoElement>();
				checkZooming(); 
				
				for (int i = 0; i < geos.length; i++) {
					if (geos[i] != plane) {
						if (geos[i] instanceof Transformable) {
							ret.addAll(Arrays.asList(
									kernel.getManager3D().Mirror3D(null, geos[i], plane)));
						} else if (geos[i].isGeoPolygon()) {
							ret.addAll(Arrays.asList(
									kernel.getManager3D().Mirror3D(null, geos[i], plane)));
						}
					}
				}
				GeoElement[] retex = {};
				return ret.toArray(retex);
			}
		}
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public boolean refreshHighlighting(Hits hits, boolean control) {

		if (AppD.getShiftDown())
			return false;

		return super.refreshHighlighting(hits, control);
	}
	
	

	/**
	 * show popup menu when no geo is selected
	 * @param hits hits on the mouse
	 */
	@Override
	protected void showPopupMenuChooseGeo(ArrayList<GeoElement> selectedGeos1, Hits hits){
		if (app.isUsingFullGui() && app.getGuiManager() != null) {
			//if (geo != null) {

				((GuiManager3D) app.getGuiManager()).showPopupChooseGeo(
						selectedGeos1,
						hits,
						view3D, mouseLoc);

			/* Now overriden
			} else {
				// for 3D : if the geo hitted is xOyPlane, then
				// chooseGeo return null
				// app.getGuiManager().showDrawingPadPopup((EuclidianView)
				// view, mouseLoc);
				showDrawingPadPopup(mouseLoc);
			}
			*/
		}
	}
	
	
	
	
	
	
	
	/**
	 * update all drawables now
	 */
	public void updateOwnDrawablesNow(){
		for (IntersectionCurve intersectionCurve : intersectionCurveList)
			intersectionCurve.drawable.update();
	}
	
	@Override
	public void setMode(int newMode) {

		if (newMode!=EuclidianConstants.MODE_INTERSECTION_CURVE)
			intersectionCurveList.clear();
		super.setMode(newMode);
	}
	
	@Override
	protected void setDragCursorIfMoveView(){
		//nothing to do, keep same cursor
	}
	
	
	
	
	@Override
	protected final void processSelectionRectangle(boolean alt, boolean isControlDown) {

		//TODO implement this
	}
	
	
	
}

