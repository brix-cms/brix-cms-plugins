package org.brixcms.plugin.usermgmt.role.service;

import java.util.Set;

import org.brixcms.plugin.usermgmt.ManagementService;
import org.brixcms.plugin.usermgmt.role.Role;

/**
 * @author dan.simko@gmail.com
 */
public interface RoleService extends ManagementService<Role> {

    void assignPermissions(Role role, Set<Long> assignedPermissionsIds);

}
