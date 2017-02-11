package org.brixcms.plugin.hierarchical.auth;

import org.brixcms.auth.Action;
import org.brixcms.plugin.hierarchical.HierarchicalPluginLocator;
import org.brixcms.workspace.Workspace;

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

/**
 * @author dan.simko@gmail.com
 */
public class AccessHierarchicalNodePluginAction implements Action {
    private final HierarchicalPluginLocator pluginLocator;
    private final Workspace workspace;

    public AccessHierarchicalNodePluginAction(HierarchicalPluginLocator pluginLocator, Workspace workspace) {
        this.workspace = workspace;
        this.pluginLocator = pluginLocator;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public HierarchicalPluginLocator getPluginLocator() {
        return pluginLocator;
    }

    @Override
    public String toString() {
        return "AccessHierarchicalNodePluginAction{" + "workspace=" + workspace + '}';
    }

    @Override
    public Context getContext() {
        return Context.ADMINISTRATION;
    }

}
