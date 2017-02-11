package org.brixcms.plugin.content.blog.tile.post;

import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.brixcms.plugin.content.blog.post.PostNode;
import org.brixcms.plugin.content.blog.tile.post.comment.CommentsLinkPanel;
import org.brixcms.plugin.content.blog.tile.post.comment.CommentsPanel;
import org.brixcms.web.generic.BrixGenericPanel;
import org.brixcms.web.nodepage.BrixPageParameters;
import org.brixcms.web.nodepage.PageParametersLink;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class PostPanel extends BrixGenericPanel<PostNode> {

    private static final String COMMENTS_PANEL_ID = "comments";
    public static final String ANCHOR_PARAM_NAME = "anchor";
    private boolean anchorExists;

    public PostPanel(String id, IModel<PostNode> model, boolean expanded) {
        super(id, new CompoundPropertyModel<>(model));

        PageParametersLink link = new PostLink("link", model);
        add(link.add(new Label("title")));
        add(new PostLink("link2", model));
        add(new Label("createdBy"));
        add(DateLabel.forDateStyle("publish", "FS"));
        add(new Label("dataAsString"));
        if (expanded) {
            add(new CommentsPanel<>(COMMENTS_PANEL_ID, model, true));
        } else {
            add(new CommentsLinkPanel(COMMENTS_PANEL_ID, model));
        }
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        anchorExists = BrixPageParameters.getCurrent().get(ANCHOR_PARAM_NAME).toBoolean(false);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        if (anchorExists) {
            response.render(new OnLoadHeaderItem("location.href='#comments-anchor';"));
        }
    }

    private static class PostLink extends PageParametersLink {

        public PostLink(String id, IModel<PostNode> model) {
            super(id, model);
        }

        @Override
        protected void contributeToPageParameters(BrixPageParameters parameters) {
            super.contributeToPageParameters(parameters);
            parameters.set(0, ((PostNode) getDefaultModelObject()).getName());
        }

    }
}
