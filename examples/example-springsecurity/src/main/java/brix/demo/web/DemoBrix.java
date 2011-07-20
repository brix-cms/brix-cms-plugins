package brix.demo.web;

import brix.demo.web.auth.LoginPage;
import brix.plugins.springsecurity.AuthorizationStrategyImpl;
import brix.plugins.springsecurity.UserPlugin;
import org.apache.wicket.RestartResponseException;
import org.brixcms.Brix;
import org.brixcms.Plugin;
import org.brixcms.auth.AuthorizationStrategy;
import org.brixcms.config.BrixConfig;
import org.brixcms.plugin.snapshot.SnapshotPlugin;
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

    public RestartResponseException getForbiddenException() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return new RestartResponseException(LoginPage.class);
        } else {
            return super.getForbiddenException();
        }
    }
}
