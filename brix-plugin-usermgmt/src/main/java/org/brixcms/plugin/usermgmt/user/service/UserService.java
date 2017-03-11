package org.brixcms.plugin.usermgmt.user.service;

import java.util.Set;

import org.brixcms.plugin.usermgmt.ManagementService;
import org.brixcms.plugin.usermgmt.user.User;

/**
 * @author dan.simko@gmail.com
 */
public interface UserService extends ManagementService<User> {

    void assignRoles(User user, Set<Long> assignedRolesIds);

}
