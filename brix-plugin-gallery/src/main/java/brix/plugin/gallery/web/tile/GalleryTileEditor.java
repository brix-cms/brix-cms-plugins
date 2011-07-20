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

import org.brixcms.BrixNodeModel;
import org.brixcms.jcr.exception.JcrException;
import org.brixcms.jcr.wrapper.BrixNode;
import brix.plugin.gallery.GalleryPlugin;
import brix.plugin.hierarchical.admin.HierarchicalNodeManagerPanel;
import org.brixcms.plugin.site.page.tile.admin.GenericTileEditorPanel;
import org.brixcms.web.picker.common.TreeAwareNode;
import org.brixcms.web.picker.node.NodePickerPanel;

public class GalleryTileEditor extends GenericTileEditorPanel<BrixNode> {

    private static final long serialVersionUID = 1L;
    public static final String GALLERY_ROOT_FOLDER = "galleryRootFolder";

    public GalleryTileEditor(String id, IModel<BrixNode> tileContainerNode) {
	super(id, tileContainerNode);
	NodePickerPanel picker = new NodePickerPanel("nodePicker", targetNodeModel, TreeAwareNode.Util
		.getTreeNode(GalleryPlugin.get().getRootNode(null)),
		HierarchicalNodeManagerPanel.SHOW_ALL_NON_NULL_NODES_FILTER,
		HierarchicalNodeManagerPanel.SHOW_ALL_NON_NULL_NODES_FILTER);
	picker.setRequired(true);
	add(picker);
    }

    private IModel<BrixNode> targetNodeModel = new BrixNodeModel();

    @Override
    public void load(BrixNode node) {
	if (node.hasProperty(GALLERY_ROOT_FOLDER)) {
	    try {
		BrixNode pageNode = (BrixNode) GalleryPlugin.getGallerySession().getNodeByIdentifier(
			node.getProperty(GALLERY_ROOT_FOLDER).getString());
		targetNodeModel.setObject(pageNode);
	    } catch (JcrException e) {
	    }
	}
    }

    @Override
    public void save(BrixNode node) {
	node.setProperty(GALLERY_ROOT_FOLDER, targetNodeModel.getObject().getIdentifier());
    }

}
