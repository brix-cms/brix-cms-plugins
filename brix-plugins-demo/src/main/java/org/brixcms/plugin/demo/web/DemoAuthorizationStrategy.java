package org.brixcms.plugin.demo.web;

import java.util.Set;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.auth.Action;
import org.brixcms.auth.AuthorizationStrategy;
import org.brixcms.auth.ViewWorkspaceAction;
import org.brixcms.plugin.content.ContentPlugin;
import org.brixcms.plugin.content.auth.ContentPluginAuthorizationStrategy;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.plugin.usermgmt.role.PermissionRepository;
import org.brixcms.plugin.usermgmt.user.User;
import org.brixcms.plugin.usermgmt.user.UserRepository;

/**
 * Implementation of {@link AuthorizationStrategy} that allows everything for
 * the purposes of the demo
 *
 * @author dan.simko@gmail.com
 */
public class DemoAuthorizationStrategy extends ContentPluginAuthorizationStrategy {

    public static final String ViewSiteWorkspaceAction = "ViewSiteWorkspaceAction";
    public static final String ViewContentWorkspaceAction = "ViewContentWorkspaceAction";

    @SpringBean
    private UserRepository userRepository;

    @SpringBean
    private PermissionRepository permissionRepository;

    public DemoAuthorizationStrategy() {
        Injector.get().inject(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isActionAuthorized(Action action) {
        if (!super.isActionAuthorized(action)) {
            return false;
        }
        String permissionName = action.getClass().getSimpleName();
        if (isManagedPermission(permissionName) || action instanceof ViewWorkspaceAction) {
            User user = AuthenticatedSession.get().getUser();
            if (user == null) {
                return false;
            }
            Set<String> permissions = userRepository.getPermissionsNames(user);
            if (action instanceof ViewWorkspaceAction) {
                ViewWorkspaceAction workspaceAction = (ViewWorkspaceAction) action;
                if (SitePlugin.get().isSiteWorkspace(workspaceAction.getWorkspace())) {
                    return permissions.contains(ViewSiteWorkspaceAction);
                } else if (ContentPlugin.get().isContentWorkspace(workspaceAction.getWorkspace())) {
                    return permissions.contains(ViewContentWorkspaceAction);
                }
            }
            return permissions.contains(permissionName);
        }
        return true;
    }

    private boolean isManagedPermission(String permissionName) {
        return permissionRepository.getAllPermissionsNames().contains(permissionName);
    }
}
