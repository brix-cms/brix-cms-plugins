package brix.demo.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UserDetails;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date Apr 29, 2009 8:20:38 PM
 */
@Entity
public class Member implements UserDetails {
// ------------------------------ FIELDS ------------------------------

    private static final Logger log = LoggerFactory.getLogger(Member.class);

    private Long id;
    private String username;
    private String password;
    private Set<Role> roles = new HashSet<Role>();

    // UserDetails defaults
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;

// --------------------------- CONSTRUCTORS ---------------------------

    public Member() {
    }

    public Member(String username, String password, Role... roles) {
        this.username = username;
        this.password = password;
        this.roles.addAll(Arrays.asList(roles));
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @ManyToMany(targetEntity = Role.class)
    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface UserDetails ---------------------

    @Transient
    public GrantedAuthority[] getAuthorities() {
        return roles.toArray(new GrantedAuthority[roles.size()]);
    }
}
