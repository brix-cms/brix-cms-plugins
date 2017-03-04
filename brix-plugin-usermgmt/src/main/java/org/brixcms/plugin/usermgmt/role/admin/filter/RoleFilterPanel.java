package org.brixcms.plugin.usermgmt.role.admin.filter;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.brixcms.plugin.usermgmt.role.RoleFilter;
import org.brixcms.web.generic.BrixGenericPanel;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class RoleFilterPanel extends BrixGenericPanel<RoleFilter> {

    public RoleFilterPanel(String id, IModel<RoleFilter> model) {
        super(id, new CompoundPropertyModel<>(model));
        add(new TextField<>("id"));
        add(new TextField<>("name"));
    }

}
