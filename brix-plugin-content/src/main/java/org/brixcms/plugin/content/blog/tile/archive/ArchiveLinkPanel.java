package org.brixcms.plugin.content.blog.tile.archive;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.brixcms.plugin.content.blog.post.PostNode;
import org.brixcms.web.generic.BrixGenericPanel;
import org.brixcms.web.nodepage.BrixPageParameters;
import org.brixcms.web.nodepage.PageParametersLink;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class ArchiveLinkPanel extends BrixGenericPanel<PostNode> {

    public ArchiveLinkPanel(String id, final IModel<PostNode> model, boolean selected) {
        super(id, new CompoundPropertyModel<>(model));
        PageParametersLink link = new PageParametersLink("link") {

            @Override
            protected void contributeToPageParameters(BrixPageParameters parameters) {
                super.contributeToPageParameters(parameters);
                parameters.set(0, model.getObject().getName());
            }
        };
        add(link);
        link.add(new Label("title"));
        if (selected) {
            link.add(new AttributeModifier("class", "active"));
        }

    }

}
