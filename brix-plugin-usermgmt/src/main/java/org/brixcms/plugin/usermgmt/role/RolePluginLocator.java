package org.brixcms.plugin.usermgmt.role;

import org.brixcms.plugin.jpa.JpaPlugin;
import org.brixcms.plugin.jpa.JpaPluginLocator;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class RolePluginLocator implements JpaPluginLocator<Role, Long, RoleFilter> {

    @Override
    public JpaPlugin<Role, Long, RoleFilter> getPlugin() {
        return RolePlugin.get();
    }

}
