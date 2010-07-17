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

package brix.plugin.article.web.tile.article;

import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.plugin.article.ArticlePlugin;
import brix.plugin.file.admin.folder.FolderNodePlugin;
import brix.plugin.hierarchical.admin.HierarchicalNodeManagerPanel;
import brix.web.picker.common.TreeAwareNode;
import brix.web.picker.node.NodePickerPanel;
import brix.web.picker.node.NodeTypeFilter;
import brix.web.tree.JcrTreeNode;
import brix.web.tree.NodeFilter;

/**
 * @author wickeria at gmail.com
 */
public class ArticleNodePickerPanel extends NodePickerPanel {

	private static final long serialVersionUID = 1L;

	public ArticleNodePickerPanel(String id, IModel<BrixNode> model, String workspaceId) {
		super(id, model, TreeAwareNode.Util.getTreeNode(ArticlePlugin.get().getRootNode(workspaceId)),
				HierarchicalNodeManagerPanel.SHOW_ALL_NON_NULL_NODES_FILTER, new NodeTypeFilter(FolderNodePlugin.TYPE));
	}

	public ArticleNodePickerPanel(String id, IModel<BrixNode> model, JcrTreeNode rootNode, NodeFilter visibilityFilter,
			NodeFilter enabledFilter) {
		super(id, model, rootNode, visibilityFilter, enabledFilter);
	}

	@Override
	protected IModel<String> newLabelModel() {
		return super.newLabelModel();
	}

}
