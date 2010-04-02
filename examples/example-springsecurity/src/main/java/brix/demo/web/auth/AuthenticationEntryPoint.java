package brix.demo.web.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date Apr 2, 2010 11:26:15 AM
 */
public class AuthenticationEntryPoint implements org.springframework.security.web.AuthenticationEntryPoint {
    private static Logger log = LoggerFactory.getLogger(AuthenticationEntryPoint.class);
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.sendRedirect("/login");
    }
}