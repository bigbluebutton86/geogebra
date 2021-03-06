package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoRoots;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunctionable;
import geogebra.common.kernel.geos.GeoNumberValue;
import geogebra.common.main.MyError;

/**
 * Roots[ <GeoFunction>, <Number> , <Number> ]
 * (Numerical version, more than one root.)
 */
public class CmdRoots extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdRoots(Kernel kernel) {
		super(kernel);
	}//Constructor

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1] instanceof GeoNumberValue))
					&& (ok[2] = (arg[2] instanceof GeoNumberValue))) {
				
				AlgoRoots algo = new AlgoRoots(cons, c.getLabels(),
						((GeoFunctionable) arg[0]).getGeoFunction(),
						(GeoNumberValue) arg[1], (GeoNumberValue) arg[2]);

				GeoElement[] ret = algo.getRootPoints();
				return ret;
			} 
			throw argErr(app,c.getName(),getBadArg(ok,arg));

		default:
			throw argNumErr(app, c.getName(), n);
		}//switch
	}//process(command)
}//class CmdRoots
