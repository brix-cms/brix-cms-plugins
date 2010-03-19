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

package brix.plugin.hierarchical.folder;

import java.util.Collections;
import java.util.List;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import brix.auth.Action.Context;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.hierarchical.HierarchicalPluginLocator;
import brix.plugin.site.ManageNodeTabFactory;
import brix.web.tab.CachingAbstractTab;
import brix.web.tab.IBrixTab;

public class ManageFolderNodeTabFactory implements ManageNodeTabFactory
{

	private final HierarchicalPluginLocator pluginLocator;

	public ManageFolderNodeTabFactory(HierarchicalPluginLocator pluginLocator)
	{
		this.pluginLocator = pluginLocator;
	}

	public List<IBrixTab> getManageNodeTabs(IModel<BrixNode> nodeModel)
	{
		if (nodeModel.getObject().isFolder())
		{
			return getTabs(nodeModel, pluginLocator);
		}
		else
		{
			return null;
		}
	}


	public static List<IBrixTab> getTabs(final IModel<BrixNode> folderModel,
			final HierarchicalPluginLocator pluginLocator)
	{
		IBrixTab listingTab = new CachingAbstractTab(new ResourceModel("listing", "Listing"), 100)
		{

			@Override
			public Panel newPanel(String panelId)
			{
				return new ListFolderNodesTab(panelId, folderModel, pluginLocator);
			}

			@Override
			public boolean isVisible()
			{
				return pluginLocator.getPlugin().canViewNodeChildren(folderModel.getObject(),
						Context.ADMINISTRATION);
			}

		};
		return Collections.singletonList(listingTab);
	}


}
