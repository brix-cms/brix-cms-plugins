package org.brixcms.plugin.content;

import javax.jcr.Node;

import org.brixcms.Brix;
import org.brixcms.jcr.JcrNodeWrapperFactory;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.plugin.content.folder.FolderNode;

/**
 * Node that can wrap the brix:root/brix:content node
 * 
 * @author dan.simko@gmail.com
 *
 */
public class ContentRootNode extends FolderNode {

    public static final JcrNodeWrapperFactory FACTORY = new JcrNodeWrapperFactory() {
        @Override
        public boolean canWrap(Brix brix, JcrNode node) {
            return node.getPath().equals(ContentPlugin.get(brix).getRootNodePath());
        }

        @Override
        public JcrNode wrap(Brix brix, Node node, JcrSession session) {
            return new ContentRootNode(node, session);
        }
    };

    public ContentRootNode(Node delegate, JcrSession session) {
        super(delegate, session);
    }

    @Override
    public String getUserVisibleName() {
        return "Content";
    }
}
