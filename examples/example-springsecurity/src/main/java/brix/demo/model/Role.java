package brix.demo.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;
import java.util.HashSet;
import java.util.Set;


/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date Apr 29, 2009 8:20:53 PM
 */

@Entity
public class Role implements GrantedAuthority {
// ------------------------------ FIELDS ------------------------------

    private static final Logger log = LoggerFactory.getLogger(Role.class);

    private Long id;
    private String roleName;
    private Set<Member> members = new HashSet<Member>();

// --------------------------- CONSTRUCTORS ---------------------------

    public Role() {
    }

    public Role(String roleName) {
        this.roleName = roleName;
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

    @ManyToMany(targetEntity = Member.class, mappedBy = "roles")
    public Set<Member> getMembers() {
        return members;
    }

    public void setMembers(Set<Member> members) {
        this.members = members;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Comparable ---------------------


    public int compareTo(Object o) {
        if (o != null && o instanceof GrantedAuthority) {
            String rhsRole = ((GrantedAuthority) o).getAuthority();

            if (rhsRole == null) {
                return -1;
            }

            return getRoleName().compareTo(rhsRole);
        }
        return -1;
    }

// --------------------- Interface GrantedAuthority ---------------------

    @Transient
    public String getAuthority() {
        return getRoleName();
    }
}
