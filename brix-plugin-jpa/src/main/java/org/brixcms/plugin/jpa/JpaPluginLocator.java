package org.brixcms.plugin.jpa;

import java.io.Serializable;

/**
 * This is a serializable lookup service that can be passed to Wicket components
 * so that they can find the plugin they are working for.
 * 
 * @author dan.simko@gmail.com
 */
public interface JpaPluginLocator<T extends Persistable<ID>, ID extends Serializable, F extends Serializable> extends Serializable {

    JpaPlugin<T, ID, F> getPlugin();

}
