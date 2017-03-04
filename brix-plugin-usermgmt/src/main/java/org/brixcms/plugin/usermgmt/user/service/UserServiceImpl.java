package org.brixcms.plugin.usermgmt.user.service;

import javax.persistence.EntityManager;

import org.brixcms.plugin.usermgmt.AbstractManagementService;
import org.brixcms.plugin.usermgmt.user.User;
import org.brixcms.plugin.usermgmt.user.UserRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

/**
 * @author dan.simko@gmail.com
 */

@Service
public class UserServiceImpl extends AbstractManagementService<User> implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository, EntityManager em) {
        super(em);
        this.userRepository = userRepository;
    }

    @Override
    protected CrudRepository<User, Long> getRepository() {
        return userRepository;
    }

}
