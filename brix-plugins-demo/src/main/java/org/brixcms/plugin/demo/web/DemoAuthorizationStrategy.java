package org.brixcms.plugin.demo.web;

import org.brixcms.auth.Action;
import org.brixcms.auth.Action.Context;
import org.brixcms.auth.AuthorizationStrategy;
import org.brixcms.auth.ViewWorkspaceAction;
import org.brixcms.plugin.content.auth.PostNodeAction;
import org.brixcms.plugin.content.auth.PostNodeAction.Type;
import org.brixcms.plugin.hierarchical.auth.AccessHierarchicalNodePluginAction;
import org.brixcms.plugin.jpa.auth.AccessJpaPluginAction;
import org.brixcms.plugin.menu.auth.AccessMenuPluginAction;
import org.brixcms.plugin.prototype.auth.AccessPrototypePluginAction;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.plugin.site.auth.AccessSitePluginAction;
import org.brixcms.plugin.snapshot.auth.AccessSnapshotPluginAction;
import org.brixcms.plugin.usermgmt.user.User;
import org.brixcms.plugin.webdavurl.AccessWebDavUrlPluginAction;

/**
 * Implementation of {@link AuthorizationStrategy} that allows everything for
 * the purposes of the demo
 *
 * @author dan.simko@gmail.com
 */
public class DemoAuthorizationStrategy implements AuthorizationStrategy {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isActionAuthorized(Action action) {
        // TODO refactor
        User user = AuthenticatedSession.get().getUser();
        boolean isAdmin = user != null && user.getUsername().equals("admin");
        if (action instanceof AccessSitePluginAction) {
            AccessSitePluginAction pluginAction = (AccessSitePluginAction) action;
            return isAdmin && SitePlugin.get().isSiteWorkspace(pluginAction.getWorkspace());
        }
        if (action instanceof AccessMenuPluginAction) {
            AccessMenuPluginAction pluginAction = (AccessMenuPluginAction) action;
            return isAdmin && SitePlugin.get().isSiteWorkspace(pluginAction.getWorkspace());
        }
        if (action instanceof AccessPrototypePluginAction || action instanceof AccessSnapshotPluginAction
                || action instanceof AccessWebDavUrlPluginAction || action instanceof AccessJpaPluginAction) {
            return isAdmin;
        }
        if (action instanceof AccessHierarchicalNodePluginAction) {
            AccessHierarchicalNodePluginAction pluginAction = (AccessHierarchicalNodePluginAction) action;
            return pluginAction.getPluginLocator().getPlugin().isPluginWorkspace(pluginAction.getWorkspace());
        }
        if (action instanceof PostNodeAction) {
            PostNodeAction postNodeAction = (PostNodeAction) action;
            if (postNodeAction.getType() == Type.VIEW && postNodeAction.getContext() == Context.PRESENTATION) {
                // TODO isPublic, isPublished?
            }
        }
        if (action instanceof ViewWorkspaceAction) {
            ViewWorkspaceAction workspaceAction = (ViewWorkspaceAction) action;
            return isAdmin || !SitePlugin.get().isSiteWorkspace(workspaceAction.getWorkspace());
        }
        return true;
    }
}
