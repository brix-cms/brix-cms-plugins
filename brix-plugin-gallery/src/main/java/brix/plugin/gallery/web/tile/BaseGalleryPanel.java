/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package brix.plugin.gallery.web.tile;

import java.util.LinkedList;

import org.apache.poi.hssf.record.formula.functions.T;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import brix.plugin.gallery.GalleryPlugin;
import brix.web.nodepage.BrixPageParameters;
import brix.web.nodepage.PageParametersAware;

/**
 * @author wickeria at gmail.com
 */
public class BaseGalleryPanel extends Panel implements PageParametersAware {

	private static final long serialVersionUID = 1L;
	private LinkedList<String> albumParams = new LinkedList<String>();

	public BaseGalleryPanel(String id) {
		super(id);
	}

	public BaseGalleryPanel(String id, IModel<T> model) {
		super(id, model);
	}

	protected String createPathFromParams(LinkedList<String> params) {
		String path = GalleryPlugin.get().getRootNodePath();
		for (String album : params) {
			path += "/" + album;
		}
		return path;
	}

	@Override
	public void initializeFromPageParameters(BrixPageParameters params) {
		albumParams.clear();
		for (int i = 0; i < params.getIndexedParamsCount(); i++) {
			albumParams.add(params.getIndexedParam(i).toString());
		}
	}

	@Override
	public void contributeToPageParameters(BrixPageParameters params) {
	}

	public LinkedList<String> getAlbumParams() {
		return albumParams;
	}
}
