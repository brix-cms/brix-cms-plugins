package org.brixcms.plugin.usermgmt.user.admin;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.plugin.jpa.web.admin.EntityPlugin;
import org.brixcms.plugin.site.SimpleCallback;
import org.brixcms.plugin.usermgmt.user.User;
import org.brixcms.plugin.usermgmt.user.UserPlugin;
import org.brixcms.plugin.usermgmt.user.admin.filter.UserFilterPlugin;

/**
 * @author dan.simko@gmail.com
 */
public class UserEntityPlugin implements EntityPlugin<User, Long> {

    public UserEntityPlugin(UserPlugin plugin) {
        plugin.registerFilterPlugin(new UserFilterPlugin());
        plugin.registerManageEntityTabFactory(new ManageUserTabFactory());
    }

    @Override
    public String getPluginId() {
        return UserEntityPlugin.class.getName();
    }

    @Override
    public Class<User> getEntityClass() {
        return User.class;
    }

    @Override
    public IModel<String> newCreateCaptionModel() {
        return new ResourceModel("usermgmt-plugin.createUser");
    }

    @Override
    public Panel newCreateEntityPanel(String id, SimpleCallback goBack) {
        return new CreateUserPanel(id, goBack);
    }

}
