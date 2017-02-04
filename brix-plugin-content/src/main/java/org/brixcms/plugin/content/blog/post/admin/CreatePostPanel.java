package org.brixcms.plugin.content.blog.post.admin;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.content.ContentPlugin;
import org.brixcms.plugin.content.blog.post.PostNode;
import org.brixcms.plugin.content.blog.post.PostNode.State;
import org.brixcms.plugin.content.blog.post.PostNode.Visibility;
import org.brixcms.plugin.site.SimpleCallback;
import org.brixcms.web.ContainerFeedbackPanel;
import org.brixcms.web.generic.BrixGenericPanel;
import org.brixcms.web.util.validators.NodeNameValidator;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class CreatePostPanel extends BrixGenericPanel<BrixNode> {

    private String name;

    public CreatePostPanel(String id, IModel<BrixNode> containerNodeModel, final SimpleCallback goBack) {
        super(id, containerNodeModel);

        Form<?> form = new Form<CreatePostPanel>("form", new CompoundPropertyModel<CreatePostPanel>(this));
        add(form);

        form.add(new ContainerFeedbackPanel("feedback", this));

        form.add(new SubmitLink("create") {
            @Override
            public void onSubmit() {
                createPost();
            }
        });

        form.add(new Link<Void>("cancel") {
            @Override
            public void onClick() {
                goBack.execute();
            }
        });

        final TextField<String> tf;
        form.add(tf = new TextField<String>("name"));
        tf.setRequired(true);
        tf.add(NodeNameValidator.getInstance());
    }

    private void createPost() {
        final JcrNode parent = getModelObject();
        String permalink = ContentPlugin.normalizeValue(name, PostNode.MAX_PERMALINK_LENGTH);
        if (parent.hasNode(permalink)) {
            String error = getString("content-plugin.postExists", new Model<CreatePostPanel>(CreatePostPanel.this));
            error(error);
        } else {
            JcrNode post = parent.addNode(permalink, "nt:file");
            PostNode node = PostNode.initialize(post);
            node.setTitle(name);
            node.setState(State.Draft);
            node.setVisibility(Visibility.Private);
            node.setData("");
            name = null;
            parent.save();
            ContentPlugin.get().selectNode(this, node, true);
        }
    }
}
