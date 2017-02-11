package org.brixcms.plugin.content.blog.tile.post.comment;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.plugin.content.blog.post.comment.CommentNode;
import org.brixcms.web.generic.BrixGenericPanel;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class CommentPanel extends BrixGenericPanel<CommentNode> {

    private static final String REPLY_PANEL_ID = "reply";

    public CommentPanel(String id, IModel<CommentNode> model) {
        super(id, new CompoundPropertyModel<CommentNode>(model));
        setOutputMarkupId(true);
        add(new Label("createdBy"));
        add(DateLabel.forDateStyle("created", "FS"));
        add(new Label("dataAsString"));
        add(new EmptyPanel("reply"));
        add(new AjaxLink<Void>("replyLink") {

            @Override
            public boolean isVisible() {
                return CommentPanel.this.get(REPLY_PANEL_ID) instanceof EmptyPanel;
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                Component panel = CommentPanel.this.get(REPLY_PANEL_ID);
                panel.replaceWith(new AddCommentPanel<CommentNode>(REPLY_PANEL_ID, model));
                target.add(CommentPanel.this);
            }
        });
        add(new Link<CommentNode>("deleteLink", model) {

            @Override
            public void onClick() {
                CommentNode commentNode = getModelObject();
                JcrSession session = commentNode.getSession();
                commentNode.remove();
                session.save();
            }
        });
        add(new CommentsPanel<>("comments", model, false));
    }

}
