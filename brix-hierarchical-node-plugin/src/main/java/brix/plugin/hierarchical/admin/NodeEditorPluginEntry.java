package brix.plugin.hierarchical.admin;

import java.io.Serializable;

import brix.plugin.hierarchical.HierarchicalPluginLocator;

public class NodeEditorPluginEntry implements Serializable
{

    private final String nodeType;
    private final HierarchicalPluginLocator pluginLocator;

    public NodeEditorPluginEntry(NodeEditorPlugin plugin, HierarchicalPluginLocator pluginLocator)
    {
        this.nodeType = plugin.getNodeType();
        this.pluginLocator = pluginLocator;
    }

	public NodeEditorPlugin getPlugin()
    {
        return pluginLocator.getPlugin().getNodeEditorPluginForType(nodeType);
    }

}
