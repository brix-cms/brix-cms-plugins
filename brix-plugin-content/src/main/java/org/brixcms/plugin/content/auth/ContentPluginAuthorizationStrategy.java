package org.brixcms.plugin.content.auth;

import org.brixcms.auth.Action;
import org.brixcms.auth.Action.Context;
import org.brixcms.auth.AuthorizationStrategy;
import org.brixcms.plugin.content.auth.PostNodeAction.Type;
import org.brixcms.plugin.hierarchical.auth.AccessHierarchicalNodePluginAction;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.plugin.site.auth.AccessSitePluginAction;

public class ContentPluginAuthorizationStrategy implements AuthorizationStrategy {

    @Override
    public boolean isActionAuthorized(Action action) {
        if (action instanceof AccessSitePluginAction) {
            // hide site tab when different than site workspace is selected
            if (!SitePlugin.get().isSiteWorkspace(((AccessSitePluginAction) action).getWorkspace())) {
                return false;
            }
        }
        if (action instanceof AccessHierarchicalNodePluginAction) {
            AccessHierarchicalNodePluginAction pluginAction = (AccessHierarchicalNodePluginAction) action;
            // hide content tab when different than content ws is selected
            if (!pluginAction.getPluginLocator().getPlugin().isPluginWorkspace(pluginAction.getWorkspace())) {
                return false;
            }
        }
        if (action instanceof PostNodeAction) {
            PostNodeAction postNodeAction = (PostNodeAction) action;
            if (postNodeAction.getType() == Type.VIEW && postNodeAction.getContext() == Context.PRESENTATION) {
                // TODO isPublic, isPublished?
            }
        }
        return true;
    }

}
