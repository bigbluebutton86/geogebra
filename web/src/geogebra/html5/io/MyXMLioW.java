package geogebra.html5.io;

import geogebra.common.io.DocHandler;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;

public class MyXMLioW extends geogebra.common.io.MyXMLio {

	private DocHandler handler, ggbDocHandler;
	private XmlParser xmlParser;

	public MyXMLioW(Kernel kernel, Construction cons) {
		this.kernel = kernel;
		this.cons = cons;	
		app = kernel.getApplication();

		xmlParser = new GwtXmlParser();
		handler = getGGBHandler();
	}

	private DocHandler getGGBHandler() {
		if (ggbDocHandler == null)	
			ggbDocHandler = kernel.newMyXMLHandler(cons);
		return ggbDocHandler;
	}

	@Override
    public void processXMLString(String str, boolean clearAll, boolean isGGTfile, boolean settingsBatch) throws Exception {
		doParseXML(str, clearAll, isGGTfile, clearAll, settingsBatch);
	}

	private void doParseXML(String xml, boolean clearConstruction,
			boolean isGGTFile, boolean mayZoom,boolean settingsBatch) throws Exception {
		boolean oldVal = kernel.isNotifyViewsActive();
		boolean oldVal2 = kernel.isUsingInternalCommandNames();
		kernel.setUseInternalCommandNames(true);
		
		if (!isGGTFile && mayZoom) {
			kernel.setNotifyViewsActive(false);
		}

		if (clearConstruction) {
			// clear construction
			kernel.clearConstruction(false);
		}

		try {
			kernel.setLoadingMode(true);
			if(settingsBatch && !isGGTFile){
				app.getSettings().beginBatch();
				App.debug("parsing start"+System.currentTimeMillis());
				xmlParser.parse(handler, xml);
				App.debug("parsing end"+System.currentTimeMillis());
				app.getSettings().endBatch();
			}
			else
				xmlParser.parse(handler, xml);
			//xmlParser.reset();
			kernel.setLoadingMode(false);
		} catch (Error e) {
			// e.printStackTrace();
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			kernel.setUseInternalCommandNames(oldVal2);
			if (!isGGTFile && mayZoom) {
				App.debug("cons up"+System.currentTimeMillis());
				kernel.updateConstruction();
				App.debug("cons upped"+System.currentTimeMillis());
				kernel.setNotifyViewsActive(oldVal);				
			}
			if (cons.hasSpreadsheetTracingGeos() && !isGGTFile) {
				// needs to be done after call to updateConstruction() to avoid spurious traces
				app.getTraceManager().loadTraceGeoCollection();
			}
			App.debug("traces"+System.currentTimeMillis());

		}
		
		// handle construction step stored in XMLhandler
		// do this only if the construction protocol navigation is showing	
		
		if (!isGGTFile && oldVal &&
				app.showConsProtNavigation()) 
		{
			App.debug("navigation"+System.currentTimeMillis());
				//((GuiManagerD)app.getGuiManager()).setConstructionStep(handler.getConsStep());

			if (app.getGuiManager() != null){
				// if there is a ConstructionProtocolView, then update its navigation bars
				app.getGuiManager().getConstructionProtocolView().setConstructionStep(handler.getConsStep());
			}
			else{
				// otherwise this is not needed 
				app.getKernel().getConstruction().setStep(handler.getConsStep());
			}

		}
		App.debug("navigation done"+System.currentTimeMillis());
	}

}
