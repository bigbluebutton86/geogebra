package geogebra.common.gui.toolbar;

import geogebra.common.euclidian.EuclidianConstants;

import java.util.Vector;

/**
 * @author gabor
 *
 * This class is not a superclass of ToolBar, only  common method stack
 */
public class ToolBar {
	
	/**
	 * Integer used to indicate a separator in the toolbar.
	 */
	public static final Integer SEPARATOR = new Integer(-1);

	/**
	 * Returns with the default definition of the general tool bar without
	 * macros.
	 * 
	 * @param showAllMenu true, if all menus must appear. (On the web there
	 * are some tools, which don't appear.)
	 * @return The default definition of the general tool bar without macros.
	 */
	public static String getAllToolsNoMacros(boolean showAllMenu, boolean html5) {
		StringBuilder sb = new StringBuilder();
		
		// move
		sb.append(EuclidianConstants.MODE_MOVE);
		if (!html5) {
			// these two are old tools that we don't want to enable in HTML5
			sb.append(" ");
			sb.append(EuclidianConstants.MODE_MOVE_ROTATE);
			sb.append(" ");
			sb.append(EuclidianConstants.MODE_RECORD_TO_SPREADSHEET);
		}
			
		// points
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_POINT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_POINT_ON_OBJECT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ATTACH_DETACH);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_INTERSECT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_MIDPOINT);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_COMPLEX_NUMBER);
	
		// basic lines
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_JOIN);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_SEGMENT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_SEGMENT_FIXED);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_RAY);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_POLYLINE);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_VECTOR);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_VECTOR_FROM_POINT);
	
		// advanced lines
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_ORTHOGONAL);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_PARALLEL);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_LINE_BISECTOR);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ANGULAR_BISECTOR);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_TANGENTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_POLAR_DIAMETER);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_FITLINE);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_LOCUS);
	
		// polygon
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_POLYGON);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_REGULAR_POLYGON);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_RIGID_POLYGON);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_VECTOR_POLYGON);
	
		// circles, arcs
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_CIRCLE_TWO_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_CIRCLE_POINT_RADIUS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_COMPASSES);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_CIRCLE_THREE_POINTS);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_SEMICIRCLE);
		sb.append("  ");
		sb.append(EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS);
	
		// conics
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_ELLIPSE_THREE_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_PARABOLA);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_CONIC_FIVE_POINTS);
	
		// measurements
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_ANGLE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ANGLE_FIXED);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_DISTANCE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_AREA);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_SLOPE);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_CREATE_LIST);

	
		// transformations
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_MIRROR_AT_LINE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_MIRROR_AT_POINT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_MIRROR_AT_CIRCLE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ROTATE_BY_ANGLE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_TRANSLATE_BY_VECTOR);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_DILATE_FROM_POINT);
	
		// dialogs
		sb.append(" | ");
		
		sb.append(EuclidianConstants.MODE_TEXT);
		sb.append(" ");
		
		//if(showAllMenu){
			sb.append(EuclidianConstants.MODE_IMAGE);
			sb.append(" ");
		//if(showAllMenu){
			sb.append(EuclidianConstants.MODE_PEN);
			sb.append(" ");
			//sb.append(EuclidianConstants.MODE_PENCIL);
			//sb.append(" ");
			sb.append(EuclidianConstants.MODE_FREEHAND_SHAPE);
			sb.append(" , ");
		//}
		
		sb.append(EuclidianConstants.MODE_RELATION);
		
		if(showAllMenu){
			sb.append(" ");
			sb.append(EuclidianConstants.MODE_PROBABILITY_CALCULATOR);
			sb.append(" ");
			if (!html5) {
				sb.append(EuclidianConstants.MODE_FUNCTION_INSPECTOR);
			}
		}
			
		// objects with actions
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_SLIDER);
		if(showAllMenu && !html5){
			sb.append(" ");
			sb.append(EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX);
			sb.append(" ");
			sb.append(EuclidianConstants.MODE_BUTTON_ACTION);
		}
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_TEXTFIELD_ACTION);
		
			
		// properties
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_TRANSLATEVIEW);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ZOOM_IN);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ZOOM_OUT);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_SHOW_HIDE_OBJECT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_SHOW_HIDE_LABEL);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_COPY_VISUAL_STYLE);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_DELETE);
	
		return sb.toString();
	}
	
	
	/**
	 * @return The default definition of the general tool bar without macros.
	 */
	public static String getAllToolsNoMacrosForPlane() {
		StringBuilder sb = new StringBuilder();
	
		// move
		sb.append(EuclidianConstants.MODE_MOVE);
		// sb.append(" ");
		// sb.append(EuclidianConstants.MODE_MOVE_ROTATE);
		// sb.append(" ");
		// sb.append(EuclidianConstants.MODE_RECORD_TO_SPREADSHEET);
	
		// points
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_POINT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_POINT_ON_OBJECT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_INTERSECT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_MIDPOINT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ATTACH_DETACH);
	
		// basic lines
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_JOIN);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_SEGMENT);
		// sb.append(" ");
		// sb.append(EuclidianView.MODE_SEGMENT_FIXED);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_RAY);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_VECTOR);
		// sb.append(" ");
		// sb.append(EuclidianView.MODE_VECTOR_FROM_POINT);
	
		// advanced lines
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_ORTHOGONAL);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_PARALLEL);
		// sb.append(" ");
		// sb.append(EuclidianView.MODE_LINE_BISECTOR);
		// sb.append(" ");
		// sb.append(EuclidianView.MODE_ANGULAR_BISECTOR);
		// sb.append(" , ");
		// sb.append(EuclidianView.MODE_TANGENTS);
		// sb.append(" ");
		// sb.append(EuclidianView.MODE_POLAR_DIAMETER);
		// sb.append(" , ");
		// sb.append(EuclidianView.MODE_FITLINE);
		// sb.append(" , ");
		// sb.append(EuclidianView.MODE_LOCUS);
	
		// polygon
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_POLYGON);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_REGULAR_POLYGON);
		// sb.append(" ");
		// sb.append(EuclidianView.MODE_RIGID_POLYGON);
		// sb.append(" ");
		// sb.append(EuclidianView.MODE_POLYLINE);
	
		// circles, arcs
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_CIRCLE_TWO_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_CIRCLE_POINT_RADIUS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_COMPASSES);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_CIRCLE_THREE_POINTS);
		/*
		 * sb.append(" , "); sb.append(EuclidianView.MODE_SEMICIRCLE);
		 * sb.append("  ");
		 * sb.append(EuclidianView.MODE_CIRCLE_ARC_THREE_POINTS);
		 * sb.append(" ");
		 * sb.append(EuclidianView.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS);
		 * sb.append(" , ");
		 * sb.append(EuclidianView.MODE_CIRCLE_SECTOR_THREE_POINTS);
		 * sb.append(" ");
		 * sb.append(EuclidianView.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS);
		 */
	
		// conics
		/*
		 * sb.append(" | "); sb.append(EuclidianView.MODE_ELLIPSE_THREE_POINTS);
		 * sb.append(" "); sb.append(EuclidianView.MODE_HYPERBOLA_THREE_POINTS);
		 * sb.append(" "); sb.append(EuclidianView.MODE_PARABOLA);
		 * sb.append(" , "); sb.append(EuclidianView.MODE_CONIC_FIVE_POINTS);
		 */
	
		// measurements
		
		sb.append(" | "); 
		sb.append(EuclidianConstants.MODE_ANGLE);
		//sb.append(" "); 
		//sb.append(EuclidianConstants.MODE_ANGLE_FIXED);
		sb.append(" , "); 
		sb.append(EuclidianConstants.MODE_DISTANCE);
		sb.append(" "); 
		sb.append(EuclidianConstants.MODE_AREA); 
		//sb.append(" ");
		//sb.append(EuclidianConstants.MODE_SLOPE);


		// transformations

		sb.append(" | "); sb.append(EuclidianConstants.MODE_MIRROR_AT_LINE);
		sb.append(" "); sb.append(EuclidianConstants.MODE_MIRROR_AT_POINT);
		// sb.append(" "); sb.append(EuclidianConstants.MODE_MIRROR_AT_CIRCLE);
		sb.append(" "); sb.append(EuclidianConstants.MODE_ROTATE_BY_ANGLE);
		sb.append(" "); sb.append(EuclidianConstants.MODE_TRANSLATE_BY_VECTOR);
		sb.append(" "); sb.append(EuclidianConstants.MODE_DILATE_FROM_POINT);
		 
	
		// dialogs
		/*
		 * sb.append(" | "); sb.append(EuclidianView.MODE_SLIDER);
		 * sb.append(" , "); sb.append(EuclidianView.MODE_TEXT); sb.append(" ");
		 * sb.append(EuclidianView.MODE_IMAGE); sb.append(" ");
		 * sb.append(EuclidianView.MODE_PEN); sb.append(" , ");
		 * sb.append(EuclidianView.MODE_RELATION); sb.append(" ");
		 * sb.append(EuclidianView.MODE_PROBABILITY_CALCULATOR); sb.append(" ");
		 * sb.append(EuclidianView.MODE_FUNCTION_INSPECTOR);
		 */
	
		// objects with actions
		/*
		 * sb.append(" | "); sb.append(EuclidianView.MODE_SHOW_HIDE_CHECKBOX);
		 * sb.append(" "); sb.append(EuclidianView.MODE_BUTTON_ACTION);
		 * sb.append(" "); sb.append(EuclidianView.MODE_TEXTFIELD_ACTION);
		 */
	
		// properties
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_TRANSLATEVIEW);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ZOOM_IN);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ZOOM_OUT);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_SHOW_HIDE_OBJECT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_SHOW_HIDE_LABEL);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_COPY_VISUAL_STYLE);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_DELETE);
	
		return sb.toString();
	}

	/**
	 * @return default toolbar (3D)
	 */
	public static String getAllToolsNoMacros3D() {
		
		StringBuilder sb = new StringBuilder();
		
		//move
		sb.append(EuclidianConstants.MODE_MOVE);
		sb.append( " | ");
		
		//points
		sb.append( EuclidianConstants.MODE_POINT );
		sb.append( " ");
		sb.append( EuclidianConstants.MODE_POINT_ON_OBJECT );
		sb.append( " ");
		sb.append( EuclidianConstants.MODE_INTERSECT );
		sb.append( " ");
		sb.append( EuclidianConstants.MODE_MIDPOINT );
		sb.append( " , ");
		sb.append( EuclidianConstants.MODE_ATTACH_DETACH );
		sb.append( " , ");
		sb.append( EuclidianConstants.MODE_COMPLEX_NUMBER );
		sb.append( " | ");
		
		//lines
		sb.append( EuclidianConstants.MODE_JOIN);
		sb.append( " ");
		sb.append( EuclidianConstants.MODE_SEGMENT);
		sb.append( " ");
		sb.append( EuclidianConstants.MODE_RAY);
		sb.append( " , ");
		sb.append( EuclidianConstants.MODE_VECTOR);
		sb.append( " | ");
		
		//specific lines
		sb.append( EuclidianConstants.MODE_ORTHOGONAL);
		sb.append( " ");
		sb.append( EuclidianConstants.MODE_PARALLEL);
		sb.append( " | ");
		
		//polygons
		sb.append( EuclidianConstants.MODE_POLYGON);
		sb.append( " | ");
		
		//conics
		sb.append( EuclidianConstants.MODE_CIRCLE_AXIS_POINT);
		sb.append( " ");
		sb.append( EuclidianConstants.MODE_CIRCLE_POINT_RADIUS_DIRECTION);
		sb.append( " ");
		sb.append( EuclidianConstants.MODE_CIRCLE_THREE_POINTS);
		sb.append( " | ");
		
		//intersection curve
		sb.append( EuclidianConstants.MODE_INTERSECTION_CURVE);
		sb.append( " | ");
		
		//planes
		sb.append( EuclidianConstants.MODE_PLANE_THREE_POINTS);
		sb.append( " ");
		sb.append( EuclidianConstants.MODE_PLANE);
		sb.append( " | ");
		
		//specific planes
		sb.append( EuclidianConstants.MODE_ORTHOGONAL_PLANE);
		sb.append( " ");
		sb.append( EuclidianConstants.MODE_PARALLEL_PLANE);
		sb.append( " | ");
		
		//prisms/pyramids/cones/cylinders
		sb.append(EuclidianConstants.MODE_PYRAMID);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_PRISM);
		sb.append(" , ");
		sb.append( EuclidianConstants.MODE_CONIFY);
		sb.append(" ");
		sb.append( EuclidianConstants.MODE_EXTRUSION);
		sb.append( " | " );
		
		//spheres
		sb.append( EuclidianConstants.MODE_SPHERE_TWO_POINTS );
		sb.append( " ");
		sb.append( EuclidianConstants.MODE_SPHERE_POINT_RADIUS );
		sb.append( " , ");
		sb.append( EuclidianConstants.MODE_CONE_TWO_POINTS_RADIUS );
		sb.append( " ");
		sb.append( EuclidianConstants.MODE_CYLINDER_TWO_POINTS_RADIUS );
		sb.append( " | ");
		
		//measures
		sb.append( EuclidianConstants.MODE_ANGLE );
		sb.append( " , ");
		sb.append( EuclidianConstants.MODE_DISTANCE );
		sb.append( " ");
		sb.append( EuclidianConstants.MODE_AREA );
		sb.append( " ");
		sb.append( EuclidianConstants.MODE_VOLUME );
		sb.append( " | ");		
		
		// transformations
		sb.append(EuclidianConstants.MODE_MIRROR_AT_PLANE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_MIRROR_AT_LINE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_MIRROR_AT_POINT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ROTATE_AROUND_LINE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_TRANSLATE_BY_VECTOR);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_DILATE_FROM_POINT);
		sb.append(" | ");
	
		// texts, sliders, etc.
		sb.append( EuclidianConstants.MODE_TEXT );
		sb.append( " | ");

		
		//view control
		sb.append(EuclidianConstants.MODE_ROTATEVIEW );
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_TRANSLATEVIEW);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ZOOM_IN);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ZOOM_OUT);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_SHOW_HIDE_OBJECT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_SHOW_HIDE_LABEL);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_COPY_VISUAL_STYLE);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_DELETE);
		sb.append( " | ");
		
		sb.append( EuclidianConstants.MODE_VIEW_IN_FRONT_OF);

		return sb.toString();
	}
	
	/**
	 * Parses a toolbar definition string like "0 , 1 2 | 3 4 5 || 7 8 9" where
	 * the int values are mode numbers, "," adds a separator within a menu, "|"
	 * starts a new menu and "||" adds a separator before starting a new menu.
	 * 
	 * @param toolbarString
	 *            toolbar definition string
	 * 
	 * @return toolbar as nested Vector objects with Integers for the modes.
	 *         Note: separators have negative values.
	 */
	public static Vector<ToolbarItem> parseToolbarString(String toolbarString) {
		String[] tokens = toolbarString.split(" ");
		Vector<ToolbarItem> toolbar = new Vector<ToolbarItem>();
		Vector<Integer> menu = new Vector<Integer>();

		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i].equals("|")) { // start new menu
				if (menu.size() > 0)
					toolbar.add(new ToolbarItem(menu));
				menu = new Vector<Integer>();
			} else if (tokens[i].equals("||")) { // separator between menus
				// no separator anymore
				// keep this for backward compatibility
				
				if (menu.size() > 0)
					toolbar.add(new ToolbarItem(menu));

				// add separator between two menus
				menu = new Vector<Integer>();
				/*
				// menu.add(SEPARATOR);
				// toolbar.add(menu);
				toolbar.add(new ToolbarItem(SEPARATOR));

				// start next menu
				menu = new Vector<Integer>();
				*/
			} else if (tokens[i].equals(",")) { // separator within menu
				menu.add(SEPARATOR);
			} else { // add mode to menu
				try {
					if (tokens[i].length() > 0) {
						int mode = Integer.parseInt(tokens[i]);
						menu.add(new Integer(mode));
					}
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		}

		// add last menu to toolbar
		if (menu.size() > 0)
			toolbar.add(new ToolbarItem(menu));
		return toolbar;
	}


}
