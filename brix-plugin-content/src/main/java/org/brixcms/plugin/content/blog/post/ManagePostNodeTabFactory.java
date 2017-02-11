package org.brixcms.plugin.content.blog.post;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.auth.Action.Context;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.content.ContentPlugin;
import org.brixcms.plugin.content.blog.post.admin.PostViewTab;
import org.brixcms.plugin.site.ManageNodeTabFactory;
import org.brixcms.web.tab.CachingAbstractTab;
import org.brixcms.web.tab.IBrixTab;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class ManagePostNodeTabFactory implements ManageNodeTabFactory {

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<IBrixTab> getManageNodeTabs(IModel<BrixNode> nodeModel) {
        String type = nodeModel.getObject().getNodeType();
        if (PostNodePlugin.TYPE.equals(type)) {
            return getTabs((IModel) nodeModel);
        } else {
            return null;
        }
    }

    public static List<IBrixTab> getTabs(final IModel<PostNode> nodeModel) {
        List<IBrixTab> tabs = new ArrayList<IBrixTab>(1);
        IBrixTab previewTab = new CachingAbstractTab(new ResourceModel("view", "View"), 300) {

            @Override
            public Panel newPanel(String panelId) {
                return new PostViewTab(panelId, nodeModel);
            }

            @Override
            public boolean isVisible() {
                return ContentPlugin.get().canViewPostNode(nodeModel.getObject(), Context.ADMINISTRATION);
            }

        };

        tabs.add(previewTab);
        return tabs;
    }

}
