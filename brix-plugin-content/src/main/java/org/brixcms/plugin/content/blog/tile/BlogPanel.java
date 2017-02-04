package org.brixcms.plugin.content.blog.tile;

import org.apache.wicket.model.IModel;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.content.blog.tile.post.PostContainer;
import org.brixcms.plugin.content.blog.tile.post.PostPanel;
import org.brixcms.plugin.content.blog.tile.post.PostsPanel;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class BlogPanel extends BaseBlogPanel {

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
}
