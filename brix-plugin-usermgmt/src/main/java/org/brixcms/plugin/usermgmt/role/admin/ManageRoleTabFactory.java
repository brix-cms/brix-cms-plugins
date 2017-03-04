package org.brixcms.plugin.usermgmt.role.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.auth.Action.Context;
import org.brixcms.plugin.jpa.web.admin.ManageEntityTabFactory;
import org.brixcms.plugin.usermgmt.role.Role;
import org.brixcms.plugin.usermgmt.role.RolePlugin;
import org.brixcms.web.tab.CachingAbstractTab;
import org.brixcms.web.tab.IBrixTab;

/**
 * @author dan.simko@gmail.com
 */
public class ManageRoleTabFactory implements ManageEntityTabFactory<Role> {

    @Override
    public List<IBrixTab> getManageNodeTabs(IModel<Role> model) {
        return getTabs(model);
    }

    @SuppressWarnings("serial")
    private static List<IBrixTab> getTabs(final IModel<Role> model) {
        List<IBrixTab> tabs = new ArrayList<IBrixTab>();

        tabs.add(new CachingAbstractTab(new ResourceModel("view", "View")) {
            @Override
            public Panel newPanel(String panelId) {
                return new ViewTab(panelId, model);
            }

            @Override
            public boolean isVisible() {
                return RolePlugin.get().canViewEntity(model.getObject(), Context.ADMINISTRATION);
            }
        });

        return tabs;
    }

}
