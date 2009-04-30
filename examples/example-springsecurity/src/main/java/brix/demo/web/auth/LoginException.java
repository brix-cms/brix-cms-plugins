package brix.demo.web.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date Apr 29, 2009 10:32:34 PM
 */
public class LoginException extends Exception {

    public LoginException() {
        super();
    }

    public LoginException(Throwable cause) {
        super(cause);
    }

    public LoginException(String message) {
        super(message);
    }

    public LoginException(String message, Throwable cause) {
        super(message, cause);
    }
}
