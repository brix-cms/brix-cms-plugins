package org.brixcms.plugin.usermgmt.role.admin;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.plugin.jpa.web.admin.EntityModel;
import org.brixcms.plugin.site.SimpleCallback;
import org.brixcms.plugin.usermgmt.role.Role;
import org.brixcms.plugin.usermgmt.role.service.RoleService;
import org.brixcms.web.ContainerFeedbackPanel;
import org.brixcms.web.generic.BrixGenericPanel;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class CreateRolePanel extends BrixGenericPanel<Role> {

    @SpringBean
    private RoleService roleService;

    public CreateRolePanel(String id, SimpleCallback goBack) {
        super(id, new CompoundPropertyModel<Role>(new EntityModel<>(new Role())));
        Form<?> form = new Form<CreateRolePanel>("form");
        add(form);
        form.add(new TextField<>("name").setRequired(true));
        form.add(new ContainerFeedbackPanel("feedback", this));
        form.add(new SubmitLink("create") {
            @Override
            public void onSubmit() {
                roleService.save(getModelObject());
                getSession().info(getString("usermgmt-plugin.status.saved"));
                goBack.execute();
            }
        });
        form.add(new Link<Void>("cancel") {
            @Override
            public void onClick() {
                getSession().info(getString("usermgmt-plugin.status.cancelled"));
                goBack.execute();
            }
        });
    }

}
