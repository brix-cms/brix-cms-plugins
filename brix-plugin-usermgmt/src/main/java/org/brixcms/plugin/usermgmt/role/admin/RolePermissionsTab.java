package org.brixcms.plugin.usermgmt.role.admin;

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
import org.brixcms.plugin.usermgmt.role.Permission;
import org.brixcms.plugin.usermgmt.role.Role;
import org.brixcms.plugin.usermgmt.role.RoleRepository;
import org.brixcms.plugin.usermgmt.user.UserPlugin;
import org.brixcms.web.generic.BrixGenericPanel;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
class RolePermissionsTab extends BrixGenericPanel<Role> {

    @SpringBean
    private RoleRepository roleRepository;

    public RolePermissionsTab(String id, IModel<Role> model) {
        super(id, new CompoundPropertyModel<>(model));
        add(new RefreshingView<Permission>("permissions") {

            @Override
            protected Iterator<IModel<Permission>> getItemModels() {
                List<IModel<Permission>> models = new ArrayList<>();
                for (Permission permission : roleRepository.getPermissions(getModelObject())) {
                    models.add(new EntityModel<Permission, Long>(permission));
                }
                return models.iterator();
            }

            @Override
            protected void populateItem(Item<Permission> item) {
                item.add(new Label("name", new PropertyModel<>(item.getModel(), "name")));
            }

        });
        add(new Link<Void>("edit") {
            @Override
            public void onClick() {
                RolePermissionsEditTab edit = new RolePermissionsEditTab(RolePermissionsTab.this.getId(),
                        RolePermissionsTab.this.getModel()) {
                    @Override
                    void goBack() {
                        replaceWith(RolePermissionsTab.this);
                    }
                };
                RolePermissionsTab.this.replaceWith(edit);
            }

            @Override
            public boolean isVisible() {
                Role entity = RolePermissionsTab.this.getModelObject();
                return UserPlugin.get().canEditRolePermissions(entity);
            }
        });
    }

}
