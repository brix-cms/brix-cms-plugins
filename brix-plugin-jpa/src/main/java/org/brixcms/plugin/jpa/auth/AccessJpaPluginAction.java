package org.brixcms.plugin.jpa.auth;

import java.io.Serializable;

import org.brixcms.auth.Action;
import org.brixcms.plugin.jpa.JpaPluginLocator;
import org.brixcms.plugin.jpa.Persistable;

/**
 * @author dan.simko@gmail.com
 */
public class AccessJpaPluginAction<T extends Persistable<ID>, ID extends Serializable, F extends Serializable> implements Action {

    private final JpaPluginLocator<T, ID, F> pluginLocator;

    public AccessJpaPluginAction(JpaPluginLocator<T, ID, F> pluginLocator) {
        this.pluginLocator = pluginLocator;
    }

    public JpaPluginLocator<T, ID, F> getPluginLocator() {
        return pluginLocator;
    }

    @Override
    public String toString() {
        return "AccessJpaPluginAction";
    }

    @Override
    public Context getContext() {
        return Context.ADMINISTRATION;
    }

}
