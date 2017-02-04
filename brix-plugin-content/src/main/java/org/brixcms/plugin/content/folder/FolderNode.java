package org.brixcms.plugin.content.folder;

import javax.jcr.Node;

import org.brixcms.Brix;
import org.brixcms.jcr.JcrNodeWrapperFactory;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.content.ContentPlugin;
import org.brixcms.plugin.hierarchical.nodes.SimpleFolderNode;
import org.brixcms.web.picker.common.TreeAwareNode;
import org.brixcms.web.tree.AbstractJcrTreeNode;
import org.brixcms.web.tree.JcrTreeNode;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class FolderNode extends SimpleFolderNode implements TreeAwareNode {

	public FolderNode(Node delegate, JcrSession session) {
		super(delegate, session);
	}

	@Override
	public JcrTreeNode getTreeNode(BrixNode node) {
		return new FolderTreeNode(node);
	}

    private static class FolderTreeNode extends AbstractJcrTreeNode {

		public FolderTreeNode(BrixNode node) {
			super(node);
		}
	};

	public static final JcrNodeWrapperFactory FACTORY = new JcrNodeWrapperFactory() {

		@Override
		public boolean canWrap(Brix brix, JcrNode node) {

			if (!node.isNodeType("nt:folder")) {
				return false;
			}
            ContentPlugin contentPlugin = ContentPlugin.get(brix);
            if (contentPlugin == null) {
                return false;
            }

			return node.getPath().startsWith(contentPlugin.getRootNodePath());
		}

		@Override
		public JcrNode wrap(Brix brix, Node node, JcrSession session) {
			return new FolderNode(node, session);
		}

	};

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
