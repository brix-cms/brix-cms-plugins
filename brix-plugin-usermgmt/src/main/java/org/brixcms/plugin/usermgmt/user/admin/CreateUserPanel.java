package org.brixcms.plugin.usermgmt.user.admin;

import java.util.Arrays;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.plugin.jpa.web.admin.EntityModel;
import org.brixcms.plugin.site.SimpleCallback;
import org.brixcms.plugin.usermgmt.user.User;
import org.brixcms.plugin.usermgmt.user.service.UserService;
import org.brixcms.web.ContainerFeedbackPanel;
import org.brixcms.web.generic.BrixGenericPanel;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class CreateUserPanel extends BrixGenericPanel<User> {

    @SpringBean
    private UserService userService;

    public CreateUserPanel(String id, SimpleCallback goBack) {
        super(id, new CompoundPropertyModel<User>(new EntityModel<>(new User())));
        Form<User> form = new Form<>("form");
        form.add(new TextField<>("username").setRequired(true));
        form.add(new TextField<>("firstName"));
        form.add(new TextField<>("lastName"));
        form.add(new TextField<>("email"));
        form.add(new DropDownChoice<>("verified", Arrays.asList(Boolean.TRUE, Boolean.FALSE)));
        add(form);

        form.add(new ContainerFeedbackPanel("feedback", this));

        form.add(new SubmitLink("create") {
            @Override
            public void onSubmit() {
                userService.save(getModelObject());
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
