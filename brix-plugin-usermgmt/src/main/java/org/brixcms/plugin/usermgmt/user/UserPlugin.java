package org.brixcms.plugin.usermgmt.user;

import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.Brix;
import org.brixcms.plugin.jpa.JpaPluginLocator;
import org.brixcms.plugin.jpa.web.admin.GridDataSource;
import org.brixcms.plugin.usermgmt.BaseManagementPlugin;
import org.brixcms.plugin.usermgmt.ManagementService;
import org.brixcms.plugin.usermgmt.user.admin.UserEntityPlugin;
import org.brixcms.plugin.usermgmt.user.service.UserService;
import org.springframework.context.ApplicationContext;

import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.column.PropertyColumn;

/**
 * @author dan.simko@gmail.com
 */
public class UserPlugin extends BaseManagementPlugin<User, UserFilter> {

    public static final String ID = UserPlugin.class.getName();

    public UserPlugin(Brix brix, ApplicationContext context) {
        super(brix, context);
        registerEntityPlugin(new UserEntityPlugin(this));
    }

    public static UserPlugin get(Brix brix) {
        return (UserPlugin) brix.getPlugin(ID);
    }

    public static UserPlugin get() {
        return get(Brix.get());
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    protected IModel<String> getTabName() {
        return new ResourceModel("usermgmt-plugin.usersTabName");
    }

    @Override
    protected JpaPluginLocator<User, Long, UserFilter> getPluginLocator() {
        return new UserPluginLocator();
    }

    @Override
    public Class<User> getEntityClass() {
        return User.class;
    }

    @Override
    public void addGridColumns(List<IGridColumn<GridDataSource<User, Long, UserFilter>, User, Object>> columns) {
        columns.add(new PropertyColumn<GridDataSource<User, Long, UserFilter>, User, Object, Object>(
                new ResourceModel("usermgmt-plugin.username"), "username", "username"));
        columns.add(new PropertyColumn<GridDataSource<User, Long, UserFilter>, User, Object, Object>(
                new ResourceModel("usermgmt-plugin.email"), "email", "email"));

    }

    @Override
    protected ManagementService<User> getManagementService() {
        return context.getBean(UserService.class);
    }

    public boolean canEditUserRoles(User user) {
        return true;
    }

}
