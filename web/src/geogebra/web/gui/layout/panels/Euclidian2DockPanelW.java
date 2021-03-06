package geogebra.web.gui.layout.panels;

import geogebra.common.euclidian.EuclidianStyleBar;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.main.App;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class may be redundant since EuclidianDockPanelW,
 * but GeoGebra Desktop also uses two different classes for similar
 * purposes, so its behaviour was imitated here.
 *  
 * @author arpad
 */

public class Euclidian2DockPanelW extends EuclidianDockPanelWAbstract {

	EuclidianStyleBar espanel;
	EuclidianPanel euclidianpanel;

	Canvas eview1 = null;// static foreground
	
	public Euclidian2DockPanelW(boolean stylebar) {
		super(
				App.VIEW_EUCLIDIAN2,	// view id 
				"DrawingPad2", 				// view title
				null,						// toolbar string
				stylebar,					// style bar?
				6,							// menu order
				'2' // ctrl-shift-1
			);

		// copied here from loadComponent
		setEmbeddedSize(300);

		// this should execute when DockPanelW.register is not called
		if (!stylebar) {
			buildDockPanel();
		}
	}

	@Override
	protected Widget loadComponent() {
		if (euclidianpanel == null) {
			euclidianpanel = new EuclidianPanel(this);
			eview1 = Canvas.createIfSupported();
			eview1.getElement().getStyle().setPosition(Style.Position.RELATIVE);
			eview1.getElement().getStyle().setZIndex(0);
			euclidianpanel.add(eview1);
		}

		// Euclidian2DockPanelW.loadComponent will be called lazy,
		// so it is this place where EuclidianView 2 should be inited
		// in EuclidianDockPanelW, EuclidianView is created automatically
		if (app != null) {
			app.getEuclidianView2();
		}

		return euclidianpanel;
	}
	
	class EuclidianPanel extends AbsolutePanel implements RequiresResize {

		Euclidian2DockPanelW dockPanel;

		int oldHeight = 0;
		int oldWidth = 0;
		
		public EuclidianPanel(Euclidian2DockPanelW dockPanel) {
			this.dockPanel = dockPanel;
		}

		public void onResize() {
		
			if (app != null) {

				int h = dockPanel.getComponentInteriorHeight();
				int w = dockPanel.getComponentInteriorWidth();

				// TODO handle this better?
				// exit if new size cannot be determined
				if (h <= 0 || w <= 0) {
					return;
				}
				if (h != oldHeight || w != oldWidth) {
					app.ggwGraphicsView2DimChanged(w, h);
					oldHeight = h;
					oldWidth = w;
				}
			}
		}
	}

	public void reset() {
		if (euclidianpanel != null) {
			euclidianpanel.oldWidth = 0;
			euclidianpanel.oldHeight = 0;
		}
	}
	
	@Override
	protected Widget loadStyleBar() {

		if (espanel == null) {
			espanel = app.getEuclidianView2().getStyleBar();
		}

		return (Widget) espanel;
	}

	public Canvas getCanvas() {
	    return eview1;
    }

	public AbsolutePanel getAbsolutePanel() {
	    return euclidianpanel;
    }

	public void onResize() {
		super.onResize();
    }

	public void add(Widget w, int x, int y) {
	    euclidianpanel.add(w,x,y);
    }

	public void remove(Widget w) {
		euclidianpanel.remove(w);
    }

	public Euclidian2DockPanelW getEuclidianView2Wrapper() {
		return this;
	}

	public AbsolutePanel getEuclidianPanel() {
		return euclidianpanel;
	}

	public EuclidianView getEuclidianView() {
		if (app != null && app.hasEuclidianView2())
			return app.getEuclidianView2();
		return null;
	}

	@Override
    public void showView(boolean b) {
	    // TODO Auto-generated method stub	    
    }

	public void updateNavigationBar() {}
}
