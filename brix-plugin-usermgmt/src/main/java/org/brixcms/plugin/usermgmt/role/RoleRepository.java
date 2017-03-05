package org.brixcms.plugin.usermgmt.role;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, Long> {

    @Query(value = "SELECT p FROM Role r JOIN r.permissions p WHERE r = ?1")
    List<Permission> getPermissions(Role role);

}
