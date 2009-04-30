package brix.demo.web.auth;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.Authentication;
import org.springframework.security.SpringSecurityException;
import org.springframework.security.AuthenticationManager;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;


/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date Apr 29, 2009 10:19:45 PM
 */
public class LoginPage extends WebPage {
    private static final Logger log = LoggerFactory.getLogger(LoginPage.class);

    private String username;
    private String password;
    @SpringBean
    private AuthenticationManager authenticationManager;

    public LoginPage() {
        add(new FeedbackPanel("feedback"));
        setDefaultModel(new CompoundPropertyModel(this));
        Form form = new Form("login") {
            @Override
            protected void onSubmit() {
                super.onSubmit();
                Authentication authResult = null;
                try {
                    authResult = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
                }
                catch (Exception e) {
                    log.debug("failed login", e);
                }

                if (authResult != null) {
                    setAuthentication(authResult);
                    if (!getPage().continueToOriginalDestination()) {
                        setResponsePage(getApplication().getHomePage());
                    }                    
                } else {
                    error(getLocalizer().getString("exception.login", this));
                }
            }
        };
        form.add(new TextField("username"));
        form.add(new PasswordTextField("password"));
        form.add(new Button("submit"));
        add(form);
    }

    private void setAuthentication(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
