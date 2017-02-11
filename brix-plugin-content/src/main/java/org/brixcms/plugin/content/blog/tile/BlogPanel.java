package org.brixcms.plugin.content.blog.tile;

import java.util.List;

import org.apache.wicket.model.IModel;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.content.blog.post.PostNode;
import org.brixcms.plugin.content.blog.tile.post.PostContainer;
import org.brixcms.plugin.content.blog.tile.post.PostPanel;
import org.brixcms.plugin.content.blog.tile.post.PostsPanel;
import org.brixcms.plugin.content.breadcrumb.BreadcrumbContributor;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class BlogPanel extends BaseBlogPanel implements BreadcrumbContributor {

    private static String CONTENT_ID = "content";

    public BlogPanel(String id, IModel<BrixNode> tileNodeModel) {
        super(id, tileNodeModel);
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        removeAll();
        if (activePost.getObject() == null) {
            add(new PostsPanel(CONTENT_ID, getModel()));
        } else {
            add(new PostPanel(CONTENT_ID, activePost, true));
        }
    }

    @Override
    protected BaseBlogContainer newBlogContainer() {
        return new PostContainer();
    }

    @Override
    public void contributeToBreadcrumb(List<BreadcrumbItem> items) {
        PostNode postNode = activePost.getObject();
        if (postNode != null) {
            items.add(new BreadcrumbItem(postNode.getTitle(), postNode.getName()));
        }
    }
}
