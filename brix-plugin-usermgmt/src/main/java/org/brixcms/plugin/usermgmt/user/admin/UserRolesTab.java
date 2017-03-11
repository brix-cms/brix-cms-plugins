package org.brixcms.plugin.usermgmt.user.admin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.plugin.jpa.web.admin.EntityModel;
import org.brixcms.plugin.usermgmt.role.Role;
import org.brixcms.plugin.usermgmt.user.User;
import org.brixcms.plugin.usermgmt.user.UserPlugin;
import org.brixcms.plugin.usermgmt.user.UserRepository;
import org.brixcms.web.generic.BrixGenericPanel;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
class UserRolesTab extends BrixGenericPanel<User> {

    @SpringBean
    private UserRepository userRepository;

    public UserRolesTab(String id, IModel<User> model) {
        super(id, new CompoundPropertyModel<>(model));
        add(new RefreshingView<Role>("roles") {

            @Override
            protected Iterator<IModel<Role>> getItemModels() {
                List<IModel<Role>> models = new ArrayList<>();
                for (Role role : userRepository.getRoles(getModelObject())) {
                    models.add(new EntityModel<Role, Long>(role));
                }
                return models.iterator();
            }

            @Override
            protected void populateItem(Item<Role> item) {
                item.add(new Label("name", new PropertyModel<>(item.getModel(), "name")));
            }

        });
        add(new Link<Void>("edit") {
            @Override
            public void onClick() {
                UserRolesEditTab edit = new UserRolesEditTab(UserRolesTab.this.getId(), UserRolesTab.this.getModel()) {
                    @Override
                    void goBack() {
                        replaceWith(UserRolesTab.this);
                    }
                };
                UserRolesTab.this.replaceWith(edit);
            }

            @Override
            public boolean isVisible() {
                User entity = UserRolesTab.this.getModelObject();
                return UserPlugin.get().canEditUserRoles(entity);
            }
        });
    }

}
