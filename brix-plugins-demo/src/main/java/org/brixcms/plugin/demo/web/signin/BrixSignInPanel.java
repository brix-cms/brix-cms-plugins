package org.brixcms.plugin.demo.web.signin;

import org.apache.wicket.authroles.authentication.panel.SignInPanel;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.brixcms.web.admin.res.AdminPanelResources;

@SuppressWarnings("serial")
public class BrixSignInPanel extends SignInPanel {

    private static final ResourceReference CSS = new CssResourceReference(BrixSignInPanel.class, "signin.css");

    public BrixSignInPanel(String id) {
        super(id);
        add(new AdminPanelResources());
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(CSS));
    }
}
