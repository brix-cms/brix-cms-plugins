package org.brixcms.plugin.demo.web.signin;

import org.apache.wicket.authroles.authentication.pages.SignOutPage;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class BrixSignOutPage extends SignOutPage{

    public BrixSignOutPage() {
        super();
        setResponsePage(getApplication().getHomePage());
    }
}
