package brix.plugin.hierarchical;

import java.io.Serializable;

public interface HierarchicalPluginLocator extends Serializable
{
	
	HierarchicalNodePlugin getPlugin();

}
