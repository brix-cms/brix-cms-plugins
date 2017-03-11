package org.brixcms.plugin.usermgmt.user;

import java.util.List;
import java.util.Set;

import org.brixcms.plugin.usermgmt.role.Role;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findByLastName(String lastName);

    User findByUsername(String username);

    @Query(value = "SELECT r FROM User u JOIN u.roles r WHERE u = ?1")
    Set<Role> getRoles(User user);

    @Query(value = "SELECT p.name FROM User u JOIN u.roles r JOIN r.permissions p WHERE u = ?1")
    Set<String> getPermissionsNames(User user);

}
