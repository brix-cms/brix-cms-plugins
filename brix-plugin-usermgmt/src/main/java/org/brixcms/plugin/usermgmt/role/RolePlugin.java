package org.brixcms.plugin.usermgmt.role;

import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.Brix;
import org.brixcms.plugin.jpa.JpaPluginLocator;
import org.brixcms.plugin.jpa.web.admin.GridDataSource;
import org.brixcms.plugin.usermgmt.BaseManagementPlugin;
import org.brixcms.plugin.usermgmt.ManagementService;
import org.brixcms.plugin.usermgmt.role.admin.RoleEntityPlugin;
import org.brixcms.plugin.usermgmt.role.service.RoleService;
import org.springframework.context.ApplicationContext;

import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.column.PropertyColumn;

/**
 * @author dan.simko@gmail.com
 */
public class RolePlugin extends BaseManagementPlugin<Role, RoleFilter> {

    public static final String ID = RolePlugin.class.getName();

    public RolePlugin(Brix brix, ApplicationContext context) {
        super(brix, context);
        registerEntityPlugin(new RoleEntityPlugin(this));
    }

    public static RolePlugin get(Brix brix) {
        return (RolePlugin) brix.getPlugin(ID);
    }

    public static RolePlugin get() {
        return get(Brix.get());
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    protected IModel<String> getTabName() {
        return new ResourceModel("usermgmt-plugin.rolesTabName");
    }

    @Override
    protected JpaPluginLocator<Role, Long, RoleFilter> getPluginLocator() {
        return new RolePluginLocator();
    }

    @Override
    public Class<Role> getEntityClass() {
        return Role.class;
    }

    @Override
    public void addGridColumns(List<IGridColumn<GridDataSource<Role, Long, RoleFilter>, Role, Object>> columns) {
        columns.add(new PropertyColumn<GridDataSource<Role, Long, RoleFilter>, Role, Object, Object>(new ResourceModel("usermgmt-plugin.name"),
                "name", "name"));
    }

    @Override
    protected ManagementService<Role> getManagementService() {
        return context.getBean(RoleService.class);
    }

}
