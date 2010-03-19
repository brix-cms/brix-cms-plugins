package brix.plugin.hierarchical.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import brix.jcr.wrapper.BrixNode;
import brix.plugin.hierarchical.HierarchicalPluginLocator;

public class NodeEditorPluginEntriesModel extends LoadableDetachableModel<List<NodeEditorPluginEntry>>
{

	private final HierarchicalPluginLocator pluginLocator;
	private final IModel<BrixNode> parentNode;
	
	public NodeEditorPluginEntriesModel(HierarchicalPluginLocator pluginLocator, IModel<BrixNode> parentNode)
	{
		this.parentNode = parentNode;
		this.pluginLocator = pluginLocator;
	}

	@Override
	protected List<NodeEditorPluginEntry> load()
	{
		return convert(pluginLocator.getPlugin().getNodeEditorPlugins());
	}

	private List<NodeEditorPluginEntry> convert(Collection<? extends NodeEditorPlugin> nodeEditorPlugins)
	{
		List<NodeEditorPluginEntry> list = new ArrayList<NodeEditorPluginEntry>();
        for (NodeEditorPlugin plugin : nodeEditorPlugins)
        {
            if (plugin.newCreateNodeCaptionModel(parentNode) != null)
            {
                list.add(new NodeEditorPluginEntry(plugin, pluginLocator));
            }
        }
		return list;
	}


}
