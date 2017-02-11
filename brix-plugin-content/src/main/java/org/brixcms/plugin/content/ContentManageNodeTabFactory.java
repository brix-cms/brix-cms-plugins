package org.brixcms.plugin.content;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.auth.Action.Context;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.content.blog.post.PostNode;
import org.brixcms.plugin.content.blog.post.admin.PostViewTab;
import org.brixcms.plugin.content.folder.FolderNodeViewTab;
import org.brixcms.plugin.site.ManageNodeTabFactory;
import org.brixcms.web.tab.CachingAbstractTab;
import org.brixcms.web.tab.IBrixTab;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class ContentManageNodeTabFactory implements ManageNodeTabFactory {

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<IBrixTab> getManageNodeTabs(IModel<BrixNode> nodeModel) {
        List<IBrixTab> result = new ArrayList<IBrixTab>();
        BrixNode node = nodeModel.getObject();
        if (node.isFolder()) {
            return getFolderTabs(nodeModel);
        } else if (node instanceof PostNode) {
            return getContentNodeTabs((IModel) nodeModel);
        }
        return result;
    }

    public static List<IBrixTab> getFolderTabs(final IModel<BrixNode> folderModel) {
        List<IBrixTab> tabs = new ArrayList<IBrixTab>(1);
        tabs.add(new CachingAbstractTab(new ResourceModel("view", "View"), 100) {

            @Override
            public Panel newPanel(String panelId) {
                return new FolderNodeViewTab(panelId, folderModel);
            }

        });
        return tabs;
    }

    public static List<IBrixTab> getContentNodeTabs(final IModel<PostNode> nodeModel) {
        List<IBrixTab> tabs = new ArrayList<IBrixTab>(1);
        IBrixTab previewTab = new CachingAbstractTab(new ResourceModel("view", "View"), 100) {

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
