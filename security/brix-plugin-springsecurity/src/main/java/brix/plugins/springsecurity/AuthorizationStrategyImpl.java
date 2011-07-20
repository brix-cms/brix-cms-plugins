
package brix.plugins.springsecurity;

import org.brixcms.auth.Action;
import org.brixcms.auth.AuthorizationStrategy;
import org.brixcms.auth.ViewWorkspaceAction;
import org.brixcms.jcr.api.JcrProperty;
import org.brixcms.jcr.api.JcrValue;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.prototype.auth.CreatePrototypeAction;
import org.brixcms.plugin.prototype.auth.DeletePrototypeAction;
import org.brixcms.plugin.prototype.auth.RestorePrototypeAction;
import org.brixcms.plugin.publishing.auth.PublishWorkspaceAction;
import org.brixcms.plugin.site.auth.ConvertNodeAction;
import org.brixcms.plugin.site.auth.SelectNewNodeTypeAction;
import org.brixcms.plugin.snapshot.auth.CreateSnapshotAction;
import org.brixcms.plugin.snapshot.auth.DeleteSnapshotAction;
import org.brixcms.plugin.snapshot.auth.RestoreSnapshotAction;
import org.brixcms.plugin.webdavurl.AccessWebDavUrlPluginAction;
import org.brixcms.web.nodepage.toolbar.AccessWorkspaceSwitcherToolbarAction;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.jcr.ValueFormatException;
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
