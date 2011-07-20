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
package brix.plugin.gallery.photo;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import org.brixcms.jcr.wrapper.BrixNode;
import brix.plugin.gallery.GalleryPlugin;
import brix.plugin.gallery.photo.admin.UploadPhotosPanel;
import brix.plugin.hierarchical.HierarchicalPluginLocator;
import brix.plugin.hierarchical.admin.NodeEditorPlugin;
import org.brixcms.plugin.site.SimpleCallback;
import org.brixcms.registry.ExtensionPoint;

/**
 * @author wickeria at gmail.com
 */
public class PhotoNodePlugin implements NodeEditorPlugin {

	public static final String TYPE = GalleryPlugin.NS_PREFIX + "photo";

	private HierarchicalPluginLocator pluginLocator;

	public PhotoNodePlugin(HierarchicalPluginLocator pluginLocator) {
		this.pluginLocator = pluginLocator;
	}

	public static final ExtensionPoint<NodeEditorPlugin> POINT = new ExtensionPoint<NodeEditorPlugin>() {
		public brix.registry.ExtensionPoint.Multiplicity getMultiplicity() {
			return Multiplicity.COLLECTION;
		}

		public String getUuid() {
			return PhotoNodePlugin.class.getName();
		}
	};

	@Override
	public String getName() {
		return "photo";
	}

	@Override
	public String getNodeType() {
		return TYPE;
	}

	@Override
	public IModel<String> newCreateNodeCaptionModel(IModel<BrixNode> parentNode) {
		return new ResourceModel("uploadPhotos");
	}

	@Override
	public Panel newCreateNodePanel(String id, IModel<BrixNode> parentNode, SimpleCallback goBack) {
		return new UploadPhotosPanel(id, parentNode, goBack, pluginLocator);
	}

}
