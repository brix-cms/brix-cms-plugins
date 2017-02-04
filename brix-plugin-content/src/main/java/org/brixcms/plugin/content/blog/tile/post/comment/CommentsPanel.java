package org.brixcms.plugin.content.blog.tile.post.comment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.brixcms.BrixNodeModel;
import org.brixcms.plugin.content.blog.post.comment.CommentNode;
import org.brixcms.plugin.content.blog.post.comment.Commentable;
import org.brixcms.web.generic.BrixGenericPanel;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class CommentsPanel<T extends Commentable> extends BrixGenericPanel<T> {

    public CommentsPanel(String id, final IModel<T> model, boolean addCommentVisible) {
        super(id, model);
        add(new RefreshingView<CommentNode>("comments") {
            @Override
            protected Iterator<IModel<CommentNode>> getItemModels() {
                List<IModel<CommentNode>> list = new ArrayList<>();
                for (CommentNode comment : getModelObject().getComments(1)) {
                    list.add(new BrixNodeModel<CommentNode>(comment));
                }
                return list.iterator();
            }

            @Override
            protected void populateItem(Item<CommentNode> item) {
                item.add(new CommentPanel("comment", item.getModel()));
            }
        });
        add(new AddCommentPanel<>("addComment", model).setVisible(addCommentVisible));
    }
}
