package org.brixcms.plugin.usermgmt.role.service;

import javax.persistence.EntityManager;

import org.brixcms.plugin.usermgmt.AbstractManagementService;
import org.brixcms.plugin.usermgmt.role.Role;
import org.brixcms.plugin.usermgmt.role.RoleRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author dan.simko@gmail.com
 */

@Service
@Transactional
public class RoleServiceImpl extends AbstractManagementService<Role> implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository, EntityManager em) {
        super(em);
        this.roleRepository = roleRepository;
    }

    @Override
    protected CrudRepository<Role, Long> getRepository() {
        return roleRepository;
    }

}
