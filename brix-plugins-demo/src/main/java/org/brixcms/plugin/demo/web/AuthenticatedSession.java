
package org.brixcms.plugin.demo.web;

import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.Request;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.plugin.usermgmt.user.User;
import org.brixcms.plugin.usermgmt.user.UserRepository;

@SuppressWarnings("serial")
public class AuthenticatedSession extends AuthenticatedWebSession {

    private Long userId;

    @SpringBean
    private UserRepository userRepository;

    public AuthenticatedSession(Request request) {
        super(request);
        Injector.get().inject(this);
    }

    public static AuthenticatedSession get() {
        return (AuthenticatedSession) Session.get();
    }

    @Override
    protected boolean authenticate(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            userId = user.getId();
            return true;
        }
        return false;
    }

    @Override
    public Roles getRoles() {
        if (isSignedIn()) {
            return new Roles(Roles.USER);
        }
        return new Roles();
    }

    public User getUser() {
        if (isSignedIn()) {
            return userRepository.findOne(userId);
        }
        return null;
    }
}