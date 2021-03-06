package geogebra.touch;

import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.ggtapi.models.Material.MaterialType;
import geogebra.common.move.ggtapi.models.MaterialFilter;
import geogebra.html5.euclidian.EuclidianViewWeb;
import geogebra.html5.main.AppWeb;
import geogebra.html5.main.StringHandler;
import geogebra.html5.util.View;
import geogebra.html5.util.ggtapi.JSONparserGGT;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.ui.RootPanel;

public class FileManagerT {
	private static final String FILE_PREFIX = "file#";
	private static final String THUMB_PREFIX = "img#";
	private static final String META_PREFIX = "meta#";
	Storage stockStore = Storage.getLocalStorageIfSupported();

	public FileManagerT() {
		if (this.stockStore != null) {
			this.ensureKeyPrefixes();
		}
	}

	public void delete(final String text) {
		this.stockStore.removeItem(FILE_PREFIX + text);
		this.stockStore.removeItem(THUMB_PREFIX + text);
		TouchEntryPoint.reloadLocalFiles(text);
	}

	private void ensureKeyPrefixes() {
		if (this.stockStore.getLength() > 0) {
			for (int i = 0; i < this.stockStore.getLength(); i++) {
				final String oldKey = this.stockStore.key(i);
				if (!oldKey.contains("#")) {
					this.stockStore.removeItem(oldKey);
				}
			}
		}
	}

	public List<Material> getAllFiles() {
		return this.getFiles(MaterialFilter.getUniversalFilter());
	}

	String getDefaultConstructionTitle(final Localization loc) {
		int i = 1;
		String filename;
		do {
			filename = loc.getPlain("UntitledA", i + "");
			i++;
		} while (this.hasFile(filename));
		return filename;
	}

	private boolean getFile(final String title, final App app) {
		boolean success = true;
		try {
			final String base64 = this.stockStore.getItem(FILE_PREFIX + title);
			if (base64 == null) {
				return false;
			}
			app.getGgbApi().setBase64(base64);
		} catch (final Throwable t) {
			success = false;
			app.showError("LoadFileFailed");
			t.printStackTrace();
		}
		return success;
	}

	private List<Material> getFiles(final MaterialFilter filter) {
		final List<Material> ret = new ArrayList<Material>();
		if (this.stockStore == null || this.stockStore.getLength() <= 0) {
			return ret;
		}

		for (int i = 0; i < this.stockStore.getLength(); i++) {
			final String key = this.stockStore.key(i);
			if (key.startsWith(FILE_PREFIX)) {
				final String keyStem = key.substring(FILE_PREFIX.length());
				Material mat = JSONparserGGT.parseMaterial(this.stockStore
						.getItem(META_PREFIX + keyStem));
				if (mat == null) {
					mat = new Material(0, MaterialType.ggb);
					mat.setTitle(keyStem);
				}
				if (filter.check(mat)) {
					mat.setURL(keyStem);
					ret.add(mat);
				}
			}
		}

		return ret;
	}

	public void getMaterial(final Material material, final AppWeb app) {
		if (material.getId() > 0) {
			// remote material
			new View(RootPanel.getBodyElement(), app)
					.processFileName("http://www.geogebratube.org/files/material-"
							+ material.getId() + ".ggb");
			app.setUnsaved();
		} else {
			((TouchApp) app).setConstructionTitle(material.getTitle());
			this.getFile(material.getURL(), app);
		}

	}

	public String getThumbnailDataUrl(final String title) {
		return this.stockStore.getItem(THUMB_PREFIX + title);
	}

	public boolean hasFile(final String filename) {
		return this.stockStore != null
				&& this.stockStore.getItem(FILE_PREFIX + filename) != null;
	}

	public void saveFile(final App app) {
		final String consTitle = app.getKernel().getConstruction().getTitle();
		final StringHandler base64saver = new StringHandler() {
			@Override
			public void handle(final String s) {
				FileManagerT.this.stockStore
						.setItem(FILE_PREFIX + consTitle, s);
				TouchEntryPoint.reloadLocalFiles(consTitle);
			}
		};

		((geogebra.html5.main.GgbAPI) app.getGgbApi()).getBase64(base64saver);

		// extract metadata
		final Material mat = new Material(0, MaterialType.ggb);
		mat.setTimestamp(System.currentTimeMillis() / 1000);
		mat.setTitle(consTitle);
		mat.setDescription(app.getKernel().getConstruction()
				.getWorksheetText(0));

		this.stockStore.setItem(META_PREFIX + consTitle, mat.toJson()
				.toString());
		this.stockStore.setItem(THUMB_PREFIX + consTitle,
				((EuclidianViewWeb) app.getEuclidianView1())
						.getCanvasBase64WithTypeString());
		app.setSaved();
		((TouchApp) app).approveFileName();
	}

	public List<Material> search(final String query) {
		return this.getFiles(MaterialFilter.getSearchFilter(query));
	}
}
