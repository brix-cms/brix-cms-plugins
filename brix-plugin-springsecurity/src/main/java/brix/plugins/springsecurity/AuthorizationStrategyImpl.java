/*
 * THIS PROGRAM IS CONFIDENTIAL AND PROPRIETARY TO BRIAN TOPPING AND MAY NOT BE REPRODUCED,
 * PUBLISHED OR DISCLOSED TO OTHERS WITHOUT WRITTEN AUTHORIZATION.
 *
 * COPYRIGHT © 2009, BRIAN TOPPING. THIS WORK IS UNPUBLISHED.
 */

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
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.util.AuthorityUtils;

import javax.jcr.ValueFormatException;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
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

// --------------------------- CONSTRUCTORS ---------------------------

    public AuthorizationStrategyImpl() {
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface AuthorizationStrategy ---------------------

    public boolean isActionAuthorized(Action action) {
        if (AuthorityUtils.userHasAuthority(AuthConstants.MEMBER_SUPERUSER)) {
            return true;
        }

        Class<? extends Action> clazz = action.getClass();
        Method m = methodHashMap.get(clazz);
        try {
            if (m == null) {
                m = this.getClass().getDeclaredMethod("isActionAuthorized", new Class[]{clazz});
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
        boolean result = false;
        JcrProperty propertyWrapper = node.getProperty(UserPlugin.AUTH_GROUP_KEY);
        // need to be careful here because sites that are restored from XML will not get their
        // multivalued properties set correctly if there is only a single value.
        try {
            for (JcrValue value : propertyWrapper.getValues()) {
                if (result = AuthorityUtils.userHasAuthority(value.getString())) {
                    break;
                }
            }
        }
        catch (Exception e) {
            if (e instanceof ValueFormatException) {
                result = AuthorityUtils.userHasAuthority(propertyWrapper.getValue().getString());
            }
        }
        return result;
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

    // .. add rest of your handlers here
}
