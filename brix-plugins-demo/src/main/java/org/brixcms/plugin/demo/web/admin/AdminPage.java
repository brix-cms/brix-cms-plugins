package org.brixcms.plugin.demo.web.admin;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.brixcms.plugin.demo.web.signin.BrixSignOutPage;
import org.brixcms.web.admin.AdminPanel;

/**
 * This page hosts Brix's {@link AdminPanel}
 *
 * @author igor.vaynberg
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class AdminPage extends WebPage {
    /**
     * Constructor
     */
    public AdminPage() {
        add(new BookmarkablePageLink<>("logout", BrixSignOutPage.class));
        add(new AdminPanel("admin", null));
    }
}
