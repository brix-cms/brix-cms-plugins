package org.brixcms.plugin.content.folder;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.brixcms.auth.Action.Context;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.content.ContentPlugin;
import org.brixcms.web.generic.BrixGenericPanel;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class FolderNodeViewTab extends BrixGenericPanel<BrixNode> {

    public FolderNodeViewTab(String id, final IModel<BrixNode> model) {
        super(id, model);
        add(new Label("title", new PropertyModel<String>(model, "title")));
        add(new Link<Void>("edit") {

            @Override
            public void onClick() {
                FolderNodeEditTab edit = new FolderNodeEditTab(FolderNodeViewTab.this.getId(), FolderNodeViewTab.this.getModel()) {

                    @Override
                    void goBack() {
                        replaceWith(FolderNodeViewTab.this);
                    }
                };
                FolderNodeViewTab.this.replaceWith(edit);
            }

            @Override
            public boolean isVisible() {
                BrixNode node = FolderNodeViewTab.this.getModelObject();
                return ContentPlugin.get().canEditNode(node, Context.ADMINISTRATION);
            }
        });
    }

}
