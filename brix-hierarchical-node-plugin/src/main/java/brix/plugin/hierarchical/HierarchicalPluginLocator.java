package brix.plugin.hierarchical;

import java.io.Serializable;

import brix.plugin.hierarchical.admin.NodeEditorPlugin;

public interface HierarchicalPluginLocator extends Serializable
{
	
	HierarchicalNodePlugin getPlugin();

}
