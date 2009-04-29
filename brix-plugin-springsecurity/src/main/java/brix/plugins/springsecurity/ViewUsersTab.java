package brix.plugins.springsecurity;

import brix.auth.Action;
import brix.jcr.api.JcrValue;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.SitePlugin;
import brix.web.generic.BrixGenericPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.security.GrantedAuthority;


/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date Mar 11, 2009 2:20:22 PM
 */
public class ViewUsersTab extends BrixGenericPanel<BrixNode> {
// ------------------------------ FIELDS ------------------------------

    @SpringBean
    private BrixUserProvider brixUserProvider;

// --------------------------- CONSTRUCTORS ---------------------------

    public ViewUsersTab(String panelId, final IModel<BrixNode> nodeModel) {
        super(panelId, nodeModel);

        add(new Link<Void>("edit") {
            @Override
            public void onClick() {
                EditAccessPanel panel = new EditAccessPanel(ViewUsersTab.this.getId(), ViewUsersTab.this.getModel()) {
                    @Override
                    void goBack() {
                        replaceWith(ViewUsersTab.this);
                    }
                };
                ViewUsersTab.this.replaceWith(panel);
            }

            @Override
            public boolean isVisible() {
                return SitePlugin.get().canEditNode(ViewUsersTab.this.getModel().getObject(), Action.Context.ADMINISTRATION);
            }
        });

        add(new Label("requiredGroup", new Model<String>() {
            @Override
            public String getObject() {
                BrixNode node = nodeModel.getObject();
                return getPermissionsString(node);
            }
        }).setEscapeModelStrings(false));

        add(new Label("inherited", new Model<String>() {
            @Override
            public String getObject() {
                BrixNode node = nodeModel.getObject();
                return node.hasProperty(UserPlugin.AUTH_GROUP_KEY) ? "False" : "True";
            }
        }));
    }

// -------------------------- OTHER METHODS --------------------------

    private String getPermissionsString(BrixNode node) {
        String result = "None";
        node = UserPlugin.findPermissionsNode(node);
        if (node != null) {
            result = "";
            for (JcrValue jcrValue : node.getProperty(UserPlugin.AUTH_GROUP_KEY).getValues()) {
                String id = jcrValue.getString();
                GrantedAuthority module = brixUserProvider.getGrantedAuthorityByID(id);
                result += module.getAuthority() + "<br/>";
            }
        }
        return result;
    }
}
