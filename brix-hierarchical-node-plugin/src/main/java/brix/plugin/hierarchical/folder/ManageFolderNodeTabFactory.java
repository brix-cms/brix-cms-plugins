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


	public static List<IBrixTab> getTabs(final IModel<BrixNode> folderModel, final HierarchicalPluginLocator pluginLocator)
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
				return pluginLocator.getPlugin().canViewNodeChildren(folderModel.getObject(), Context.ADMINISTRATION);
			}

		};
		return Collections.singletonList(listingTab);
	}


}
