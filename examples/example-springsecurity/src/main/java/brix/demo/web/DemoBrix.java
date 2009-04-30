package brix.demo.web;

import brix.Brix;
import brix.Plugin;
import brix.demo.web.auth.LoginPage;
import brix.auth.AuthorizationStrategy;
import brix.config.BrixConfig;
import brix.plugin.snapshot.SnapshotPlugin;
import brix.plugins.springsecurity.AuthorizationStrategyImpl;
import brix.plugins.springsecurity.UserPlugin;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.AbstractRestartResponseException;
import org.springframework.security.context.SecurityContextHolder;

public class DemoBrix extends Brix {
// --------------------------- CONSTRUCTORS ---------------------------

    /**
     * Constructor
     *
     * @param config
     */
    public DemoBrix(BrixConfig config) {
        super(config);

        config.getRegistry().register(Plugin.POINT, new SnapshotPlugin(this));
        config.getRegistry().register(Plugin.POINT, new UserPlugin(this));
    }

// -------------------------- OTHER METHODS --------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public AuthorizationStrategy newAuthorizationStrategy() {
        // register our simple demo auth strategy
        return new AuthorizationStrategyImpl();
    }

    public AbstractRestartResponseException getForbiddenException() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return new RestartResponseAtInterceptPageException(LoginPage.class);
        } else {
            return super.getForbiddenException();
        }
    }
}
