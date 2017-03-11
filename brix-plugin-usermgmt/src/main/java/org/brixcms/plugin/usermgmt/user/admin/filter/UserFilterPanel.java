package org.brixcms.plugin.usermgmt.user.admin.filter;

import java.util.Arrays;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.brixcms.plugin.usermgmt.user.UserFilter;
import org.brixcms.web.generic.BrixGenericPanel;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class UserFilterPanel extends BrixGenericPanel<UserFilter> {

    public UserFilterPanel(String id, IModel<UserFilter> model) {
        super(id, new CompoundPropertyModel<>(model));
        add(new TextField<>("id"));
        add(new TextField<>("username"));
        add(new TextField<>("firstName"));
        add(new TextField<>("lastName"));
        add(new TextField<>("email"));
        add(new DropDownChoice<>("verified", Arrays.asList(null, Boolean.TRUE, Boolean.FALSE)));
    }

}
