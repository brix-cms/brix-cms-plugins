package org.brixcms.plugin.usermgmt.role.service;

import java.util.Set;

import javax.persistence.EntityManager;

import org.brixcms.plugin.usermgmt.AbstractManagementService;
import org.brixcms.plugin.usermgmt.role.PermissionRepository;
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
    private final PermissionRepository permissionRepository;

    public RoleServiceImpl(RoleRepository roleRepository, PermissionRepository permissionRepository, EntityManager em) {
        super(em);
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    protected CrudRepository<Role, Long> getRepository() {
        return roleRepository;
    }

    @Override
    @Transactional
    public void assignPermissions(Role role, Set<Long> assignedPermissionsIds) {
        Role freshRole = roleRepository.findOne(role.getId());
        freshRole.getPermissions().clear();
        for (Long permissionId : assignedPermissionsIds) {
            freshRole.getPermissions().add(permissionRepository.findOne(permissionId));
        }
        save(freshRole);
    }
}
