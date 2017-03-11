
package org.brixcms.plugin.jpa.web.admin;

import java.util.List;

import org.apache.wicket.model.IModel;
import org.brixcms.plugin.jpa.Persistable;
import org.brixcms.web.tab.IBrixTab;

/**
 * Factory for creating entity management tabs.
 *
 * @author dan.simko@gmail.com
 */
public interface ManageEntityTabFactory<T extends Persistable<?>> {

    /**
     * Returns list of management tabs for given entity.
     *
     * @param model
     * @return list of tabs
     */
    List<IBrixTab> getManageNodeTabs(IModel<T> model);
}
