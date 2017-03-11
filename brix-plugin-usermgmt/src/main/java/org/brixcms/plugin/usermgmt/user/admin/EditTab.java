package org.brixcms.plugin.usermgmt.user.admin;

import java.util.Arrays;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.plugin.usermgmt.user.User;
import org.brixcms.plugin.usermgmt.user.service.UserService;
import org.brixcms.web.generic.BrixGenericPanel;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
abstract class EditTab extends BrixGenericPanel<User> {

    @SpringBean
    private UserService userService;

    public EditTab(String id, final IModel<User> model) {
        super(id, new CompoundPropertyModel<>(model));
        Form<Void> form = new Form<Void>("form");
        form.add(new TextField<>("username").setEnabled(false));
        form.add(new TextField<>("firstName"));
        form.add(new TextField<>("lastName"));
        form.add(new TextField<>("email"));
        form.add(new DropDownChoice<>("verified", Arrays.asList(Boolean.TRUE, Boolean.FALSE)));
        add(form);
        form.add(new Button("save") {
            @Override
            public void onSubmit() {
                userService.save(EditTab.this.getModelObject());
                getSession().info(getString("usermgmt-plugin.status.saved"));
                goBack();
            }
        });
        form.add(new Link<Void>("cancel") {
            @Override
            public void onClick() {
                getSession().info(getString("usermgmt-plugin.status.cancelled"));
                goBack();
            }
        });
    }

    abstract void goBack();
}