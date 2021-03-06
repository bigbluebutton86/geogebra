package geogebra.touch.gui;

import geogebra.common.kernel.Kernel;
import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.controller.TouchController;
import geogebra.touch.gui.algebra.AlgebraViewPanel;
import geogebra.touch.gui.algebra.events.FastClickHandler;
import geogebra.touch.gui.elements.FastButton;
import geogebra.touch.gui.elements.StandardButton;
import geogebra.touch.gui.elements.header.TabletHeaderPanel;
import geogebra.touch.gui.elements.stylebar.StyleBar;
import geogebra.touch.gui.elements.toolbar.ToolBar;
import geogebra.touch.gui.euclidian.EuclidianViewPanel;
import geogebra.touch.gui.laf.DefaultResources;
import geogebra.touch.model.TouchModel;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HeaderPanel;

/**
 * Coordinates the GUI of the tablet.
 * 
 */
public class TabletGUI extends HeaderPanel implements GeoGebraTouchGUI {
	private static final float ALGEBRA_VIEW_WIDTH_FRACTION = 0.2f;

	public static final int ALGEBRA_BUTTON_WIDTH = 50;
	private static final int MINIMAL_WIDTH_FOR_TWO_VIEWS = 400;

	public static final int STYLEBAR_WIDTH = 140;

	public static int computeAlgebraWidth() {
		if (Window.getClientWidth() < MINIMAL_WIDTH_FOR_TWO_VIEWS) {
			return Window.getClientWidth();
		}
		return Math.max(250,
				(int) (Window.getClientWidth() * ALGEBRA_VIEW_WIDTH_FRACTION));
	}

	private final List<ResizeListener> resizeListeners = new ArrayList<ResizeListener>();
	private TouchModel touchModel;
	private DockLayoutPanel contentPanel;
	private ToolBar toolBar;
	private final EuclidianViewPanel euclidianViewPanel;
	private AlgebraViewPanel algebraViewPanel;
	private StyleBar styleBar;
	private FlowPanel algebraViewButtonPanel, algebraViewArrowPanel;
	private TouchApp app;
	private boolean editing = true;
	private FastButton algebraButton;
	private TouchController touchController;

	/**
	 * Sets the viewport and other settings, creates a link element at the end
	 * of the head, appends the css file and initializes the GUI elements.
	 */
	public TabletGUI() {
		// required to start the kernel
		this.euclidianViewPanel = new EuclidianViewPanel(this);
	}

	public void addResizeListener(final ResizeListener rl) {
		this.resizeListeners.add(rl);
	}

	@Override
	public void allowEditing(final boolean b) {
		if (this.editing == b) {
			return;
		}
		this.editing = b;
		this.resetMode();
		this.toolBar.setVisible(b);
		this.algebraViewButtonPanel.setVisible(b);
		this.setAlgebraVisible(this.isAlgebraShowing());
		this.styleBar.setVisible(b);

		if (b) {
			this.touchModel.getGuiModel().setStyleBar(this.styleBar);
		} else {
			this.touchModel.getGuiModel().setStyleBar(null);
		}
	}

	public void editTitle() {
		if (this.getHeaderWidget() instanceof TabletHeaderPanel) {
			((TabletHeaderPanel) this.getHeaderWidget()).editTitle();
		}
	}

	@Override
	public AlgebraViewPanel getAlgebraViewPanel() {
		return this.algebraViewPanel;
	}

	public String getConstructionTitle() {
		if (this.getHeaderWidget() instanceof TabletHeaderPanel) {
			return ((TabletHeaderPanel) this.getHeaderWidget())
					.getConstructionTitle();
		}
		return "";
	}

	public DockLayoutPanel getContentPanel() {
		return this.contentPanel;
	}

	@Override
	public EuclidianViewPanel getEuclidianViewPanel() {
		return this.euclidianViewPanel;
	}

	public TouchModel getTouchModel() {
		return this.touchModel;
	}

	public ToolBar getToolBar() {
		return this.toolBar;
	}

	private boolean rtl;
	/**
	 * Creates a new instance of {@link TouchController} and
	 * {@link MobileAlgebraController} and initializes the
	 * {@link EuclidianViewPanel euclidianViewPanel} and
	 * {@link AlgebraViewPanel algebraViewPanel} according to these instances.
	 * 
	 * @param kernel
	 *            Kernel
	 */
	@Override
	public void initComponents(final Kernel kernel, boolean isRtl) {
		this.rtl = isRtl;
		if(this.rtl){
			StyleInjector.injectStylesheet(DefaultResources.INSTANCE.rtlStyle().getText());
			StyleInjector.injectStylesheet(DefaultResources.INSTANCE.additionalRtlStyle().getText());
			TouchEntryPoint.getLookAndFeel().loadRTLStyles();
		}
		//url parameter for debugging, cannot be used in app
		if("true".equals(Location.getParameter("ios7")) ||
				Window.Navigator.getUserAgent().contains("CPU OS 7")){
			StyleInjector.injectStylesheet(DefaultResources.INSTANCE.ios7Style().getText());
		}
		this.touchModel = new TouchModel(kernel);
		this.app = (TouchApp) kernel.getApplication();
		// Initialize GUI Elements
		TouchEntryPoint.getLookAndFeel().buildTabletHeader(this.touchModel);

		this.contentPanel = new DockLayoutPanel(Unit.PX);

		this.touchController = new TouchController(this.touchModel,
				this.app);
		this.touchController.setKernel(kernel);

		// init toolBar before setting the size of algebraView and euclidianView
		this.toolBar = new ToolBar(this.touchModel, this.app);
		this.setFooterWidget(this.toolBar);

		this.algebraViewPanel = new AlgebraViewPanel(this.touchController, kernel);

		final int width = Window.getClientWidth() - computeAlgebraWidth();
		final int height = TouchEntryPoint.getLookAndFeel()
				.getContentWidgetHeight();
		this.euclidianViewPanel.setPixelSize(width, height);
		this.euclidianViewPanel.initEuclidianView(this.touchController, super.getHeaderWidget(),
				width, height);

		this.styleBar = new StyleBar(this.touchModel,
				this.euclidianViewPanel.getEuclidianView(),this);
		this.touchModel.getGuiModel().setStyleBar(this.styleBar);
		this.euclidianViewPanel.add(this.styleBar);
		this.euclidianViewPanel.setWidgetPosition(this.styleBar, this.rtl ? width - STYLEBAR_WIDTH :0, 0);
		if(this.rtl){
			this.contentPanel.addWest(this.algebraViewPanel, computeAlgebraWidth());
		}else{
			this.contentPanel.addEast(this.algebraViewPanel, computeAlgebraWidth());
		}
		this.contentPanel.add(this.euclidianViewPanel);
		this.contentPanel.setHeight("100%");
		
		this.algebraViewPanel.onResize();
		this.setContentWidget(this.contentPanel);		

		// show/hide AlgebraView Button
		this.algebraViewButtonPanel = new FlowPanel();

		// Prevent events from getting through to the canvas
		this.algebraViewButtonPanel.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				event.preventDefault();
				event.stopPropagation();
			}
		}, ClickEvent.getType());

		this.algebraViewButtonPanel.addDomHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(final MouseDownEvent event) {
				event.preventDefault();
				event.stopPropagation();
			}

		}, MouseDownEvent.getType());

		this.algebraViewButtonPanel.addDomHandler(new TouchStartHandler() {

			@Override
			public void onTouchStart(final TouchStartEvent event) {
				event.preventDefault();
				event.stopPropagation();
			}

		}, TouchStartEvent.getType());

		this.algebraViewArrowPanel = new FlowPanel();
		this.algebraViewArrowPanel.setStyleName("algebraViewArrowPanel");

		this.algebraButton = new StandardButton(TouchEntryPoint
				.getLookAndFeel().getIcons().triangle_left());
		this.algebraButton.setStyleName("arrowRight");

		if (TouchEntryPoint.getLookAndFeel().useClickHandlerForOpenClose()) {
			this.algebraButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					TabletGUI.this.toggleAlgebraView();
				}
			});
		} else {
			this.algebraButton.addFastClickHandler(new FastClickHandler() {
				@Override
				public void onClick() {
					TabletGUI.this.toggleAlgebraView();
				}
			});
		}

		this.algebraViewArrowPanel.add(this.algebraButton);

		this.euclidianViewPanel.add(this.algebraViewButtonPanel);
		this.euclidianViewPanel.setWidgetPosition(this.algebraViewButtonPanel,
				this.rtl? 0 : width - TabletGUI.ALGEBRA_BUTTON_WIDTH, 0);

		this.algebraViewButtonPanel.setStyleName("algebraViewButtonPanel");
		this.algebraViewButtonPanel.add(this.algebraViewArrowPanel);

		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(final ResizeEvent event) {
				TabletGUI.this.updateViewSizes();
			}
		});
	}

	@Override
	public boolean isAlgebraShowing() {
		return this.algebraViewPanel.isVisible();
	}

	@Override
	public void updateViewSizes() {
		this.touchModel.getGuiModel().closeOptions();
		super.onResize();
		for (final ResizeListener res : this.resizeListeners) {
			res.onResize();
		}
		positionAlgebraViewButtonPanel();
	}

	private void positionAlgebraViewButtonPanel() {
		if (!isAlgebraShowing()) {
			this.algebraButton.setStyleName("arrowLeft");
			this.contentPanel.setWidgetSize(this.algebraViewPanel, 0);
			this.algebraViewButtonPanel.addStyleName("transparent");
		} else {
			this.algebraButton.setStyleName("arrowRight");
			this.algebraViewButtonPanel.removeStyleName("transparent");
			this.contentPanel.setWidgetSize(this.algebraViewPanel,
					TabletGUI.computeAlgebraWidth());
		}
	}

	public void restoreEuclidian(final DockLayoutPanel panel) {
		this.contentPanel = panel;
		this.setContentWidget(this.contentPanel);
	}

	@Override
	public void resetController() {
		this.touchController.reset();
	}
	
	public void toggleAlgebraView() {
		this.setAlgebraVisible(!this.algebraViewPanel.isVisible());
		this.app.setUnsaved();
	}

	public FlowPanel getAlgebraViewButtonPanel() {
		return this.algebraViewButtonPanel;
	}

	@Override
	public void resetMode() {
		this.touchModel.getGuiModel().setActive(
				this.touchModel.getGuiModel().getDefaultButton());
	}

	@Override
	public void setAlgebraVisible(final boolean visible) {
		this.algebraViewPanel.setVisible(visible);
		this.updateViewSizes();
	}

	@Override
	public void setLabels() {
		if (this.algebraViewPanel != null) {
			this.algebraViewPanel.setLabels();
		}
		if (TouchEntryPoint.getLookAndFeel().getTabletHeaderPanel() != null) {
			TouchEntryPoint.getLookAndFeel().getTabletHeaderPanel().setLabels();
		}
		this.toolBar.setLabels();
	}

	public boolean isRTL() {
		return this.rtl;
	}

	public FlowPanel getStylebar() {
		return this.styleBar;
	}

}
