package geogebra.common.kernel.prover.polynomial;

import geogebra.common.cas.GeoGebraCAS;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * This is a simple polynomial class for polynomials with arbitrary many
 * variables.
 * 
 * @author Simon Weitzhofer
 * 
 */
public class Polynomial implements Comparable<Polynomial> {
	private TreeMap<Term, Integer> terms;

	/**
	 * Creates the 0 polynomial
	 */
	public Polynomial() {
		terms = new TreeMap<Term, Integer>();
	}

	/**
	 * Copies a polynomial
	 * 
	 * @param poly
	 *            the polynomial to copy
	 */
	public Polynomial(final Polynomial poly) {
		terms = new TreeMap<Term, Integer>(poly.getTerms());
	}

	private Polynomial(final TreeMap<Term, Integer> terms) {
		this.terms = terms;
	}

	/**
	 * Getter for the map which contains the terms and the according
	 * coefficients.
	 * 
	 * @return the map
	 */
	public TreeMap<Term, Integer> getTerms() {
		return terms;
	}

	/**
	 * Creates a constant polynomial.
	 * 
	 * @param coeff
	 *            the constant
	 */
	public Polynomial(final int coeff) {
		this(coeff, new Term());
	}

	/**
	 * Creates a polynomial which contains only one variable
	 * 
	 * @param fv
	 *            the variable
	 */
	public Polynomial(final Variable fv) {
		this();
		terms.put(new Term(fv), 1);
	}

	/**
	 * Creates the polynomial coeff*variable
	 * 
	 * @param coeff
	 *            the coefficient
	 * @param variable
	 *            the variable
	 */
	public Polynomial(final int coeff, final Variable variable) {
		this();
		terms.put(new Term(variable), coeff);
	}

	/**
	 * Creates the polynomial coeff*(variable^power)
	 * 
	 * @param coeff
	 *            The coefficient
	 * @param variable
	 *            the variable
	 * @param power
	 *            the exponent
	 */
	public Polynomial(final int coeff, final Variable variable,
			final int power) {
		this();
		terms.put(new Term(variable, power), coeff);
	}

	/**
	 * Creates the polynomial which contains only one term
	 * 
	 * @param t
	 *            the term
	 */
	public Polynomial(final Term t) {
		this();
		terms.put(t, 1);
	}

	/**
	 * Creates the polynomial coeff*t
	 * 
	 * @param coeff
	 *            the coefficient
	 * @param t
	 *            the term
	 */
	public Polynomial(final int coeff, final Term t) {
		this();
		terms.put(t, coeff);
	}
	
	/**
	 * Returns the sum of the polynomial plus another polynomial.
	 * 
	 * @param poly
	 *            the polynomial to add
	 * @return the sum
	 */
	public Polynomial add(final Polynomial poly) {
		TreeMap<Term, Integer> result = new TreeMap<Term, Integer>(terms);
		TreeMap<Term, Integer> terms2 = poly.getTerms();
		Iterator<Term> it = terms2.keySet().iterator();
		while (it.hasNext()) {
			Term t = it.next();
			if (terms.containsKey(t)) {
				int coefficient = terms.get(t) + terms2.get(t);
				if (coefficient == 0) {
					result.remove(t);
				} else {
					result.put(t, terms.get(t) + terms2.get(t));
				}
			} else {
				result.put(t, terms2.get(t));
			}
		}
		return new Polynomial(result);
	}

	/**
	 * Calculates the additive inverse of the polynomial
	 * 
	 * @return the negation of the polynomial
	 */
	public Polynomial negate() {
		TreeMap<Term, Integer> result = new TreeMap<Term, Integer>();
		Iterator<Term> it = terms.keySet().iterator();
		while (it.hasNext()) {
			Term t = it.next();
			result.put(t, 0 - terms.get(t));
		}
		return new Polynomial(result);
	}

	/**
	 * Subtracts another polynomial
	 * 
	 * @param poly
	 *            the polynomial which is subtracted
	 * @return the difference
	 */
	public Polynomial subtract(final Polynomial poly) {
		return add(poly.negate());
	}

	/**
	 * Multiplies the polynomial with another polynomial
	 * 
	 * @param poly
	 *            the polynomial which is multiplied
	 * @return the product
	 */
	public Polynomial multiply(final Polynomial poly) {

		/*
		if (AbstractApplication.singularWS != null && AbstractApplication.singularWS.isAvailable()) {
			if (poly.toString().length()>100 && this.toString().length()>100) {
				String singularMultiplicationProgram = getSingularMultiplication("rr", poly, this);
				AbstractApplication.trace(singularMultiplicationProgram.length() + " bytes -> singular");
				String singularMultiplication = AbstractApplication.singularWS.directCommand(singularMultiplicationProgram);
				return new Polynomial(singularMultiplication);
			}
		}
		*/
		
		TreeMap<Term, Integer> result = new TreeMap<Term, Integer>();
		TreeMap<Term, Integer> terms2 = poly.getTerms();
		Iterator<Term> it1 = terms.keySet().iterator();
		while (it1.hasNext()) {
			Term t1 = it1.next();
			Iterator<Term> it2 = terms2.keySet().iterator();
			while (it2.hasNext()) {
				Term t2 = it2.next();
				Term product = t1.times(t2);
				int productCoefficient = terms.get(t1) * terms2.get(t2);
				if (result.containsKey(product)) {
					int sum = result.get(product) + productCoefficient;
					if (sum == 0) {
						result.remove(product);
					} else {
						result.put(product, result.get(product)
								+ productCoefficient);
					}
				} else {
					result.put(product, productCoefficient);
				}
			}

		}
		return new Polynomial(result);
	}

	public int compareTo(Polynomial poly) {
		if (this==poly){
			return 0;
		}

		TreeMap<Term, Integer> polyVars=poly.getTerms();
		if (polyVars.isEmpty()) {
			if (terms.isEmpty()) {
				return 0;
			}
			return 1;
		}
		if (terms.isEmpty()) {
			return -1;
		}
		
		Term termsLastKey=terms.lastKey(),
				polyVarsLastKey=polyVars.lastKey();

		int compare = termsLastKey.compareTo(polyVarsLastKey);

		if (compare == 0) {
			compare = terms.get(termsLastKey).compareTo(polyVars.get(polyVarsLastKey));
		}

		if (compare != 0) {
			return compare;
		}
		
		do {
			SortedMap<Term, Integer> termsSub = terms.headMap(termsLastKey);
			SortedMap<Term, Integer> oSub = polyVars.headMap(polyVarsLastKey);
			if (termsSub.isEmpty()) {
				if (oSub.isEmpty()) {
					return 0;
				}
				return -1;
			}
			if (oSub.isEmpty()) {
				return 1;
			}
			termsLastKey=termsSub.lastKey();
			polyVarsLastKey=oSub.lastKey();
			compare = termsLastKey.compareTo(polyVarsLastKey);
			if (compare == 0) {
				compare = termsSub.get(termsLastKey).compareTo(
						oSub.get(polyVarsLastKey));
			}
		} while (compare == 0);

		return compare;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator<Term> it = terms.keySet().iterator();
		if (!it.hasNext()) {
			return "0";
		}
		while (it.hasNext()) {
			Term t = it.next();
			int c = terms.get(t);
			if (!t.getTerm().isEmpty()) {
				if (c != 1)
					sb.append(c + "*");
				sb.append(t);
			}
			else
				sb.append(c);
			sb.append('+');
		}
		return sb.substring(0, sb.length() - 1); // removing closing "+"
	}
	
	/**
	 * The set of the variables in this polynomial
	 * @return the set of variables
	 */
	public HashSet<Variable> getVars() {
		HashSet<Variable> v = new HashSet<Variable>();
		Iterator<Term> it = terms.keySet().iterator();
		while (it.hasNext()) {
			Term t = it.next();
			v.addAll(t.getVars());
		}
		return v;
	}

	/**
	 * The set of the variables in the given polynomials
	 * @param polys the polynomials
	 * @return the set of variables
	 */
	public static HashSet<Variable> getVars(Polynomial[] polys) {
		HashSet<Variable> v = new HashSet<Variable>();
		int polysLength = 0;
		if (polys != null)
			polysLength = polys.length;
		for (int i=0; i<polysLength; ++i) {
			HashSet<Variable> vars = polys[i].getVars();
			if (vars != null)
				v.addAll(vars);
		}
		return v;
	}
	
	/**
	 * Creates a comma separated list of the variables in the given polynomials
	 * @param polys the polynomials
	 * @param extraVars (maybe) extra variables (typically substituted variables)
	 * @param free filter the query if the variables are free or dependant (or any if null)
	 * @return the comma separated list
	 */
	public static String getVarsAsCommaSeparatedString(Polynomial[] polys, HashSet<Variable> extraVars, Boolean free) {
		StringBuilder sb = new StringBuilder();
		HashSet<Variable> vars = getVars(polys);
		if (extraVars != null)
			vars.addAll(extraVars);
		Iterator<Variable> it = vars.iterator();
		while (it.hasNext()) {
			Variable fv = it.next();
			if ((free == null) || (free && fv.isFree()) || (!free && !fv.isFree()))
				sb.append("," + fv);		
		}
		if (sb.length()>0)
			return sb.substring(1); // removing first "," character
		return "";
	}
	
	/**
	 * Creates a comma separated list of the given polynomials
	 * @param polys the polynomials
	 * @return the comma separated list
	 */
	public static String getPolysAsCommaSeparatedString(Polynomial[] polys) {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<polys.length; ++i)
			sb.append("," + polys[i].toString());
		if (sb.length()>0)
			return sb.substring(1); // removing first "," character
		return "";
	}
	
	/**
	 * Creates a Singular program for creating a ring to work with two
	 * polynomials, and multiply them; adds a closing ";" 
	 * @param ringVariable variable name for the ring in Singular
	 * @param p1 first polynomial
	 * @param p2 second polynomial
	 * @return the Singular program code
	 */
	public String getSingularMultiplication(String ringVariable, Polynomial p1, Polynomial p2) {
		String vars = getVarsAsCommaSeparatedString(new Polynomial[] {p1, p2}, null, null);
		if (vars != "")
			return "ring " + ringVariable + "=0,(" 
				+ vars
				+ "),dp;" // ring definition in Singular
				
				+ "short=0;" // switching off short output
				
				+ "(" + p1.toString() + ")"
				+ "*"
				+ "(" + p2.toString() + ");"; // the multiplication command
		return p1.toString() + "*" + p2.toString() + ";";
	}
	
	/**
	 * Creates a polynomial which describes the input coordinates as points
	 * lying on the same line. 
	 * @param fv1 x-coordinate of the first point
	 * @param fv2 y-coordinate of the first point
	 * @param fv3 x-coordinate of the second point
	 * @param fv4 y-coordinate of the second point
	 * @param fv5 x-coordinate of the third point
	 * @param fv6 y-coordinate of the third point
	 * @return the polynomial
	 */
	public static Polynomial collinear(Variable fv1, Variable fv2, Variable fv3, 
			Variable fv4, Variable fv5, Variable fv6) {
		App.debug("Setting up equation for collinear points " +
			"(" + fv1 + "," + fv2 + "), " +
			"(" + fv3 + "," + fv4 + ") and " +
			"(" + fv5 + "," + fv6 + ")");
		// a*d-b*c:
		Polynomial a = new Polynomial(fv1);
		Polynomial b = new Polynomial(fv2);
		Polynomial c = new Polynomial(fv3);
		Polynomial d = new Polynomial(fv4);
		Polynomial e = new Polynomial(fv5);
		Polynomial f = new Polynomial(fv6);
		
		Polynomial ret = a.multiply(d).subtract(b.multiply(c))
				// + e*(b-d)
				.add(e.multiply(b.subtract(d)))
				// - f*(a-c)
				.subtract(f.multiply(a.subtract(c)));
		return ret;
	}
	
	/**
	 * Creates a polynomial which describes the input coordinates as points
	 * are perpendicular, i.e. AB is perpendicular to CD. 
	 * @param v1 x-coordinate of the first point (A)
	 * @param v2 y-coordinate of the first point (A)
	 * @param v3 x-coordinate of the second point (B)
	 * @param v4 y-coordinate of the second point (B)
	 * @param v5 x-coordinate of the third point (C)
	 * @param v6 y-coordinate of the third point (C)
	 * @param v7 x-coordinate of the fourth point (D)
	 * @param v8 y-coordinate of the fourth point (D)
	 * @return the polynomial
	 */
	public static Polynomial perpendicular(Variable v1, Variable v2, Variable v3, 
			Variable v4, Variable v5, Variable v6, Variable v7, Variable v8) {

		App.debug("Setting up equation for perpendicular lines " +
				"(" + v1 + "," + v2 + ")-" +
				"(" + v3 + "," + v4 + ") and " +
				"(" + v5 + "," + v6 + ")-" +
				"(" + v7 + "," + v8 + ")");
		
		Polynomial a1 = new Polynomial(v1);
		Polynomial a2 = new Polynomial(v2);
		Polynomial b1 = new Polynomial(v3);
		Polynomial b2 = new Polynomial(v4);
		Polynomial c1 = new Polynomial(v5);
		Polynomial c2 = new Polynomial(v6);
		Polynomial d1 = new Polynomial(v7);
		Polynomial d2 = new Polynomial(v8);
		
		// (a1-b1)*(c1-d1)+(a2-b2)*(c2-d2)
		Polynomial ret = ((a1.subtract(b1)).multiply(c1.subtract(d1)))
				.add((a2.subtract(b2)).multiply(c2.subtract(d2)));
		return ret;
	}

	/**
	 * Creates a polynomial which describes the input coordinates as points
	 * are parallel, i.e. AB is parallel to CD. 
	 * @param v1 x-coordinate of the first point (A)
	 * @param v2 y-coordinate of the first point (A)
	 * @param v3 x-coordinate of the second point (B)
	 * @param v4 y-coordinate of the second point (B)
	 * @param v5 x-coordinate of the third point (C)
	 * @param v6 y-coordinate of the third point (C)
	 * @param v7 x-coordinate of the fourth point (D)
	 * @param v8 y-coordinate of the fourth point (D)
	 * @return the polynomial
	 */
	public static Polynomial parallel(Variable v1, Variable v2, Variable v3, 
			Variable v4, Variable v5, Variable v6, Variable v7, Variable v8) {

		App.debug("Setting up equation for parallel lines " +
				"(" + v1 + "," + v2 + ")-" +
				"(" + v3 + "," + v4 + ") and " +
				"(" + v5 + "," + v6 + ")-" +
				"(" + v7 + "," + v8 + ")");
		
		Polynomial a1 = new Polynomial(v1);
		Polynomial a2 = new Polynomial(v2);
		Polynomial b1 = new Polynomial(v3);
		Polynomial b2 = new Polynomial(v4);
		Polynomial c1 = new Polynomial(v5);
		Polynomial c2 = new Polynomial(v6);
		Polynomial d1 = new Polynomial(v7);
		Polynomial d2 = new Polynomial(v8);

		// (a1-b1)*(c2-d2)-(a2-b2)*(c1-d1)
		Polynomial ret = ((a1.subtract(b1)).multiply(c2.subtract(d2)))
				.subtract((a2.subtract(b2)).multiply(c1.subtract(d1)));
		return ret;
	}
	
	/**
	 * Calculates the determinant of a 4 times 4 matrix
	 * @param matrix matrix
	 * @return the determinant
	 */
	public static Polynomial det4(final Polynomial[][] matrix){
		return matrix[0][3].multiply(matrix[1][2].multiply(matrix[2][1].multiply(matrix[3][0]))).subtract(
				matrix[0][2].multiply(matrix[1][3]).multiply(matrix[2][1]).multiply(matrix[3][0])).subtract(
				matrix[0][3].multiply(matrix[1][1]).multiply(matrix[2][2]).multiply(matrix[3][0])).add(
				matrix[0][1].multiply(matrix[1][3]).multiply(matrix[2][2]).multiply(matrix[3][0])).add(
				matrix[0][2].multiply(matrix[1][1]).multiply(matrix[2][3]).multiply(matrix[3][0])).subtract(
				matrix[0][1].multiply(matrix[1][2]).multiply(matrix[2][3]).multiply(matrix[3][0])).subtract(
				matrix[0][3].multiply(matrix[1][2]).multiply(matrix[2][0]).multiply(matrix[3][1])).add(
				matrix[0][2].multiply(matrix[1][3]).multiply(matrix[2][0]).multiply(matrix[3][1])).add(
				matrix[0][3].multiply(matrix[1][0]).multiply(matrix[2][2]).multiply(matrix[3][1])).subtract(
				matrix[0][0].multiply(matrix[1][3]).multiply(matrix[2][2]).multiply(matrix[3][1])).subtract(
				matrix[0][2].multiply(matrix[1][0]).multiply(matrix[2][3]).multiply(matrix[3][1])).add(
				matrix[0][0].multiply(matrix[1][2]).multiply(matrix[2][3]).multiply(matrix[3][1])).add(
				matrix[0][3].multiply(matrix[1][1]).multiply(matrix[2][0]).multiply(matrix[3][2])).subtract(
				matrix[0][1].multiply(matrix[1][3]).multiply(matrix[2][0]).multiply(matrix[3][2])).subtract(
				matrix[0][3].multiply(matrix[1][0]).multiply(matrix[2][1]).multiply(matrix[3][2])).add(
				matrix[0][0].multiply(matrix[1][3]).multiply(matrix[2][1]).multiply(matrix[3][2])).add(
				matrix[0][1].multiply(matrix[1][0]).multiply(matrix[2][3]).multiply(matrix[3][2])).subtract(
				matrix[0][0].multiply(matrix[1][1]).multiply(matrix[2][3]).multiply(matrix[3][2])).subtract(
				matrix[0][2].multiply(matrix[1][1]).multiply(matrix[2][0]).multiply(matrix[3][3])).add(
				matrix[0][1].multiply(matrix[1][2]).multiply(matrix[2][0]).multiply(matrix[3][3])).add(
				matrix[0][2].multiply(matrix[1][0]).multiply(matrix[2][1]).multiply(matrix[3][3])).subtract(
				matrix[0][0].multiply(matrix[1][2]).multiply(matrix[2][1]).multiply(matrix[3][3])).subtract(
				matrix[0][1].multiply(matrix[1][0]).multiply(matrix[2][2]).multiply(matrix[3][3])).add(
				matrix[0][0].multiply(matrix[1][1]).multiply(matrix[2][2]).multiply(matrix[3][3]));
	}
	
	/** 
	 * Calculates the cross product of two vectors of dimension three.
	 * @param a the first vector
	 * @param b the second vector
	 * @return the cross product of the two vectors
	 */
	public static Polynomial[] crossProduct(Polynomial[] a,
			Polynomial[] b) {
		Polynomial[] result=new Polynomial[3];
		result[0]=(a[1].multiply(b[2])).subtract(a[2].multiply(b[1]));
		result[1]=(a[2].multiply(b[0])).subtract(a[0].multiply(b[2]));
		result[2]=(a[0].multiply(b[1])).subtract(a[1].multiply(b[0]));
		return result;
	}
	
	/**
	 * Substitutes variables in the polynomial by integer values
	 * 
	 * @param substitutions
	 *            A map of the substitutions (not null)
	 * @return a new polynomial with the variables substituted.
	 */
	public Polynomial substitute(Map<Variable, Integer> substitutions) {
		
		TreeMap<Term, Integer> result = new TreeMap<Term, Integer>();

		Iterator<Term> it = terms.keySet().iterator();
		while (it.hasNext()) {
			Term t1 = it.next();
			TreeMap<Variable, Integer> term = new TreeMap<Variable, Integer>(t1.getTerm());
			BigInteger product = BigInteger.ONE;
			Iterator<Variable> itSubst = substitutions.keySet().iterator();
			while (itSubst.hasNext()) {
				Variable variable = itSubst.next();
				Integer exponent = term.get(variable);
				if (exponent != null) {
					product = product.multiply(BigInteger.valueOf(
							substitutions.get(variable)).pow(exponent));
					term.remove(variable);
				}
			}
			product = product.multiply(BigInteger.valueOf(terms.get(t1)));
			Term t = new Term(term);
			if (result.containsKey(t)) {
				BigInteger sum = BigInteger.valueOf(result.get(t)).add(
						product);
//				if (sum.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > -1) {
//					throw new ArithmeticException(
//							"Integer Overflow in polynomial class");
//				}
				if (sum.intValue() == 0) {
					result.remove(t);
				} else {
					result.put(t, sum.intValue());
				}
			} else if (product.intValue() != 0){
//				if (product.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > -1) {
//					throw new ArithmeticException(
//							"Integer Overflow in polynomial class");
//				}
				result.put(t, product.intValue());
			}
		}
		return new Polynomial(result);
	}

	
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Polynomial) {
			return this.compareTo((Polynomial) o) == 0;
		}
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return terms.hashCode();
	}

	/**
	 * Tests if the Polynomial is the zero polynomial
	 * @return true if the polynomial is zero false otherwise
	 */
	public boolean isZero() {
		return terms.isEmpty();
	}
	
	/**
	 * Tests if the Polynomial is the constant one polynomial
	 * @return true if the polynomial is zero false otherwise
	 */
	public boolean isOne() {
		return equals(new Polynomial(1));
	}
	

	/**
	 * Converts substitutions to Singular strings
	 * @param subst input as a HashMap
	 * @return the parameters for Singular (e.g. "v1,0,v2,0,v3,0,v4,1")
	 */
	static String substitutionsString(HashMap<Variable,Integer> subst) {
		String ret = "";
		Iterator<Variable> it = subst.keySet().iterator();
		while (it.hasNext()) {
			Variable v = it.next();
			ret += "," + v.toString() + "," + subst.get(v);
		}
		if (ret.length()>0)
			return ret.substring(1);
		return "";
	}
		
	/**
	 * Adds a leading comma to the input string if it not empty
	 * @param in input
	 * @return output string
	 */
	public static String addLeadingComma (String in) {
		if (in == null || in.length() == 0)
			return "";
		return "," + in;
	}
	
	/**
	 * Returns in1 if it is not empty, in2 otherwise
	 * @param in1 input1
	 * @param in2 input2
	 * @return the first non-empty input
	 */
	public static String coalesce (String in1, String in2) {
		if (in1 == null || in1.length() == 0)
			return in2;
		return in1;
	}
	
	/**
	 * Creates a Singular program for creating a ring to work with several
	 * polynomials, and returns if the equation system has a solution. Uses
	 * the Groebner basis w.r.t. the revgradlex order.
	 * @param ringVariable variable name for the ring in Singular
	 * @param idealVariable variable name for the ideal in Singular
	 * @param dummyVar dummy variable name
	 * @param substitutions HashMap with variables and values, e.g. {v1->0},{v2->1}
	 * @param polys polynomials, e.g. "v1+v2-3*v4-10"
	 * @param fieldVars field variables (comma separated) 
	 * @param ringVars ring variables (comma separated)
	 * @param polysofractf use polynomial ring over a fraction field of polynomials (rational functions)
	 * @return the Singular program code
	 */
	public static String createGroebnerSolvableScript(String ringVariable, String idealVariable,
			String dummyVar, HashMap<Variable,Integer> substitutions, String polys,
			String fieldVars, String ringVars, boolean polysofractf) {
		
		String vars = ringVars + addLeadingComma(fieldVars); 
		
		String substCommand = "";
		if (substitutions != null) {
			String substParams = substitutionsString(substitutions);
			substCommand = idealVariable + "=subst(" + idealVariable + "," + substParams + ");";
		}
		String ret = "ring " + ringVariable + "=";
		
		if (polysofractf) {
			ret += "(0" + addLeadingComma(fieldVars)
				+ "),(" + coalesce(ringVars, dummyVar);
		}
		else {
			ret += "0,(" + coalesce(vars, dummyVar);
		}
		
		ret += "),dp;" // ring definition in Singular, using revgradlex
			+ "ideal " + idealVariable + "="
		 	+ polys + ";"; // ideal definition in Singular

		ret += substCommand;

		ret += "groebner(" + idealVariable + ")!=1;"; // the Groebner basis calculation command
		return ret;
	}
	
	
	/**
	 * Creates a Singular program for the elimination ideal given by
	 * a set of generating polynomials. We get the result in factorized form.
	 * @param polys set of polynomials generating the ideal
	 * @param variables the variables of the polynomials
	 * @param dependentVariables the variables that should be eliminated
	 * @return the Singular program code
	 */
	
	/*
	 * Example program code:
	 * ring r=0,(v1,v2,v3,v4,v5,v6,v7,v8,v9,v10,v11,v12,v13,v14,v15),dp;
	 * ideal i=-1*v5+-1*v3+2*v1,-1*v6+-1*v4+2*v2,-1*v9+2*v7+-1*v5,-1*v10+2*v8+-1*v6,2*v11+-1*v7+-1*v1,2*v12+-1*v8+-1*v2,
	 * -1*v13*v12+v14*v11+v13*v6+-1*v11*v6+-1*v14*v5+v12*v5,
	 * -1*v13*v10+v14*v9+v13*v4+-1*v9*v4+-1*v14*v3+v10*v3,
	 * -1+2*v15*v14*v10+-1*v15*v10^2+2*v15*v13*v9+-1*v15*v9^2+-2*v15*v14*v4+v15*v4^2+-2*v15*v13*v3+v15*v3^2;
	 * ideal e=eliminate(i,v1*v2*v7*v8*v11*v12*v13*v14*v15);
	 * list o;int s=size(e);int j;for(j=1;j<=s;j=j+1){o[j]=factorize(e[j]);}o;
	 * 
	 * Example output from Singular:
	 * [1]:
          [1]:
             _[1]=1
             _[2]=v4*v5-v3*v6-v4*v9+v6*v9+v3*v10-v5*v10
          [2]:
             1,1
	 */
	
	public static String getSingularEliminationIdeal( 
			Polynomial[] polys, Set<Variable> variables, Set<Variable> dependentVariables) {

		String ringVariable = "r";
		String idealVariable = "i";
		String loopVariable = "j";
		String sizeVariable = "s";
		String eliminationVariable = "e";
		String outputVariable = "o";
		String dummyVar = "d";
		
		StringBuffer ret = new StringBuffer("ring ");
		ret.append(ringVariable);
		ret.append("=0,(");
		String vars = "";
		Iterator<Variable> variablesIterator = variables.iterator();
		while (variablesIterator.hasNext()) {
			vars += variablesIterator.next();
			if (variablesIterator.hasNext())
				vars += ",";
		}
		
		if (vars != "")
			ret.append(vars);
		else
			ret.append(dummyVar);
		
		ret.append("),dp;");
		
		ret.append("ideal ");
		ret.append(idealVariable);
		ret.append("=");
		ret.append(getPolysAsCommaSeparatedString(polys));
		ret.append(";");
		
		ret.append("ideal ");
		ret.append(eliminationVariable);
		ret.append("=");
		ret.append("eliminate(");
		ret.append(idealVariable);
		ret.append(",");

		vars = "";
		Iterator<Variable> dependentVariablesIterator = dependentVariables.iterator();
		while (dependentVariablesIterator.hasNext()){
			vars += dependentVariablesIterator.next();
			if (dependentVariablesIterator.hasNext()){
				vars += "*";
			}
		}
		if (vars != "")
			ret.append(vars);
		else
			ret.append(dummyVar);
		
		ret.append(");");

		// list o;int s=size(e);int j;for(j=1;j<=s;j=j+1){o[j]=factorize(e[j]);}o;
		ret.append("list " + outputVariable + ";int " + sizeVariable + "=size(" + eliminationVariable + ");");
		ret.append("int " + loopVariable + ";for(" + loopVariable + "=1;" + loopVariable + "<=" + sizeVariable
				+ ";" + loopVariable + "=" + loopVariable + "+1)");
		ret.append("{" + outputVariable + "[" + loopVariable + "]=factorize(" + eliminationVariable
				+ "[" + loopVariable + "]);}o;");
		
		return ret.toString();
	}

	/**
	 * Decides if an array of polynomials (as a set) gives a solvable equation system
	 * on the field of the complex numbers.
	 * @param polys the array of polynomials
	 * @param substitutions some variables which are to be evaluated with exact numbers
	 * @param kernel kernel for the prover
	 * @param polysofractf use polynomial over rational functions if possible 
	 * @return yes if solvable, no if no solutions, or null (if cannot decide)
	 */
	public static Boolean solvable(Polynomial[] polys, HashMap<Variable,Integer> substitutions, Kernel kernel,
			boolean polysofractf) {
		
		HashSet<Variable> substVars = null;
		String polysAsCommaSeparatedString = getPolysAsCommaSeparatedString(polys);
		substVars = new HashSet<Variable>(substitutions.keySet());		
		
		String freeVars = getVarsAsCommaSeparatedString(polys, substVars, true);
		String dependantVars = getVarsAsCommaSeparatedString(polys, substVars, false);
		 
		
		if (App.singularWS != null && App.singularWS.isAvailable()) {
			
			String singularSolvableProgram = createGroebnerSolvableScript("r", "i", "d", substitutions, polysAsCommaSeparatedString, 
					freeVars, dependantVars, polysofractf);
 
			// createGroebnerSolvableScript("r", "i", 
			// 		substitutions, varsAsCommaSeparatedString, polysAsCommaSeparatedString);
			
			if (singularSolvableProgram.length()>500)
				App.debug(singularSolvableProgram.length() + " bytes -> singular");
			else
				App.debug(singularSolvableProgram + " -> singular");
			String singularSolvable = App.singularWS.directCommand(singularSolvableProgram);
			if (singularSolvable.length()>500)
				App.debug("singular -> " + singularSolvable.length() + " bytes");
			else
				App.debug("singular -> " + singularSolvable);
			if ("0".equals(singularSolvable))
				return false; // no solution
			return true; // at least one solution exists
		}

		// If SingularWS is not applicable, then we try to use the internal CAS:
		GeoGebraCAS cas = (GeoGebraCAS) kernel.getGeoGebraCAS();
		
		String script = cas.getCurrentCAS().createGroebnerSolvableScript(substitutions, polysAsCommaSeparatedString,
				freeVars, dependantVars, polysofractf);
		if (script == null) {
			App.info("Not implemented (yet)");
			return null; // cannot decide
		}
		App.info("[solvable] input to cas: "+script);
		String result = cas.evaluate(script);
		App.info("[solvable] output from cas: "+result);	
		if ("0".equals(result))
			return false; // no solution
		if ("1".equals(result))
			return true; // at least one solution exists
		return null; // cannot decide 
	}
	
	/** Returns the square of the input polynomial
	 * @param p input polynomial
	 * @return the square (p*p)
	 */
	public static Polynomial sqr(Polynomial p) {
		return p.multiply(p);
	}
	
	/**
	 * Returns the square of the distance of two points
	 * @param a1 first coordinate of A
	 * @param a2 second coordinate of A
	 * @param b1 first coordinate of B
	 * @param b2 second coordinate of B
	 * @return the square of the distance
	 */
	public static Polynomial sqrDistance(Variable a1, Variable a2, Variable b1, Variable b2) {
		return sqr(new Polynomial(a1).subtract(new Polynomial(b1)))
				.add(sqr(new Polynomial(a2).subtract(new Polynomial(b2))));
	}
	
	/**
	 * Returns if AO=OB, i.e. whether the AOB triangle is isosceles
	 * @param a1 first coordinate of A
	 * @param a2 second coordinate of A
	 * @param o1 first coordinate of O
	 * @param o2 second coordinate of O
	 * @param b1 first coordinate of B
	 * @param b2 second coordinate of B
	 * @return the 0 polynomial if AO=OB
	 */
	public static Polynomial equidistant(Variable a1, Variable a2,
			Variable o1, Variable o2, Variable b1,
			Variable b2) {
		return sqrDistance(a1,a2,o1,o2).subtract(sqrDistance(o1,o2,b1,b2));  
	}
	
	/**
	 * Returns the elimination ideal for the given equation system,
	 * assuming given substitutions. Only the dependent variables
	 * will be eliminated. 
	 * @param eqSystem the equation system
	 * @param substitutions fixed values for certain variables
	 * @return elements of the elimination ideal
	 */
	public static Set<Set<Polynomial>> eliminate(Polynomial[] eqSystem,
			HashMap<Variable, Integer> substitutions) {
		if (App.singularWS != null && App.singularWS.isAvailable()) {
			HashSet<Variable> dependentVariables = new HashSet<Variable>();
			Set<Variable> variables = getVars(eqSystem);
			Iterator<Variable> variablesIterator = variables.iterator();
			while (variablesIterator.hasNext()) {
				Variable variable = variablesIterator.next();
				if (!variable.isFree()) {
					dependentVariables.add(variable);
				}
			}
			Polynomial[] eqSystemSubstituted;
			if (substitutions != null) {
				eqSystemSubstituted = new Polynomial[eqSystem.length];
				for (int i = 0; i < eqSystem.length; i++) {
					eqSystemSubstituted[i] = eqSystem[i]
							.substitute(substitutions);
				}
				variables.removeAll(substitutions.keySet());
			} else {
				eqSystemSubstituted = eqSystem;
			}
			String singularEliminationProgram = getSingularEliminationIdeal(
					eqSystemSubstituted, variables, dependentVariables);

			variablesIterator = variables.iterator();
			while (variablesIterator.hasNext()){
				Variable variable = variablesIterator.next();
				// App.debug(variable.getName()+" -> "+variable.getParent());
			}
			
			if (singularEliminationProgram.length() > 500)
				App.debug(singularEliminationProgram.length()
						+ " bytes -> singular");
			else
				App.debug(singularEliminationProgram + " -> singular");
			String singularSolvable = App.singularWS
					.directCommand(singularEliminationProgram);
			if (singularSolvable == null) {
				return null;
			}
			if (singularSolvable.length() > 500)
				App.debug("singular -> " + singularSolvable.length() + " bytes");
			else
				App.debug("singular -> " + singularSolvable);
						
			try {
				return PolynomialParser.parseFactoredPolynomialSet(singularSolvable, variables);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
		return null; // cannot decide
	}

}