
package brix.plugins.springsecurity;

import brix.auth.Action;
import brix.auth.AuthorizationStrategy;
import brix.auth.ViewWorkspaceAction;
import brix.jcr.api.JcrProperty;
import brix.jcr.api.JcrValue;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.prototype.auth.CreatePrototypeAction;
import brix.plugin.prototype.auth.DeletePrototypeAction;
import brix.plugin.prototype.auth.RestorePrototypeAction;
import brix.plugin.publishing.auth.PublishWorkspaceAction;
import brix.plugin.site.auth.ConvertNodeAction;
import brix.plugin.site.auth.SelectNewNodeTypeAction;
import brix.plugin.site.auth.SiteNodeAction;
import brix.plugin.snapshot.auth.CreateSnapshotAction;
import brix.plugin.snapshot.auth.DeleteSnapshotAction;
import brix.plugin.snapshot.auth.RestoreSnapshotAction;
import brix.plugin.webdavurl.AccessWebDavUrlPluginAction;
import brix.web.nodepage.toolbar.AccessWorkspaceSwitcherToolbarAction;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.protocol.http.WebResponse;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.jcr.ValueFormatException;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Delegating AuthorizationStrategy that looks at the type of message and dispatches to a more specific handler.
 *
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date Mar 17, 2009 12:57:02 PM
 */
public class AuthorizationStrategyImpl implements AuthorizationStrategy {
// ------------------------------ FIELDS ------------------------------

    private Map<Class, Method> methodHashMap = new ConcurrentHashMap<Class, Method>();

    private AccessDecisionManager accessDecisionManager;

// --------------------------- CONSTRUCTORS ---------------------------

    public AuthorizationStrategyImpl() {
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setAccessDecisionManager(AccessDecisionManager accessDecisionManager) {
        this.accessDecisionManager = accessDecisionManager;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface AuthorizationStrategy ---------------------

    public boolean isActionAuthorized(Action action) {
        if (userHasAuthority(action, Arrays.asList((ConfigAttribute)new ConfigAttributeImpl(AuthConstants.MEMBER_SUPERUSER)))) {
            return true;
        }

        Class<? extends Action> clazz = action.getClass();
        Method m = methodHashMap.get(clazz);
        try {
            Class<? extends AuthorizationStrategyImpl> thisClazz = this.getClass();
            while (m == null) {
                try {
                    m = thisClazz.getDeclaredMethod("isActionAuthorized", new Class[]{clazz});
                } catch (NoSuchMethodException e) {
                    if (thisClazz != AuthorizationStrategyImpl.class) {
                        thisClazz = (Class<? extends AuthorizationStrategyImpl>) thisClazz.getSuperclass();
                        continue;
                    } else {
                        throw e;
                    }
                }
                methodHashMap.put(clazz, m);
            }
            return (Boolean) m.invoke(this, action);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

// -------------------------- OTHER METHODS --------------------------

    public boolean isActionAuthorized(SiteNodeAction action) {
        boolean result = true;
        BrixNode node = action.getNode();
        node = UserPlugin.findPermissionsNode(node);

        if (node != null) {
            result = checkPermsForNode(node);

            Response response = RequestCycle.get().getResponse();
            if (response instanceof WebResponse) {
                HttpServletResponse servletResponse = ((WebResponse) response).getHttpServletResponse();
                servletResponse.setHeader("Cache-Control", "no-cache");
            }
//            response.setHeader("Pragma", "no-cache");
//            response.setDateHeader("Expires", 0);
        }
        return result;
    }

    private boolean checkPermsForNode(BrixNode node) {
        JcrProperty propertyWrapper = node.getProperty(UserPlugin.AUTH_GROUP_KEY);
        // need to be careful here because sites that are restored from XML will not get their
        // multivalued properties set correctly if there is only a single value.
        List<ConfigAttribute> attributes = new ArrayList<ConfigAttribute>();
        try {
            for (JcrValue value : propertyWrapper.getValues()) {
                attributes.add(new ConfigAttributeImpl(value.getString()));
            }
        }
        catch (Exception e) {
            if (e instanceof ValueFormatException) {
                attributes.add(new ConfigAttributeImpl(propertyWrapper.getValue().getString()));
            }
        }
        return userHasAuthority(node, attributes);
    }

    public boolean userHasAuthority(Object object, List<ConfigAttribute> attributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                return false;
            }
            accessDecisionManager.decide(authentication, object, attributes);
        } catch (AccessDeniedException e) {
            return false;
        } catch (InsufficientAuthenticationException e) {
            return false;
        }
        return true;
    }

    public boolean isActionAuthorized(AccessWorkspaceSwitcherToolbarAction action) {
        return action.getContext().equals(Action.Context.PRESENTATION) && SecurityContextHolder.getContext().getAuthentication() != null;
    }

    public boolean isActionAuthorized(ViewWorkspaceAction action) {
        return true;
    }

    public boolean isActionAuthorized(ConvertNodeAction action) {
        return true;
    }

    public boolean isActionAuthorized(SelectNewNodeTypeAction action) {
        return true;
    }

    public boolean isActionAuthorized(RestorePrototypeAction action) {
        return true;
    }

    public boolean isActionAuthorized(PublishWorkspaceAction action) {
        return true;
    }

    public boolean isActionAuthorized(AccessWebDavUrlPluginAction action) {
        return true;
    }

    public boolean isActionAuthorized(CreateSnapshotAction action) {
        return true;
    }

    public boolean isActionAuthorized(DeletePrototypeAction action) {
        return true;
    }

    public boolean isActionAuthorized(RestoreSnapshotAction action) {
        return true;
    }

    public boolean isActionAuthorized(CreatePrototypeAction action) {
        return true;
    }

    public boolean isActionAuthorized(DeleteSnapshotAction action) {
        return true;
    }

// -------------------------- INNER CLASSES --------------------------

    // .. add rest of your handlers here
    public static class ConfigAttributeImpl implements ConfigAttribute {
        private String attribute;

        public ConfigAttributeImpl(String attribute) {
            this.attribute = attribute;
        }

        public String getAttribute() {
            return attribute;
        }
    }
}
