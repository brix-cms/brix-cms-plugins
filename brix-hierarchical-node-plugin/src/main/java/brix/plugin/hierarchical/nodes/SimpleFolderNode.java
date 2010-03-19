package brix.plugin.hierarchical.nodes;

import javax.jcr.Node;

import brix.jcr.api.JcrSession;

public class SimpleFolderNode extends TitledNode
{
	
	public static final String JCR_PRIMARY_TYPE = "nt:folder";

	public SimpleFolderNode(Node delegate, JcrSession session)
	{
		super(delegate, session);
	}

}
