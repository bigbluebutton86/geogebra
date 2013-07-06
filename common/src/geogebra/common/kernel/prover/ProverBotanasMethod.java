package geogebra.common.kernel.prover;

import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoCircleThreePoints;
import geogebra.common.kernel.algos.AlgoCircleTwoPoints;
import geogebra.common.kernel.algos.SymbolicParametersBotanaAlgo;
import geogebra.common.kernel.algos.SymbolicParametersBotanaAlgoAre;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.prover.polynomial.Polynomial;
import geogebra.common.kernel.prover.polynomial.Variable;
import geogebra.common.main.App;
import geogebra.common.main.ProverSettings;
import geogebra.common.util.Prover;
import geogebra.common.util.Prover.NDGCondition;
import geogebra.common.util.Prover.ProofResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A prover which uses Francisco Botana's method to prove geometric theorems.
 * 
 * @author Zoltan Kovacs
 *
 */
public class ProverBotanasMethod {

	/**
	 * Inverse mapping of botanaVars.
	 */
    private static HashMap<List<Variable>,GeoElement> botanaVarsInv;

    private static void updateBotanaVarsInv(GeoElement statement) {
    	if (botanaVarsInv == null)
    		botanaVarsInv = new HashMap<List<Variable>,GeoElement>();
    	Iterator<GeoElement> it = statement.getAllPredecessors().iterator();
    	while (it.hasNext()) {
    		GeoElement geo = it.next();
    		Variable[] vars = ((SymbolicParametersBotanaAlgo) geo).getBotanaVars(geo);
    		if (vars != null) {
    			List<Variable> varsList = Arrays.asList(vars);
    			botanaVarsInv.put(varsList, geo);
    		}
    	}
    }
    
	protected static List<GeoElement> getFreePoints(GeoElement statement) {
		List<GeoElement> freePoints = new ArrayList<GeoElement>();
		Iterator<GeoElement> it = statement.getAllPredecessors().iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (geo.isGeoPoint() && geo.getParentAlgorithm() == null) { // this is a free point
				freePoints.add(geo);
			}
		}
		return freePoints;
	}

	// We don't use this at the moment. It seemed to be useful to select the best coordinates to fix from circle centers
	// but finally there is no test case for this at the moment to be convinced if this really helps in speed.
	private static List<GeoElement> getCircleCenters(GeoElement statement) {
		List<GeoElement> circleCenters = new ArrayList<GeoElement>();
		Iterator<GeoElement> it = statement.getAllPredecessors().iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (geo.isGeoConic()) { // this is probably a circle
				if (geo.getParentAlgorithm() instanceof AlgoCircleTwoPoints
						|| geo.getParentAlgorithm() instanceof AlgoCircleThreePoints) {
					// Search for the center point.
					Variable[] vars = ((SymbolicParametersBotanaAlgo) geo).getBotanaVars(geo);
					Variable[] center = new Variable[2];
					center[0] = vars[0];
					center[1] = vars[1];
					GeoElement centerGeo = botanaVarsInv.get(Arrays.asList(center));
					if (centerGeo != null) // it may be a virtual center (TODO: handle somehow)
						circleCenters.add(centerGeo);
				}
			}
		}
		return circleCenters;
	}

	/** 
	 * Creates those polynomials which describe that none of 3 free points
	 * can lie on the same line. 
	 * @return the NDG polynomials (in denial form)
	 */
	private static Polynomial[] create3FreePointsNeverCollinearNDG(Prover prover) {
		// Creating the set of free points first:
		List<GeoElement> freePoints = getFreePoints(prover.getStatement());
		int setSize = freePoints.size();

		// Creating NDGs:
		NDGCondition ndgc = new NDGCondition();
		if (setSize > 3)
			ndgc.setCondition("DegeneratePolygon");
		else
			ndgc.setCondition("AreCollinear");
		GeoElement[] geos = new GeoElement[setSize]; 
		int i = 0;
		Iterator<GeoElement> it = freePoints.iterator();
		while (it.hasNext()) {
			geos[i++] = it.next();
		}
		ndgc.setGeos(geos);
		Arrays.sort(ndgc.getGeos());
		prover.addNDGcondition(ndgc);
		
		// The output will contain $\binom{n}{3}$ elements:
		Polynomial[] ret = new Polynomial[setSize * (setSize - 1) * (setSize - 2) / 6];
		i = 0;
		// Creating the set of triplets:
		HashSet<HashSet<GeoElement>> triplets = new HashSet<HashSet<GeoElement>>();
		Iterator<GeoElement> it1 = freePoints.iterator();
		while (it1.hasNext()) {
			GeoElement geo1 = it1.next();
			Iterator<GeoElement> it2 = freePoints.iterator();
			while (it2.hasNext()) {
				GeoElement geo2 = it2.next();
				if (!geo1.isEqual(geo2)) {
					Iterator<GeoElement> it3 = freePoints.iterator();
					while (it3.hasNext()) {
						GeoElement geo3 = it3.next();
						if (!geo1.isEqual(geo3) && !geo2.isEqual(geo3)) {
							HashSet<GeoElement> triplet = new HashSet<GeoElement>();
							triplet.add(geo1);
							triplet.add(geo2);
							triplet.add(geo3);
							// Only the significantly new triplets will be processed:
							if (!triplets.contains(triplet)) {
								triplets.add(triplet);
								Variable[] fv1 = ((SymbolicParametersBotanaAlgo)geo1).getBotanaVars(geo1);
								Variable[] fv2 = ((SymbolicParametersBotanaAlgo)geo2).getBotanaVars(geo2);
								Variable[] fv3 = ((SymbolicParametersBotanaAlgo)geo3).getBotanaVars(geo3);
								// Creating the polynomial for collinearity:
								Polynomial p = Polynomial.collinear(fv1[0], fv1[1],
										fv2[0], fv2[1], fv3[0], fv3[1]);
								App.debug("Forcing non-collinearity for points "
										+ geo1.getLabelSimple() + ", "
										+ geo2.getLabelSimple() + " and "
										+ geo3.getLabelSimple());
								// Rabinowitsch trick for prohibiting collinearity:
								ret[i] = p.multiply(new Polynomial(new Variable())).subtract(new Polynomial(1));
								// FIXME: this always introduces an extra variable, shouldn't do
								i++;
							}
						}
					}
				}
			}
		}
		return ret;
	}
	
	/**
	 * Uses a minimal heuristics to fix the first four variables to certain "easy" numbers.
	 * The first two variables (usually the coordinates of the first point) are set to 0,
	 * and the second two variables (usually the coordinates of the second point) are set to 0 and 1.
	 * @param prover the input prover
	 * @param coords number of fixed coordinates
	 * @return a HashMap, containing the substitutions
	 */
	static HashMap<Variable,Integer> fixValues(Prover prover, int coords) {
		
		int[] fixCoords = {0, 0, 0, 1};
		
		GeoElement statement = prover.getStatement();
		List<GeoElement> freePoints = getFreePoints(statement);
		List<GeoElement> fixedPoints = new ArrayList<GeoElement>();
		// Adding free points:
		for (GeoElement ge : freePoints) {
			fixedPoints.add(ge);
		}
		
		HashMap<Variable,Integer> ret = new HashMap<Variable, Integer>();
		
		Iterator<GeoElement> it = fixedPoints.iterator();
		GeoElement[] geos = new GeoElement[2];
		int i = 0, j = 0;
		while (it.hasNext() && i < 2 && j < coords) {
			GeoElement geo = it.next();
			Variable[] fv = ((SymbolicParametersBotanaAlgo) geo).getBotanaVars(geo);
			geos[i] = geo;
			ret.put(fv[0], fixCoords[j]);
			++j;
			if (j < coords) {
				ret.put(fv[1], fixCoords[j]);
				++i;
				++j;
			}
		}

		// We implicitly assumed that the first two points are different:
		if (i == 2 && prover.isReturnExtraNDGs()) {
			NDGCondition ndgc = new NDGCondition();
			ndgc.setCondition("AreEqual");
			ndgc.setGeos(geos);
			Arrays.sort(ndgc.getGeos());
			prover.addNDGcondition(ndgc);
		}
		return ret;
	}
	
	/**
	 * Proves the statement by using Botana's method
	 * @param prover the prover input object 
	 * @return if the statement is true
	 */
	public static ProofResult prove(geogebra.common.util.Prover prover) {
		// Getting the hypotheses:
		Polynomial[] hypotheses = null;
		GeoElement statement = prover.getStatement();
		Iterator<GeoElement> it = statement.getAllPredecessors().iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			// AbstractApplication.debug(geo);
			if (geo instanceof SymbolicParametersBotanaAlgo) {
				try {
					App.debug("/* PROCESSING OBJECT " + geo.getLabelSimple() + " */");
					String command = geo.getCommandDescription(StringTemplate.noLocalDefault);
					if (!("".equals(command))) {
						App.debug("/* Command definition */");
						App.debug(geo.getLabelSimple() + " = " +
							geo.getCommandDescription(StringTemplate.noLocalDefault) + " /* " +
					 		geo.getDefinitionDescription(StringTemplate.noLocalDefault) + " */");
					} else {
						String description = geo.getAlgebraDescriptionDefault();
						if (!description.startsWith("xOyPlane")) // handling GeoGebra3D's definition for xy-plane
							App.debug(description + " /* free point */");
					}
					Polynomial[] geoPolys = ((SymbolicParametersBotanaAlgo) geo).getBotanaPolynomials(geo);

					if (geoPolys != null) {
						Variable[] v = new Variable[2];
						v = ((SymbolicParametersBotanaAlgo) geo).getBotanaVars(geo);
						App.debug("Constrained point " + geo.getLabelSimple() + "(" + v[0] + "," + v[1] + ")");
						int nHypotheses = 0;
						if (hypotheses != null)
							nHypotheses = hypotheses.length;
						Polynomial[] allPolys = new Polynomial[nHypotheses + geoPolys.length];
						for (int i=0; i<nHypotheses; ++i) {
							allPolys[i] = hypotheses[i];
						}

						App.debug("Hypotheses:");
						for (int i=0; i<geoPolys.length; ++i) {
							App.debug((nHypotheses + i + 1) + ". " + geoPolys[i]);
							allPolys[nHypotheses + i] = geoPolys[i];
						}
						hypotheses = allPolys;
					
					}
				} catch (NoSymbolicParametersException e) {
					App.debug(geo.getParentAlgorithm() + " is not fully implemented");
					return ProofResult.UNKNOWN;
				}
			}
			else {
				App.debug(geo.getParentAlgorithm() + " unimplemented");
				return ProofResult.UNKNOWN;
			}
		}
		updateBotanaVarsInv(statement);
		try {
			// The sets of statement polynomials.
			// The last equation of each set will be negated.
			if (!(statement.getParentAlgorithm() instanceof SymbolicParametersBotanaAlgoAre)) {
				App.debug(statement.getParentAlgorithm() + " unimplemented");
				return ProofResult.UNKNOWN;
			}
				
			Polynomial[][] statements = ((SymbolicParametersBotanaAlgoAre) statement.getParentAlgorithm()).getBotanaPolynomials();
			// The NDG conditions (automatically created):
			Polynomial[] ndgConditions = null;
			if (ProverSettings.freePointsNeverCollinear)
				ndgConditions = create3FreePointsNeverCollinearNDG(prover);
			HashMap<Variable,Integer> substitutions = null;
			int fixcoords = 0;
			if (prover.isReturnExtraNDGs())
				fixcoords = ProverSettings.useFixCoordinatesProveDetails;
			else
				fixcoords = ProverSettings.useFixCoordinatesProve;
			if (fixcoords > 0) {
				substitutions = fixValues(prover, fixcoords);
				App.debug("substitutions: " + substitutions);
			}
			int nHypotheses = 0;
			int nNdgConditions = 0;
			int nStatements = 0;
			if (hypotheses != null)
				nHypotheses = hypotheses.length;
			if (ndgConditions != null)
				nNdgConditions = ndgConditions.length;
			if (statements != null)
				nStatements = statements.length;
						
			boolean ans = true;
			// Solving the equation system for each sets of polynomials of the statement:
			for (int i=0; i<nStatements && ans; ++i) {
				int nPolysStatement = statements[i].length;
				Polynomial[] eqSystem = new Polynomial[nHypotheses + nNdgConditions + nPolysStatement];
				// These polynomials will be in the equation system always:
				for (int j=0; j<nHypotheses; ++j)
					eqSystem[j] = hypotheses[j];
				if (nNdgConditions > 0)
					App.debug("Extra NDGs:");
				for (int j=0; j<nNdgConditions; ++j) {
					App.debug((j + nHypotheses + 1) + ". " + ndgConditions[j]);
					eqSystem[j + nHypotheses] = ndgConditions[j];
				}
				if (nPolysStatement > 1)
					App.debug("Statement equations (non-denied parts):");
				for (int j=0; j<nPolysStatement - 1; ++j) {
					App.debug((j + nHypotheses + nNdgConditions + 1) + ". " + statements[i][j]);
					eqSystem[j + nHypotheses + nNdgConditions] = statements[i][j];
				}

				// Rabinowitsch trick for the last polynomial of the current statement:
				Polynomial spoly = statements[i][nPolysStatement - 1].multiply(new Polynomial(new Variable())).subtract(new Polynomial(1));
				// FIXME: this always introduces an extra variable, shouldn't do
				App.debug("Thesis reductio ad absurdum (denied statement):");
				eqSystem[nHypotheses + nNdgConditions + nPolysStatement - 1] = spoly;
				App.debug((nHypotheses + nNdgConditions + nPolysStatement) + ". " + spoly);
				
				if (prover.isReturnExtraNDGs()) {
					eqSystem[nHypotheses + nPolysStatement - 1] = spoly;				
					
					Set<Set<Polynomial>> eliminationIdeal = Polynomial.eliminate(eqSystem, substitutions);
					if (eliminationIdeal == null){
						return ProofResult.UNKNOWN;
					}
					
					Iterator<Set<Polynomial>> ndgSet = eliminationIdeal.iterator();
					boolean found = false;
					while (ndgSet.hasNext() && !found) {
						List<NDGCondition> ndgcl = new ArrayList<NDGCondition>();
						// All NDGs must be translatable into human readable form.
						boolean readable = true;
						Iterator<Polynomial> ndg = ndgSet.next().iterator();
						while (ndg.hasNext() && readable) {
							Polynomial poly = ndg.next();
							if (poly.isZero())
								return ProofResult.FALSE;
							NDGCondition ndgc = NDGDetector.detect(poly, prover);
							if (ndgc == null)
								readable = false;
							else {
								ndgcl.add(ndgc);
							}
						}
						// Now we take the first set with readable conditions.
						// Later we will change to select the most educational set.
						if (readable) {
							Iterator<NDGCondition> ndgc = ndgcl.iterator();
							while (ndgc.hasNext()) {
								prover.addNDGcondition(ndgc.next());
							}
							found = true;
						}
						ans = true;
						// TODO: If !found, add an ndgc with "Unknown" or "Object" item.
					}
				} else {
					if (Polynomial.solvable(eqSystem, substitutions, statement.getKernel(),
							ProverSettings.polysofractf)) // FIXME: here seems NPE if SingularWS not initialized 
						ans = false;
				}
			}

			if (ans)
				return ProofResult.TRUE;
			
			return ProofResult.FALSE;
		} catch (NoSymbolicParametersException e) {
			return ProofResult.UNKNOWN;
		}
	}
	
}
