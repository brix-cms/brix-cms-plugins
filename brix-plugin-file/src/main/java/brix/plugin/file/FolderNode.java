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

package brix.plugin.file;

import javax.jcr.Node;

import org.brixcms.Brix;
import org.brixcms.jcr.JcrNodeWrapperFactory;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.wrapper.BrixNode;
import brix.plugin.file.admin.folder.FolderNodePlugin;
import brix.plugin.hierarchical.HierarchicalNodePlugin;
import brix.plugin.hierarchical.HierarchicalPluginLocator;
import brix.plugin.hierarchical.nodes.SimpleFolderNode;
import org.brixcms.web.picker.common.TreeAwareNode;
import org.brixcms.web.tree.AbstractJcrTreeNode;
import org.brixcms.web.tree.JcrTreeNode;

/**
 * @author wickeria at gmail.com
 */
public class FolderNode extends SimpleFolderNode implements TreeAwareNode {

	public FolderNode(Node delegate, JcrSession session) {
		super(delegate, session);
	}

	@Override
	public JcrTreeNode getTreeNode(BrixNode node) {
		return new FolderTreeNode(node);
	}

	private static class FolderTreeNode extends AbstractJcrTreeNode {
		private static final long serialVersionUID = 1L;

		public FolderTreeNode(BrixNode node) {
			super(node);
		}
	};

	public static final JcrNodeWrapperFactory FACTORY(final HierarchicalPluginLocator locator) {
		return new JcrNodeWrapperFactory() {

			@Override
			public boolean canWrap(Brix brix, JcrNode node) {

				if (!node.isNodeType("nt:folder")) {
					return false;
				}

				HierarchicalNodePlugin plugin = locator.getPlugin();
				return node.getPath().startsWith(plugin.getRootNodePath());
			}

			@Override
			public JcrNode wrap(Brix brix, Node node, JcrSession session) {
				return new FolderNode(node, session);
			}

			// @Override
			// public void initializeRepository(Brix brix, Session session) {
			// RepositoryUtil.registerNodeType(session.getWorkspace(),
			// ArticleFolderNodePlugin.TYPE, true, false, true);
			// }

		};
	}

	public static FolderNode initialize(JcrNode node) {
		BrixNode brixNode = (BrixNode) node;
		brixNode.setNodeType(FolderNodePlugin.TYPE);
		return new FolderNode(node.getDelegate(), node.getSession());
	}

	@Override
	public String getUserVisibleType() {
		return "Folder";
	}
}
