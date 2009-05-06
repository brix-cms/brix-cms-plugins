package brix.plugins.springsecurity;

import org.springframework.security.GrantedAuthority;

import java.util.List;

/**
 * This interface should be typically implemented by a DAO and registered in your Spring context.
 * If your user store is in a database, this means you would probably be adding this interface to
 * a service bean that also implements <code>org.springframework.security.userdetails.UserDetailsService</code>.
 * If your user store is in LDAP, you'll need to create a service bean that extends LdapTemplate
 * and is capable of interrogating and caching the correct groups.
 *
 * todo: Understand changes in Spring 3.0, whether GrantedAuthority has any additional labels, or implement
 * our own, which Spring Security fully supports.
 *
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date Apr 28, 2009 9:39:44 PM
 */
public interface BrixUserProvider {
// -------------------------- OTHER METHODS --------------------------

    List<GrantedAuthority> getAllAuthorities();
    GrantedAuthority getGrantedAuthorityByID(String string);
}
