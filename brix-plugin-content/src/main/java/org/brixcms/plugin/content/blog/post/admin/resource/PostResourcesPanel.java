package org.brixcms.plugin.content.blog.post.admin.resource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.brixcms.BrixNodeModel;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.api.JcrNodeIterator;
import org.brixcms.jcr.wrapper.BrixFileNode;
import org.brixcms.plugin.content.blog.post.PostNode;
import org.brixcms.web.generic.BrixGenericPanel;

@SuppressWarnings("serial")
public class PostResourcesPanel extends BrixGenericPanel<PostNode> {

    public PostResourcesPanel(String id, IModel<PostNode> model) {
        super(id, model);
        add(new RefreshingView<BrixFileNode>("resources") {
            @Override
            protected Iterator<IModel<BrixFileNode>> getItemModels() {
                List<IModel<BrixFileNode>> models = new ArrayList<>();
                JcrNodeIterator it = getModelObject().getResourcesFolder().getNodes();
                while (it.hasNext()) {
                    JcrNode node = (JcrNode) it.next();
                    if (node instanceof BrixFileNode) {
                        models.add(new BrixNodeModel<>((BrixFileNode) node));
                    }
                }
                return models.iterator();
            }

            @Override
            protected void populateItem(Item<BrixFileNode> item) {
                item.add(new PostResourcePanel("resource", item.getModel()));
            }
        });
    }

}
