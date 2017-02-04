package org.brixcms.plugin.content.blog.tile.post.comment;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.brixcms.plugin.content.blog.post.comment.Commentable;
import org.brixcms.web.generic.BrixGenericPanel;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class AddCommentPanel<T extends Commentable> extends BrixGenericPanel<T> {

    private String comment;

    public AddCommentPanel(String id, IModel<T> model) {
        super(id, model);
        Form<Void> form = new Form<Void>("form") {
            @Override
            protected void onSubmit() {
                AddCommentPanel.this.getModelObject().addComment(comment);
            }
        };
        add(form);
        form.add(new TextArea<String>("comment", new PropertyModel<>(this, "comment")).setRequired(true));
    }

}
