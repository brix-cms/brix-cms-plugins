package org.brixcms.plugin.usermgmt.user.admin;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.brixcms.auth.Action.Context;
import org.brixcms.plugin.usermgmt.user.User;
import org.brixcms.plugin.usermgmt.user.UserPlugin;
import org.brixcms.web.generic.BrixGenericPanel;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
class ViewTab extends BrixGenericPanel<User> {
    public ViewTab(String id, IModel<User> model) {
        super(id, new CompoundPropertyModel<>(model));
        add(new Label("username"));
        add(new Label("firstName"));
        add(new Label("lastName"));
        add(new Label("email"));
        add(new Label("verified"));
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
                User entity = ViewTab.this.getModelObject();
                return UserPlugin.get().canEditEntity(entity, Context.ADMINISTRATION);
            }
        });
    }

}
