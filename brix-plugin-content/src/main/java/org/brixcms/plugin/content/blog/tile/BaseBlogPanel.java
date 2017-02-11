package org.brixcms.plugin.content.blog.tile;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;
import org.brixcms.BrixNodeModel;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.content.blog.post.PostNode;
import org.brixcms.plugin.content.blog.tile.post.PostContainer;
import org.brixcms.web.generic.BrixGenericPanel;
import org.brixcms.web.nodepage.BrixPageParameters;
import org.brixcms.web.nodepage.PageParametersAware;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public abstract class BaseBlogPanel extends BrixGenericPanel<BrixNode> implements PageParametersAware {

    protected IModel<PostNode> activePost = new BrixNodeModel<PostNode>();

    public BaseBlogPanel(String id, IModel<BrixNode> tileNodeModel) {
        super(id, tileNodeModel);
    }

    @Override
    public void contributeToPageParameters(BrixPageParameters params) {
    }

    @Override
    public void initializeFromPageParameters(BrixPageParameters params) {
        BaseBlogContainer postContainer = newBlogContainer();
        postContainer.load(getModelObject());
        String selectedPostName = params.get(0).toString();
        BrixNode folder = postContainer.getContentFolder();
        if (!Strings.isEmpty(selectedPostName) && folder != null && folder.hasNode(selectedPostName)) {
            activePost.setObject((PostNode) postContainer.getContentFolder().getNode(selectedPostName));
        }
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        activePost.detach();
    }
    
    protected abstract BaseBlogContainer newBlogContainer();
}
