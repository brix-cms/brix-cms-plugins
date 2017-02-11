package org.brixcms.plugin.content.blog.post;

import org.apache.wicket.extensions.markup.html.tree.BaseTree;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.admin.nodetree.AbstractNodeTreeRenderer;
import org.brixcms.plugin.site.admin.nodetree.PageNodeTreeRenderer;

/**
 * @author dan.simko@gmail.com
 */
public class PostNodeTreeRenderer extends AbstractNodeTreeRenderer {

    private static final long serialVersionUID = 1L;
    private static final ResourceReference RESOURCE = new PackageResourceReference(PageNodeTreeRenderer.class,
            "resources/x-office-document.png");

    @Override
    protected ResourceReference getImageResourceReference(BaseTree tree, Object node) {
        return RESOURCE;
    }

    @Override
    protected Class<? extends BrixNode> getNodeClass() {
        return PostNode.class;
    }

}
