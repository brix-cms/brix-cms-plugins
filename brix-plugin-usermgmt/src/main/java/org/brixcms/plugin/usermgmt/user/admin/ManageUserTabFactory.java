package org.brixcms.plugin.usermgmt.user.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.auth.Action.Context;
import org.brixcms.plugin.jpa.web.admin.ManageEntityTabFactory;
import org.brixcms.plugin.usermgmt.user.User;
import org.brixcms.plugin.usermgmt.user.UserPlugin;
import org.brixcms.web.tab.CachingAbstractTab;
import org.brixcms.web.tab.IBrixTab;

/**
 * @author dan.simko@gmail.com
 */
public class ManageUserTabFactory implements ManageEntityTabFactory<User> {

    @Override
    public List<IBrixTab> getManageNodeTabs(IModel<User> model) {
        return getTabs(model);
    }

    @SuppressWarnings("serial")
    private static List<IBrixTab> getTabs(final IModel<User> model) {
        List<IBrixTab> tabs = new ArrayList<IBrixTab>();

        tabs.add(new CachingAbstractTab(new ResourceModel("view", "View")) {
            @Override
            public Panel newPanel(String panelId) {
                return new ViewTab(panelId, model);
            }

            @Override
            public boolean isVisible() {
                return UserPlugin.get().canViewEntity(model.getObject(), Context.ADMINISTRATION);
            }
        });

        tabs.add(new CachingAbstractTab(new ResourceModel("roles", "Roles")) {
            @Override
            public Panel newPanel(String panelId) {
                return new UserRolesTab(panelId, model);
            }

        });

        return tabs;
    }

}
