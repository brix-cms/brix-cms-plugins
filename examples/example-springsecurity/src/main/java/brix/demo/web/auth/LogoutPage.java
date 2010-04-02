package brix.demo.web.auth;

import org.apache.wicket.markup.html.WebPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date Apr 29, 2009 10:42:44 PM
 */
public class LogoutPage extends WebPage {
    private static final Logger log = LoggerFactory.getLogger(LogoutPage.class);

    public LogoutPage() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
