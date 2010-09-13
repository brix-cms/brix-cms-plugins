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

import org.apache.wicket.model.IModel;

import brix.BrixNodeModel;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.gallery.GalleryPlugin;
import brix.plugin.gallery.album.AlbumFolderNodePlugin;
import brix.plugin.hierarchical.admin.HierarchicalNodeManagerPanel;
import brix.plugin.site.page.tile.admin.GenericTileEditorPanel;
import brix.web.picker.common.TreeAwareNode;
import brix.web.picker.node.NodePickerPanel;
import brix.web.picker.node.NodeTypeFilter;

public class GalleryTileEditor extends GenericTileEditorPanel<BrixNode> {

	private static final long serialVersionUID = 1L;
	public static final String GALLERY_ROOT_FOLDER = "galleryRootFolder";

	public GalleryTileEditor(String id, IModel<BrixNode> tileContainerNode) {
		super(id, tileContainerNode);
		NodePickerPanel picker = new NodePickerPanel("nodePicker", targetNodeModel, TreeAwareNode.Util.getTreeNode(GalleryPlugin.get()
				.getRootNode(null)), HierarchicalNodeManagerPanel.SHOW_ALL_NON_NULL_NODES_FILTER, new NodeTypeFilter(
				AlbumFolderNodePlugin.TYPE));
		picker.setRequired(true);
		add(picker);
	}

	private IModel<BrixNode> targetNodeModel = new BrixNodeModel();

	@Override
	public void load(BrixNode node) {
		if (node.hasProperty(GALLERY_ROOT_FOLDER)) {
			BrixNode pageNode = (BrixNode) GalleryPlugin.getGallerySession().getNodeByIdentifier(
					node.getProperty(GALLERY_ROOT_FOLDER).getString());
			targetNodeModel.setObject(pageNode);
		}
	}

	@Override
	public void save(BrixNode node) {
		node.setProperty(GALLERY_ROOT_FOLDER, targetNodeModel.getObject().getIdentifier());
	}

}
