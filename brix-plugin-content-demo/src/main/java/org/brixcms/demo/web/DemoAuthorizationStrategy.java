/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.brixcms.demo.web;

import org.brixcms.auth.Action;
import org.brixcms.auth.Action.Context;
import org.brixcms.auth.AuthorizationStrategy;
import org.brixcms.plugin.content.auth.PostNodeAction;
import org.brixcms.plugin.content.auth.PostNodeAction.Type;
import org.brixcms.plugin.hierarchical.auth.AccessHierarchicalNodePluginAction;
import org.brixcms.plugin.menu.auth.AccessMenuPluginAction;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.plugin.site.auth.AccessSitePluginAction;

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
        if (action instanceof AccessSitePluginAction) {
            AccessSitePluginAction pluginAction = (AccessSitePluginAction) action;
            return SitePlugin.get().isSiteWorkspace(pluginAction.getWorkspace());
        }
        if (action instanceof AccessMenuPluginAction) {
            AccessMenuPluginAction pluginAction = (AccessMenuPluginAction) action;
            return SitePlugin.get().isSiteWorkspace(pluginAction.getWorkspace());
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
        return true;
    }
}
