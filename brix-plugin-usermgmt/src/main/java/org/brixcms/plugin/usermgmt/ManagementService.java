package org.brixcms.plugin.usermgmt;

import java.util.List;

/**
 * @author dan.simko@gmail.com
 */
public interface ManagementService<T extends BaseEntity> {

    void save(T entity);

    void clone(List<T> entities);

    void delete(List<T> entities);

}
