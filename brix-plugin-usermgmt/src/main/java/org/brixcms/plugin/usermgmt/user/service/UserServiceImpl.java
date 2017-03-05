package org.brixcms.plugin.usermgmt.user.service;

import java.util.Set;

import javax.persistence.EntityManager;

import org.brixcms.plugin.usermgmt.AbstractManagementService;
import org.brixcms.plugin.usermgmt.role.RoleRepository;
import org.brixcms.plugin.usermgmt.user.User;
import org.brixcms.plugin.usermgmt.user.UserRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author dan.simko@gmail.com
 */

@Service
public class UserServiceImpl extends AbstractManagementService<User> implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, EntityManager em) {
        super(em);
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    protected CrudRepository<User, Long> getRepository() {
        return userRepository;
    }

    @Override
    @Transactional
    public void assignRoles(User user, Set<Long> assignedRolesIds) {
        User freshUser = userRepository.findOne(user.getId());
        freshUser.getRoles().clear();
        for (Long roleId : assignedRolesIds) {
            freshUser.getRoles().add(roleRepository.findOne(roleId));
        }
        save(freshUser);
    }
}
