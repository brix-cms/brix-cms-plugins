package org.brixcms.plugin.content.blog.tile.post.comment;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.brixcms.plugin.content.blog.post.PostNode;
import org.brixcms.plugin.content.blog.tile.post.PostPanel;
import org.brixcms.web.generic.BrixGenericPanel;
import org.brixcms.web.nodepage.BrixPageParameters;
import org.brixcms.web.nodepage.PageParametersLink;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class CommentsLinkPanel extends BrixGenericPanel<PostNode> {

    public CommentsLinkPanel(String id, final IModel<PostNode> model) {
        super(id, model);
        PageParametersLink link = new PageParametersLink("link") {

            @Override
            protected void contributeToPageParameters(BrixPageParameters parameters) {
                super.contributeToPageParameters(parameters);
                parameters.set(0, getModelObject().getName());
                parameters.add(PostPanel.ANCHOR_PARAM_NAME, true);
            }

        };
        add(link.add(new Label("count", new IModel<Integer>() {

            @Override
            public Integer getObject() {
                return getModelObject().getComments(Integer.MAX_VALUE).size();
            }

        })));
    }
}
