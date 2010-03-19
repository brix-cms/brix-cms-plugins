package brix.plugin.hierarchical;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import brix.Brix;
import brix.BrixNodeModel;
import brix.Plugin;
import brix.auth.Action;
import brix.auth.Action.Context;
import brix.jcr.JcrNodeWrapperFactory;
import brix.jcr.RepositoryInitializer;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.hierarchical.admin.HierarchicalNodeManagerPanel;
import brix.plugin.hierarchical.admin.NodeEditorPlugin;
import brix.plugin.hierarchical.admin.NodeTreeParentComponent;
import brix.plugin.hierarchical.nodes.SimpleFolderNode;
import brix.plugin.hierarchical.nodes.TitledNode;
import brix.plugin.site.admin.NodeTreeContainer;
import brix.plugin.site.auth.SiteNodeAction;
import brix.plugin.site.auth.SiteNodeAction.Type;
import brix.registry.ExtensionPoint;
import brix.registry.ExtensionPointRegistry;
import brix.web.tab.AbstractWorkspaceTab;
import brix.web.tab.IBrixTab;
import brix.workspace.Workspace;

@SuppressWarnings("deprecation")
public abstract class HierarchicalNodePlugin implements Plugin
{
	public static final String NAMESPACE = "brixhierarchicalnode";
	public static final String NS_PREFIX = NAMESPACE + ":";

	private final Brix brix;

	public HierarchicalNodePlugin(Brix brix)
	{
		this.brix = brix;
		ExtensionPointRegistry registry = brix.getConfig().getRegistry();
		registry.register(RepositoryInitializer.POINT, new HierarchicalRepoInitializer());
		registry.register(JcrNodeWrapperFactory.POINT, TitledNode.FACTORY);
	}

	protected abstract IModel<String> getTabName();

	protected abstract String getRootNodeName();

	protected abstract HierarchicalPluginLocator getPluginLocator();

	protected abstract ExtensionPoint<? extends NodeEditorPlugin> getNodeEditorPluginExtensionPoint();

	protected int getTabPriority()
	{
		return 0;
	}

	public BrixNode getRootNode(String workspaceID)
	{
		JcrSession workspaceSession = getBrix().getCurrentSession(workspaceID);
		return (BrixNode)workspaceSession.getItem(getRootNodePath());
	}

	public NodeEditorPlugin getNodeEditorPluginForType(String nodeType)
	{
		for (NodeEditorPlugin plugin : getNodeEditorPlugins())
		{
			if (plugin.getNodeType().equals(nodeType))
			{
				return plugin;
			}
		}
		return null;
	}

	public Collection<? extends NodeEditorPlugin> getNodeEditorPlugins()
	{
		return brix.getConfig().getRegistry().lookupCollection(getNodeEditorPluginExtensionPoint());
	}

	public String getRootNodePath()
	{
		return brix.getRootPath() + "/" + getRootNodeName();
	}

	public Brix getBrix()
	{
		return brix;
	}

	public void selectNode(Component component, BrixNode node, boolean refreshTree)
	{
		NodeTreeParentComponent panel = findContainer(component);
		if (panel != null)
		{
			panel.selectNode(node);
			panel.updateTree();
		}
		else
		{
			throw new IllegalStateException(
					"Can't call selectNode with component outside of the hierarchy.");
		}
	}

	private NodeTreeParentComponent findContainer(Component component)
	{
		if (component instanceof NodeTreeParentComponent)
		{
			return (NodeTreeParentComponent)component;
		}
		else
		{
			return component.findParent(NodeTreeParentComponent.class);
		}
	}

	public boolean canAddNodeChild(BrixNode node, Context context)
	{
		if (!isNodeEditable(node))
		{
			return false;
		}
		Action action = new SiteNodeAction(context, Type.NODE_ADD_CHILD, node);
		return brix.getAuthorizationStrategy().isActionAuthorized(action);
	}

	private boolean isNodeEditable(BrixNode node)
	{
		if (node.isNodeType("mix:versionable") && !node.isCheckedOut())
		{
			return false;
		}
		if (node.isLocked() && node.getLock().getLockToken() == null)
		{
			return false;
		}
		return true;
	}

	/* Implemented (Plugin) interface methods */
	public List<Workspace> getWorkspaces(Workspace currentWorkspace, boolean isFrontend)
	{
		return null;
	}

	public void initWorkspace(Workspace workspace, JcrSession workspaceSession)
	{
		JcrNode root = (JcrNode)workspaceSession.getItem(brix.getRootPath());
		JcrNode pluginRoot = null;
		if (root.hasNode(getRootNodeName()))
		{
			pluginRoot = root.getNode(getRootNodeName());
		}
		else
		{
			pluginRoot = root.addNode(getRootNodeName(), SimpleFolderNode.JCR_PRIMARY_TYPE);
		}

		if (pluginRoot != null)
		{
			if (!pluginRoot.isNodeType(BrixNode.JCR_TYPE_BRIX_NODE))
			{
				pluginRoot.addMixin(BrixNode.JCR_TYPE_BRIX_NODE);
			}
		}
	}

	public boolean isPluginWorkspace(Workspace workspace)
	{
		return false;
	}

	public List<IBrixTab> newTabs(IModel<Workspace> workspaceModel)
	{
		IBrixTab tab = new NodeTreeEditorTab(getTabName(), workspaceModel, getPluginLocator(),
				getTabPriority());
		return Collections.singletonList(tab);
	}

	/* end of implemented (Plugin) interface methods */

	static class NodeTreeEditorTab extends AbstractWorkspaceTab
	{
		private final HierarchicalPluginLocator pluginLocator;

		public NodeTreeEditorTab(IModel<String> title, IModel<Workspace> workspaceModel,
				HierarchicalPluginLocator pluginLocator, int priority)
		{
			super(title, workspaceModel, priority);
			this.pluginLocator = pluginLocator;
		}

		@Override
		public Panel newPanel(String panelId, IModel<Workspace> workspaceModel)
		{
			BrixNode rootNode = pluginLocator.getPlugin().getRootNode(
					workspaceModel.getObject().getId());
			return new HierarchicalNodeManagerPanel(panelId, workspaceModel, new BrixNodeModel(
					rootNode), pluginLocator);
		}
	}

}
