package geogebra.web.main;

import geogebra.common.GeoGebraConstants;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.util.debug.GeoGebraProfiler;
import geogebra.common.util.debug.Log;
import geogebra.html5.main.HasAppletProperties;
import geogebra.html5.util.ArticleElement;
import geogebra.web.euclidian.EuclidianSimplePanelW;
import geogebra.web.gui.applet.GeoGebraFrame;
import geogebra.web.gui.infobar.InfoBarW;
import geogebra.web.helper.ObjectPool;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class AppWsimple extends AppW {

	private GeoGebraFrame frame = null;

	/******************************************************
	 * Constructs AppW for applets with undo enabled
	 * 
	 * @param ae
	 * @param gf
	 */
	public AppWsimple(ArticleElement ae, GeoGebraFrame gf) {
		this(ae, gf, true);
	}

	/******************************************************
	 * Constructs AppW for applets
	 * 
	 * @param undoActive
	 *            if true you can undo by CTRL+Z and redo by CTRL+Y
	 */
	public AppWsimple(ArticleElement ae, GeoGebraFrame gf, final boolean undoActive) {
		this.articleElement = ae;
		this.frame = gf;
		this.objectPool = new ObjectPool();
		setAppletHeight(frame.getComputedHeight());
		setAppletWidth(frame.getComputedWidth());

		this.useFullGui = false;

		infobar = new InfoBarW(this);

		Log.info("GeoGebra " + GeoGebraConstants.VERSION_STRING + " "
		        + GeoGebraConstants.BUILD_DATE + " "
		        + Window.Navigator.getUserAgent());
		initCommonObjects();
		initing = true;

		// TODO: EuclidianSimplePanelW
		this.euclidianViewPanel = new EuclidianSimplePanelW(this, false);
		//(EuclidianDockPanelW)getGuiManager().getLayout().getDockManager().getPanel(App.VIEW_EUCLIDIAN);
		this.canvas = this.euclidianViewPanel.getCanvas();
		canvas.setWidth("1px");
		canvas.setHeight("1px");
		canvas.setCoordinateSpaceHeight(1);
		canvas.setCoordinateSpaceWidth(1);
		initCoreObjects(undoActive, this);
		//this may only be called after factories are initialized
		StringTemplate.latexIsMathQuill = true;
		removeDefaultContextMenu(this.getArticleElement());
	}

	public GeoGebraFrame getGeoGebraFrame() {
		return frame;
	}

	@Override
	protected void afterCoreObjectsInited() {
		// Code to run before buildApplicationPanel

		//initGuiManager();// TODO: comment it out

		GeoGebraFrame.finishAsyncLoading(articleElement, frame, this);
		initing = false;
	}

	public void buildApplicationPanel() {
		if (frame != null) {
			frame.clear();
			frame.add((Widget)getEuclidianViewpanel());
			getEuclidianViewpanel().setPixelSize(
					getSettings().getEuclidian(1).getPreferredSize().getWidth(),
					getSettings().getEuclidian(1).getPreferredSize().getHeight());
		}
	}

	@Override
    public void afterLoadFileAppOrNot() {

		buildApplicationPanel();

		getScriptManager().ggbOnInit();	// put this here from Application constructor because we have to delay scripts until the EuclidianView is shown

		kernel.initUndoInfo();

		getEuclidianView1().synCanvasSize();
		
		getEuclidianView1().doRepaint2();
		stopCollectingRepaints();
		frame.splash.canNowHide();
		requestFocusInWindow();
		setDefaultCursor();
		GeoGebraProfiler.getInstance().profileEnd();
    }

	@Override
	public void focusLost() {
		GeoGebraFrame.useDataParamBorder(
				getArticleElement(),
				getGeoGebraFrame());
	}

	@Override
	public void focusGained() {
		GeoGebraFrame.useFocusedBorder(
				getArticleElement(),
				getGeoGebraFrame());
	}

	@Override
    public void syncAppletPanelSize(int widthDiff, int heightDiff, int evno) {

		// not sure this is needed here

		/*if (widthDiff != 0 || heightDiff != 0)
			getEuclidianViewpanel().setPixelSize(
				getEuclidianViewpanel().getOffsetWidth() + widthDiff,
				getEuclidianViewpanel().getOffsetHeight() + heightDiff);
		*/
	}
	
	@Override
    public Element getFrameElement(){
		return frame.getElement();
	}
	
	@Override
    public HasAppletProperties getAppletFrame() {
		return frame;
	}
}
