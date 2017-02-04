package org.brixcms.plugin.content.blog.post.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.auth.Action.Context;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.content.ContentPlugin;
import org.brixcms.plugin.content.blog.post.PostNode;
import org.brixcms.web.generic.BrixGenericPanel;
import org.brixcms.web.tab.BrixCardPanel;
import org.brixcms.web.tab.CachingAbstractTab;
import org.brixcms.web.tab.IBrixTab;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class PostViewTab extends BrixGenericPanel<PostNode> {

    public PostViewTab(String id, final IModel<PostNode> model) {
        super(id, new CompoundPropertyModel<>(model));
        add(new Label("title"));
        add(new Label("name"));
        add(new Label("created"));
        add(new Label("createdBy"));
        add(new Label("lastModified"));
        add(new Label("lastModifiedBy"));
        add(new Label("publish"));
        add(new Label("visibility"));
        add(new Label("state"));
        List<IBrixTab> tabs = new ArrayList<IBrixTab>();
        tabs.add(new CachingAbstractTab(new ResourceModel("content-plugin.textPreview")) {
            private static final long serialVersionUID = 1L;

            @Override
            public Panel newPanel(String panelId) {
                return new TextPreviewPanel(panelId, model);
            }
        });
        add(new BrixCardPanel("previewTabbedPanel", tabs));
        add(new Link<Void>("edit") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                PostEditTab edit = new PostEditTab(PostViewTab.this.getId(), PostViewTab.this.getModel()) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    void goBack() {
                        replaceWith(PostViewTab.this);
                    }
                };
                PostViewTab.this.replaceWith(edit);
            }

            @Override
            public boolean isVisible() {
                BrixNode node = PostViewTab.this.getModelObject();
                return ContentPlugin.get().canEditNode(node, Context.ADMINISTRATION);
            }
        });
    }

    private class TextPreviewPanel extends Panel {

        public TextPreviewPanel(String id, IModel<PostNode> model) {
            super(id, new CompoundPropertyModel<PostNode>(model));
            add(new Label("dataAsString"));
        }
    }

}
