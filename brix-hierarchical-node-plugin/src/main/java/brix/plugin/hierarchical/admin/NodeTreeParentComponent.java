package brix.plugin.hierarchical.admin;

import brix.jcr.wrapper.BrixNode;

public interface NodeTreeParentComponent
{

    /**
     * Called when the tree selection needs to be changed to the specified node
     * 
     * @param node
     */
    public abstract void selectNode(BrixNode node);

    /**
     * Called when the tree needs to be updated - eg a new node has been inserted
     */
    public abstract void updateTree();
}
