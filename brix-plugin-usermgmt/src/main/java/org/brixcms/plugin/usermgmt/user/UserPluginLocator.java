package org.brixcms.plugin.usermgmt.user;

import org.brixcms.plugin.jpa.JpaPlugin;
import org.brixcms.plugin.jpa.JpaPluginLocator;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class UserPluginLocator implements JpaPluginLocator<User, Long, UserFilter> {

    @Override
    public JpaPlugin<User, Long, UserFilter> getPlugin() {
        return UserPlugin.get();
    }

}
