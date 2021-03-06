/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoBarChart;
import geogebra.common.kernel.algos.DrawInformationAlgo;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.util.Cloner;

/**
 * @author G. Sturr
 * @version 2011-06-21
 */

public class AlgoPascalBarChart extends AlgoBarChart {

	

	public AlgoPascalBarChart(Construction cons, String label, 
			NumberValue n, NumberValue p) {
        super(cons,label, n, p, null, null, AlgoBarChart.TYPE_BARCHART_PASCAL);
        cons.registerEuclidianViewCE(this);
    }
	
	
	public AlgoPascalBarChart(Construction cons, String label, 
			NumberValue n, NumberValue p, GeoBoolean isCumulative) {
        super(cons,label, n, p, null, isCumulative, AlgoBarChart.TYPE_BARCHART_PASCAL);
        cons.registerEuclidianViewCE(this);
    }
	
	private AlgoPascalBarChart( 
			NumberValue n, NumberValue p, GeoBoolean isCumulative,NumberValue a,NumberValue b,double[]vals,
			double[]borders,int N) {
        super(n, p, null, isCumulative, AlgoBarChart.TYPE_BARCHART_PASCAL,a,b,vals,borders,N);
    }
	

	@Override
	public Commands getClassName() {
		return Commands.Pascal;
	}

   
	@Override
	public DrawInformationAlgo copy() {
		GeoBoolean b = (GeoBoolean)this.getIsCumulative();
		if(b!=null)b=(GeoBoolean)b.copy();
		return new AlgoPascalBarChart(
				(NumberValue)this.getP1().deepCopy(kernel),(NumberValue)this.getP2().deepCopy(kernel),				
				b,(NumberValue)this.getA().deepCopy(kernel),(NumberValue)this.getB().deepCopy(kernel),
				Cloner.clone(getValues()),Cloner.clone(getLeftBorder()),getIntervals());

	}
	
	
}

