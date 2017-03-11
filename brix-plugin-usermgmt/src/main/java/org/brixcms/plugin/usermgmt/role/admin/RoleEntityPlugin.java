package org.brixcms.plugin.usermgmt.role.admin;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.plugin.jpa.web.admin.EntityPlugin;
import org.brixcms.plugin.site.SimpleCallback;
import org.brixcms.plugin.usermgmt.role.Role;
import org.brixcms.plugin.usermgmt.role.RolePlugin;
import org.brixcms.plugin.usermgmt.role.admin.filter.RoleFilterPlugin;

/**
 * @author dan.simko@gmail.com
 */
public class RoleEntityPlugin implements EntityPlugin<Role, Long> {

    public RoleEntityPlugin(RolePlugin plugin) {
        plugin.registerFilterPlugin(new RoleFilterPlugin());
        plugin.registerManageEntityTabFactory(new ManageRoleTabFactory());
    }

    @Override
    public String getPluginId() {
        return RoleEntityPlugin.class.getName();
    }

    @Override
    public Class<Role> getEntityClass() {
        return Role.class;
    }

    @Override
    public IModel<String> newCreateCaptionModel() {
        return new ResourceModel("usermgmt-plugin.createRole");
    }

    @Override
    public Panel newCreateEntityPanel(String id, SimpleCallback goBack) {
        return new CreateRolePanel(id, goBack);
    }

}
