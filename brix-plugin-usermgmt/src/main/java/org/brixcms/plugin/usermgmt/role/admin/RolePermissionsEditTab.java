package org.brixcms.plugin.usermgmt.role.admin;

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
import org.brixcms.plugin.usermgmt.role.Permission;
import org.brixcms.plugin.usermgmt.role.PermissionRepository;
import org.brixcms.plugin.usermgmt.role.Role;
import org.brixcms.plugin.usermgmt.role.RoleRepository;
import org.brixcms.plugin.usermgmt.role.service.RoleService;
import org.brixcms.web.generic.BrixGenericPanel;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
abstract class RolePermissionsEditTab extends BrixGenericPanel<Role> {

    @SpringBean
    private PermissionRepository permissionRepository;
    @SpringBean
    private RoleRepository roleRepository;
    @SpringBean
    private RoleService roleService;
    private Set<Long> assignedPermissionsIds = new HashSet<>();

    public RolePermissionsEditTab(String id, IModel<Role> model) {
        super(id, new CompoundPropertyModel<>(model));
        add(new RefreshingView<Permission>("permissions") {

            @Override
            protected Iterator<IModel<Permission>> getItemModels() {
                List<IModel<Permission>> models = new ArrayList<>();
                for (Permission permission : permissionRepository.findAll()) {
                    models.add(new EntityModel<Permission, Long>(permission));
                }
                return models.iterator();
            }

            @Override
            protected void populateItem(Item<Permission> item) {
                item.add(new Label("name", new PropertyModel<>(item.getModel(), "name")));
                item.add(new CheckBox("checkbox", new IModel<Boolean>() {
                    @Override
                    public Boolean getObject() {
                        return assignedPermissionsIds.contains(item.getModelObject().getId());
                    }

                    @Override
                    public void setObject(Boolean bool) {
                        if (bool) {
                            assignedPermissionsIds.add(item.getModelObject().getId());
                        } else {
                            assignedPermissionsIds.remove(item.getModelObject().getId());
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
        add(new Link<Role>("save", model) {
            @Override
            public void onClick() {
                roleService.assignPermissions(getModelObject(), assignedPermissionsIds);
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
        for (Permission permission : roleRepository.getPermissions(getModelObject())) {
            assignedPermissionsIds.add(permission.getId());
        }
    }

    abstract void goBack();
}
