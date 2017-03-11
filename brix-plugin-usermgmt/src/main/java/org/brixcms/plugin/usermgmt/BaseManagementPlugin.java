package org.brixcms.plugin.usermgmt;

import java.util.List;

import org.brixcms.Brix;
import org.brixcms.plugin.jpa.JpaPlugin;
import org.springframework.context.ApplicationContext;

/**
 * @author dan.simko@gmail.com
 */
public abstract class BaseManagementPlugin<T extends BaseEntity, F extends BaseEntityFilter> extends JpaPlugin<T, Long, F> {

    protected final ApplicationContext context;

    public BaseManagementPlugin(Brix brix, ApplicationContext context) {
        super(brix);
        this.context = context;
    }

    protected abstract ManagementService<T> getManagementService();

    @Override
    public void clone(List<T> entities) {
        getManagementService().clone(entities);
    }

    @Override
    public void delete(List<T> entities) {
        getManagementService().delete(entities);
    }
}
