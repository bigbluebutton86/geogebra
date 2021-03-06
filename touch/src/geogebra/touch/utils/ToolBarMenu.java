package geogebra.touch.utils;

/**
 * 
 * Merging {@link ToolBarCommand ToolBarCommands} of the buttons from the
 * main-toolBar with the ToolBarCommands of all the buttons in the submenu.
 * 
 * @author Thomas Krismayer
 * 
 */
public enum ToolBarMenu {

	// FIXME: add menu items when implemented

	ManipulateObjects(ToolBarCommand.Move_Mobile, new ToolBarCommand[] {
			ToolBarCommand.Move_Mobile,
			// TODO: ToolBarCommand.Select,
			ToolBarCommand.RotateAroundPoint, ToolBarCommand.DeleteObject }),

	Point(ToolBarCommand.NewPoint, new ToolBarCommand[] {
			ToolBarCommand.NewPoint, ToolBarCommand.PointOnObject,
			ToolBarCommand.AttachDetachPoint,
			ToolBarCommand.IntersectTwoObjects,
			ToolBarCommand.MidpointOrCenter, ToolBarCommand.ComplexNumbers }),

	Line(ToolBarCommand.LineThroughTwoPoints, new ToolBarCommand[] {
			ToolBarCommand.LineThroughTwoPoints,
			ToolBarCommand.SegmentBetweenTwoPoints,
			ToolBarCommand.SegmentFixed,
			ToolBarCommand.RayThroughTwoPoints,
			ToolBarCommand.PolylineBetweenPoints,
			ToolBarCommand.VectorBetweenTwoPoints,
			ToolBarCommand.VectorFromPoint }),

	SpecialLine(ToolBarCommand.PerpendicularLine, new ToolBarCommand[] {
			ToolBarCommand.PerpendicularLine, ToolBarCommand.ParallelLine,
			ToolBarCommand.PerpendicularBisector, ToolBarCommand.AngleBisector,
			ToolBarCommand.Tangents,
			// TODO: ToolBarCommand.PolarOrDiameterLine,
			// TODO: ToolBarCommand.BestFitLine,
			ToolBarCommand.Locus }),

	Polygon(ToolBarCommand.Polygon, new ToolBarCommand[] {
			ToolBarCommand.Polygon, ToolBarCommand.RegularPolygon,
			ToolBarCommand.RigidPolygon, ToolBarCommand.VectorPolygon }),

	CircleAndArc(ToolBarCommand.CircleWithCenterThroughPoint,
			new ToolBarCommand[] { ToolBarCommand.CircleWithCenterThroughPoint,
					ToolBarCommand.CirclePointRadius,
					ToolBarCommand.Compasses,
					ToolBarCommand.CircleThroughThreePoints,
					ToolBarCommand.Semicircle,
					ToolBarCommand.CircularArcWithCenterBetweenTwoPoints,
					ToolBarCommand.CircumCirculuarArcThroughThreePoints,
					ToolBarCommand.CircularSectorWithCenterBetweenTwoPoints,
					ToolBarCommand.CircumCircularSectorThroughThreePoints }),

	ConicSection(ToolBarCommand.Ellipse, new ToolBarCommand[] {
			ToolBarCommand.Parabola, ToolBarCommand.Ellipse,
			ToolBarCommand.Hyperbola, ToolBarCommand.ConicThroughFivePoints }),

	Measurement(ToolBarCommand.Angle,
			new ToolBarCommand[] { ToolBarCommand.Angle,
			ToolBarCommand.AngleFixed,
			ToolBarCommand.DistanceOrLength,
			ToolBarCommand.Area,
			ToolBarCommand.Slope,
			// TODO: ToolBarCommand.CreateList
			}),

	Transformation(ToolBarCommand.ReflectObjectAboutLine, new ToolBarCommand[] {
			ToolBarCommand.ReflectObjectAboutLine,
			ToolBarCommand.ReflectObjectAboutPoint,
			ToolBarCommand.ReflectObjectAboutCircle,
			ToolBarCommand.RotateObjectByAngle,
			ToolBarCommand.TranslateObjectByVector, ToolBarCommand.Dilate }),

	// TODO:
	SpecialObject(ToolBarCommand.Pen /* TODO: ToolBarCommand.InsertText */,
			new ToolBarCommand[] {
					// ToolBarCommand.InsertText,
					// ToolBarCommand.InsertImage,
					ToolBarCommand.Pen, ToolBarCommand.FreehandShape
			// , ToolBarCommand.RelationBetweenTwoObjects
			}),

	// TODO:
	ActionObject(ToolBarCommand.Slider,
			new ToolBarCommand[] { ToolBarCommand.Slider,
			// ToolBarCommand.CheckBoxToShowHideObjects,
			// ToolBarCommand.InsertButton,
			// ToolBarCommand.InsertInputBox
			});

	private ToolBarCommand[] entry;
	private ToolBarCommand command;

	ToolBarMenu(final ToolBarCommand command, final ToolBarCommand[] entries) {
		this.command = command;
		this.entry = entries;
	}

	public ToolBarCommand getCommand() {
		return this.command;
	}

	public ToolBarCommand[] getEntries() {
		return this.entry;
	}

}
