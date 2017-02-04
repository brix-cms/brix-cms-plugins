package org.brixcms.plugin.content.blog.tile.archive;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.content.blog.post.PostNode;
import org.brixcms.plugin.content.blog.tile.navigation.BootstrapAjaxPagingNavigator;
import org.brixcms.plugin.content.blog.tile.post.PostDataProvider;
import org.brixcms.web.generic.BrixGenericPanel;
import org.brixcms.web.nodepage.BrixPageParameters;
import org.brixcms.web.nodepage.PageParametersAware;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class ArchivePanel extends BrixGenericPanel<BrixNode> implements PageParametersAware {

    private String postNodeName;

    public ArchivePanel(String id, IModel<BrixNode> model) {
        super(id, model);
        ArchiveContainer articleContainer = new ArchiveContainer();
        articleContainer.load(model.getObject());
        PostDataProvider articleDataProvider = new PostDataProvider(articleContainer);
        DataView<PostNode> dataView = new DataView<PostNode>("posts", articleDataProvider, articleContainer.getItemsPerPage()) {

            @Override
            protected void populateItem(Item<PostNode> item) {
                boolean selected = !Strings.isEmpty(postNodeName) && postNodeName.equals(item.getModelObject().getName());
                item.add(new ArchiveLinkPanel("post", item.getModel(), selected));
            }

        };
        WebMarkupContainer wmc = new WebMarkupContainer("wmc");
        wmc.add(dataView);
        wmc.add(new BootstrapAjaxPagingNavigator("navigator", dataView));
        add(wmc.setOutputMarkupId(true));
    }

    @Override
    public void contributeToPageParameters(BrixPageParameters params) {

    }

    @Override
    public void initializeFromPageParameters(BrixPageParameters params) {
        postNodeName = params.get(0).toString();
    }

}
