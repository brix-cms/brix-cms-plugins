package brix.demo.service.impl;

import org.brixcms.demo.model.Member;
import org.brixcms.demo.model.Role;
import org.brixcms.demo.service.UserDAO;
import brix.plugins.springsecurity.BrixUserProvider;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.sql.SQLException;
import java.util.List;


/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date Apr 29, 2009 8:14:27 PM
 */
public class UserDAOImpl extends HibernateDaoSupport implements UserDAO, UserDetailsService, BrixUserProvider {
// ------------------------------ FIELDS ------------------------------

    private static final Logger log = LoggerFactory.getLogger(UserDAOImpl.class);

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface BrixUserProvider ---------------------


    @SuppressWarnings("unchecked")
    public List<Role> getAllAuthorities() {
        return getHibernateTemplate().loadAll(Role.class);
    }

    public GrantedAuthority getGrantedAuthorityByID(final String roleName) {
        return (GrantedAuthority) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createCriteria(Role.class).add(Restrictions.eq("roleName", roleName)).uniqueResult();
            }
        });
    }

// --------------------- Interface UserDAO ---------------------


    public void saveOrUpdate(Object toSave) {
        getHibernateTemplate().saveOrUpdate(toSave);
    }

// --------------------- Interface UserDetailsService ---------------------

    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException, DataAccessException {
        return (UserDetails) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createCriteria(Member.class).add(Restrictions.eq("username", username)).uniqueResult();
            }
        });
    }
}
