package org.brixcms.plugin.demo.web;

import javax.jcr.Credentials;
import javax.jcr.Repository;
import javax.jcr.SimpleCredentials;

import org.brixcms.jcr.ThreadLocalSessionFactory;
import org.brixcms.plugin.usermgmt.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dan.simko@gmail.com
 */
public class DemoThreadLocalSessionFactory extends ThreadLocalSessionFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DemoThreadLocalSessionFactory.class);

    public DemoThreadLocalSessionFactory(Repository repository, Credentials credentials) {
        super(repository, credentials);
    }

    @Override
    protected Credentials getCredentials() {
        AuthenticatedSession session;
        try {
            session = AuthenticatedSession.get();
        } catch (Exception e) {
            LOG.trace(e.getMessage(), e);
            return super.getCredentials();
        }
        User user = session.getUser();
        if (user == null) {
            return super.getCredentials();
        }
        return new SimpleCredentials(user.getUsername(), new char[0]);
    }

}
