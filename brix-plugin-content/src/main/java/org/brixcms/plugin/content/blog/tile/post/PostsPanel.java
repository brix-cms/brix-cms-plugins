package org.brixcms.plugin.content.blog.tile.post;

import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.content.blog.post.PostNode;
import org.brixcms.plugin.content.blog.tile.navigation.StatelessPager;
import org.brixcms.plugin.content.blog.tile.navigation.StatelessPagination;
import org.brixcms.web.generic.BrixGenericPanel;
import org.brixcms.web.nodepage.BrixPageParameters;
import org.brixcms.web.nodepage.PageParametersAware;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class PostsPanel extends BrixGenericPanel<BrixNode> implements PageParametersAware {

    private final DataView<PostNode> dataView;

    public PostsPanel(String id, IModel<BrixNode> tileNodeModel) {
        super(id, tileNodeModel);
        PostContainer postContainer = new PostContainer();
        postContainer.load(tileNodeModel.getObject());
        PostDataProvider articleDataProvider = new PostDataProvider(postContainer);
        dataView = new DataView<PostNode>("posts", articleDataProvider, postContainer.getItemsPerPage()) {

            @Override
            protected void populateItem(Item<PostNode> item) {
                item.add(new PostPanel("post", item.getModel(), false));
            }

        };
        add(dataView);
        add(new StatelessPager("navigator", dataView));
    }

    @Override
    public void contributeToPageParameters(BrixPageParameters params) {

    }

    @Override
    public void initializeFromPageParameters(BrixPageParameters params) {
        int page = params.get(StatelessPagination.PAGE_PARAM_NAME).toInt(0);
        if (page > 0) {
            dataView.setCurrentPage(page);
        }
    }

}
