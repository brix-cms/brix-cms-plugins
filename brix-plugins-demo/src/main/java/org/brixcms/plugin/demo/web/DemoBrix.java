package org.brixcms.plugin.demo.web;

import org.brixcms.Brix;
import org.brixcms.Plugin;
import org.brixcms.auth.AuthorizationStrategy;
import org.brixcms.config.BrixConfig;
import org.brixcms.plugin.content.ContentPlugin;
import org.brixcms.plugin.menu.MenuPlugin;
import org.brixcms.plugin.prototype.PrototypePlugin;
import org.brixcms.plugin.snapshot.SnapshotPlugin;
import org.brixcms.plugin.usermgmt.role.RolePlugin;
import org.brixcms.plugin.usermgmt.user.UserPlugin;
import org.brixcms.plugin.webdavurl.WebdavUrlPlugin;
import org.springframework.context.ApplicationContext;

/**
 * Subclass of {@link Brix} that configures demo-specific settings such as
 * plugins, tiles, etc.
 */
public class DemoBrix extends Brix {

    /**
     * Constructor
     *
     * @param config
     */
    public DemoBrix(BrixConfig config, ApplicationContext context) {
        super(config);

        // register plugins
        config.getRegistry().register(Plugin.POINT, new MenuPlugin(this));
        config.getRegistry().register(Plugin.POINT, new SnapshotPlugin(this));
        config.getRegistry().register(Plugin.POINT, new PrototypePlugin(this));
        config.getRegistry().register(Plugin.POINT, new WebdavUrlPlugin());
        config.getRegistry().register(Plugin.POINT, new ContentPlugin(this));
        config.getRegistry().register(Plugin.POINT, new UserPlugin(this, context));
        config.getRegistry().register(Plugin.POINT, new RolePlugin(this, context));

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuthorizationStrategy newAuthorizationStrategy() {
        // register our simple demo auth strategy
        return new DemoAuthorizationStrategy();
    }

}
