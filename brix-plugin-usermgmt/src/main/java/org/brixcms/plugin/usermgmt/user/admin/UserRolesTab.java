package org.brixcms.plugin.usermgmt.user.admin;

import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.brixcms.plugin.usermgmt.user.User;
import org.brixcms.web.generic.BrixGenericPanel;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
class UserRolesTab extends BrixGenericPanel<User> {

    public UserRolesTab(String id, IModel<User> model) {
        super(id, new CompoundPropertyModel<>(model));
        // TODO implement
    }

}
