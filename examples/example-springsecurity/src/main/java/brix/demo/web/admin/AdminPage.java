package brix.demo.web.admin;

import org.brixcms.web.admin.AdminPanel;
import org.apache.wicket.markup.html.WebPage;

/**
 * This page hosts Brix's {@link AdminPanel}
 *
 * @author igor.vaynberg
 */
public class AdminPage extends WebPage {
// --------------------------- CONSTRUCTORS ---------------------------

    /**
     * Constructor
     */
    public AdminPage() {
        add(new AdminPanel("admin", null));
    }
}
