package org.brixcms.plugin.usermgmt.user.admin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.plugin.jpa.web.admin.EntityModel;
import org.brixcms.plugin.usermgmt.role.Role;
import org.brixcms.plugin.usermgmt.role.RoleRepository;
import org.brixcms.plugin.usermgmt.user.User;
import org.brixcms.plugin.usermgmt.user.UserRepository;
import org.brixcms.plugin.usermgmt.user.service.UserService;
import org.brixcms.web.generic.BrixGenericPanel;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
abstract class UserRolesEditTab extends BrixGenericPanel<User> {

    @SpringBean
    private RoleRepository roleRepository;
    @SpringBean
    private UserRepository userRepository;
    @SpringBean
    private UserService userService;
    private Set<Long> assignedRolesIds = new HashSet<>();

    public UserRolesEditTab(String id, IModel<User> model) {
        super(id, new CompoundPropertyModel<>(model));
        add(new RefreshingView<Role>("roles") {

            @Override
            protected Iterator<IModel<Role>> getItemModels() {
                List<IModel<Role>> models = new ArrayList<>();
                for (Role role : roleRepository.findAll()) {
                    models.add(new EntityModel<Role, Long>(role));
                }
                return models.iterator();
            }

            @Override
            protected void populateItem(Item<Role> item) {
                item.add(new Label("name", new PropertyModel<>(item.getModel(), "name")));
                item.add(new CheckBox("checkbox", new IModel<Boolean>() {
                    @Override
                    public Boolean getObject() {
                        return assignedRolesIds.contains(item.getModelObject().getId());
                    }

                    @Override
                    public void setObject(Boolean bool) {
                        if (bool) {
                            assignedRolesIds.add(item.getModelObject().getId());
                        } else {
                            assignedRolesIds.remove(item.getModelObject().getId());
                        }
                    }
                }).add(new OnChangeAjaxBehavior() {

                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        // nothing to refresh
                    }
                }));
            }
        });
        add(new Link<User>("save", model) {
            @Override
            public void onClick() {
                userService.assignRoles(getModelObject(), assignedRolesIds);
                getSession().info(getString("usermgmt-plugin.status.saved"));
                goBack();
            }
        });
        add(new Link<Void>("cancel") {
            @Override
            public void onClick() {
                getSession().info(getString("usermgmt-plugin.status.cancelled"));
                goBack();
            }
        });

    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        for (Role role : userRepository.getRoles(getModelObject())) {
            assignedRolesIds.add(role.getId());
        }
    }

    abstract void goBack();
}
