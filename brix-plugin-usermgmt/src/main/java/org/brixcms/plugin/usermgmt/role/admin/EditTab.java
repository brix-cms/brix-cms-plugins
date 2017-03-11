package org.brixcms.plugin.usermgmt.role.admin;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.plugin.usermgmt.role.Role;
import org.brixcms.plugin.usermgmt.role.service.RoleService;
import org.brixcms.web.generic.BrixGenericPanel;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
abstract class EditTab extends BrixGenericPanel<Role> {

    @SpringBean
    private RoleService roleService;

    public EditTab(String id, final IModel<Role> model) {
        super(id, new CompoundPropertyModel<>(model));
        Form<Void> form = new Form<Void>("form");
        form.add(new TextField<>("name"));
        add(form);
        form.add(new Button("save") {
            @Override
            public void onSubmit() {
                roleService.save(EditTab.this.getModelObject());
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