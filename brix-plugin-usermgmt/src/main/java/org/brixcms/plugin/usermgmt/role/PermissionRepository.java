package org.brixcms.plugin.usermgmt.role;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface PermissionRepository extends CrudRepository<Permission, Long> {

    @Query(value = "SELECT p.name FROM Permission p")
    List<Permission> getAllPermissionsNames();
}
