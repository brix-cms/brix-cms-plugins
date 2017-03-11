package org.brixcms.plugin.usermgmt.role.admin;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.brixcms.auth.Action.Context;
import org.brixcms.plugin.usermgmt.role.Role;
import org.brixcms.plugin.usermgmt.role.RolePlugin;
import org.brixcms.web.generic.BrixGenericPanel;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
class ViewTab extends BrixGenericPanel<Role> {
    public ViewTab(String id, IModel<Role> model) {
        super(id, new CompoundPropertyModel<>(model));
        add(new Label("name"));
        add(new Link<Void>("edit") {
            @Override
            public void onClick() {
                EditTab edit = new EditTab(ViewTab.this.getId(), ViewTab.this.getModel()) {
                    @Override
                    void goBack() {
                        replaceWith(ViewTab.this);
                    }
                };
                ViewTab.this.replaceWith(edit);
            }

            @Override
            public boolean isVisible() {
                Role entity = ViewTab.this.getModelObject();
                return RolePlugin.get().canEditEntity(entity, Context.ADMINISTRATION);
            }
        });
    }

}
