package org.brixcms.plugin.content.folder;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.admin.NodeManagerPanel;
import org.brixcms.web.ContainerFeedbackPanel;
import org.brixcms.web.model.ModelBuffer;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
abstract class FolderNodeEditTab extends NodeManagerPanel {

    public FolderNodeEditTab(String id, final IModel<BrixNode> nodeModel) {
        super(id, nodeModel);
        Form<Void> form = new Form<Void>("form");
        add(form);
        final ModelBuffer adapter = new ModelBuffer(nodeModel);
        IModel<String> titleModel = adapter.forProperty("title");
        form.add(new TextField<String>("title", titleModel).setRequired(true));
        form.add(new ContainerFeedbackPanel("feedback", this));
        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                JcrNode node = nodeModel.getObject();
                node.checkout();
                adapter.apply();
                node.save();
                node.checkin();
                getSession().info(getString("content-plugin.status.saved"));
                goBack();
            }
        });

        form.add(new Link<Void>("cancel") {

            @Override
            public void onClick() {
                getSession().info(getString("content-plugin.status.cancelled"));
                goBack();
            }

        });
    }

    abstract void goBack();

}