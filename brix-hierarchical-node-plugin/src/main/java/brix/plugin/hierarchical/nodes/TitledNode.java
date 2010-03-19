package brix.plugin.hierarchical.nodes;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import brix.Brix;
import brix.jcr.JcrNodeWrapperFactory;
import brix.jcr.RepositoryUtil;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.hierarchical.HierarchicalNodePlugin;

public class TitledNode extends BrixNode
{
	public static final String NODE_TYPE = HierarchicalNodePlugin.NS_PREFIX + "titled";

	public static JcrNodeWrapperFactory FACTORY = new JcrNodeWrapperFactory() {

		@Override
		public boolean canWrap(Brix brix, JcrNode node) {
			return NODE_TYPE.equals(getNodeType(node));
		}

		@Override
		public JcrNode wrap(Brix brix, Node node, JcrSession session) {
			return new TitledNode(node, session);
		}

		@Override
		public void initializeRepository(Brix brix, Session session) throws RepositoryException {
			RepositoryUtil.registerNodeType(session.getWorkspace(), NODE_TYPE, false, true, true);
		}
	};

	private static class Properties {
		public static final String TITLE = HierarchicalNodePlugin.NS_PREFIX + "title";
	}

	public TitledNode(Node delegate, JcrSession session)
	{
		super(delegate, session);
	}

	public String getTitle() {
		if (hasProperty(Properties.TITLE)) {
			return getProperty(Properties.TITLE).getString();
		}
		return null;
	}

	public void setTitle(String title) {
		setProperty(Properties.TITLE, title);
	}

}
