package geogebra.common.kernel.commands;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoFocus;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.main.MyError;

/**
 * Focus[ <GeoConic> ]
 */
public class CmdFocus extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFocus(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoConic()) {
				
				AlgoFocus algo = newAlgoFocus(cons, c.getLabels(), (GeoConicND) arg[0]);
				return (GeoElement[]) algo.getFocus();

			}
			throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

	/**
	 * 
	 * @param cons
	 * @param labels
	 * @param c
	 * @return new AlgoFocus
	 */
	protected AlgoFocus newAlgoFocus(Construction cons, String[] labels, GeoConicND c){
		return new AlgoFocus(cons, labels, c);
	}
}
