package brix.demo.web;

import brix.Brix;
import brix.Plugin;
import brix.auth.AuthorizationStrategy;
import brix.config.BrixConfig;
import brix.demo.web.auth.LoginPage;
import brix.plugin.snapshot.SnapshotPlugin;
import brix.plugins.springsecurity.AuthorizationStrategyImpl;
import brix.plugins.springsecurity.UserPlugin;
import org.apache.wicket.AbstractRestartResponseException;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.springframework.security.core.context.SecurityContextHolder;

public class DemoBrix extends Brix {
    public AuthorizationStrategyImpl authStrategy;
// --------------------------- CONSTRUCTORS ---------------------------

    /**
     * Constructor
     *
     * @param config
     * @param authStrategy
     */
    public DemoBrix(BrixConfig config, AuthorizationStrategyImpl authStrategy) {
        super(config);

        config.getRegistry().register(Plugin.POINT, new SnapshotPlugin(this));
        config.getRegistry().register(Plugin.POINT, new UserPlugin(this));
        this.authStrategy = authStrategy;
    }

// -------------------------- OTHER METHODS --------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public AuthorizationStrategy newAuthorizationStrategy() {
        // register our simple demo auth strategy
        return authStrategy;
    }

    public AbstractRestartResponseException getForbiddenException() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return new RestartResponseAtInterceptPageException(LoginPage.class);
        } else {
            return super.getForbiddenException();
        }
    }
}
