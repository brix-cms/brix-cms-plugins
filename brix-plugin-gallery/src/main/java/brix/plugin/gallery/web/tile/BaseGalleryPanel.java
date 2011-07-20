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

import org.apache.wicket.model.IModel;

import org.brixcms.jcr.exception.JcrException;
import org.brixcms.jcr.wrapper.BrixNode;
import brix.plugin.gallery.GalleryPlugin;
import org.brixcms.web.generic.BrixGenericPanel;
import org.brixcms.web.nodepage.BrixPageParameters;
import org.brixcms.web.nodepage.PageParametersAware;

/**
 * @author wickeria at gmail.com
 */
public class BaseGalleryPanel extends BrixGenericPanel<BrixNode> implements PageParametersAware {

	private static final long serialVersionUID = 1L;
	private LinkedList<String> albumParams = new LinkedList<String>();

	public BaseGalleryPanel(String id, IModel<BrixNode> model) {
		super(id, model);
	}

	protected String createPathFromParams(LinkedList<String> params) {
		BrixNode node = getModelObject();
		String path = GalleryPlugin.get().getRootNodePath();
		if (node != null) {
			if (node.hasProperty(GalleryTileEditor.GALLERY_ROOT_FOLDER)) {
				try {
					BrixNode folderNode = (BrixNode) GalleryPlugin.getGallerySession().getNodeByIdentifier(
							node.getProperty(GalleryTileEditor.GALLERY_ROOT_FOLDER).getString());
					path = folderNode.getPath();
				} catch (JcrException e) {
					path = GalleryPlugin.get().getRootNodePath();
				}
			}
		}
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
