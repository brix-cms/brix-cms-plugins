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

package brix.plugin.hierarchical;

import brix.plugin.hierarchical.admin.HierarchicalNodeManagerPanel;
import brix.plugin.hierarchical.admin.NodeEditorPlugin;
import brix.plugin.hierarchical.admin.NodeTreeParentComponent;
import brix.plugin.hierarchical.folder.ManageFolderNodeTabFactory;
import brix.plugin.hierarchical.nodes.SimpleFolderNode;
import brix.plugin.hierarchical.nodes.TitledNode;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.brixcms.Brix;
import org.brixcms.BrixNodeModel;
import org.brixcms.Plugin;
import org.brixcms.auth.Action;
import org.brixcms.auth.Action.Context;
import org.brixcms.jcr.JcrNodeWrapperFactory;
import org.brixcms.jcr.RepositoryInitializer;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.ManageNodeTabFactory;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.plugin.site.auth.SiteNodeAction;
import org.brixcms.plugin.site.auth.SiteNodeAction.Type;
import org.brixcms.registry.ExtensionPoint;
import org.brixcms.registry.ExtensionPointRegistry;
import org.brixcms.web.tab.AbstractWorkspaceTab;
import org.brixcms.web.tab.IBrixTab;
import org.brixcms.workspace.Workspace;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * The HierarchicalNodePlugin was created as an abstract parent plugin to make
 * it easy to create new plugins that focus on editing hierarchical nodes. It is
 * largely based off of the work of the {@link SitePlugin}. The
 * {@link SitePlugin} had many great features that were desired in other
 * plugins, but it was not built for reuse in other plugins. Therefore, this was
 * created, and hopefully at some point the {@link SitePlugin} can be refactored
 * to use this as a parent.
 * 
 * You will notice that use of this plugin requires certain dependencies on
 * classes belonging to the {@link SitePlugin}. I tried reusing whatever classes
 * could easily be reused. Unfortunately, many of them had to be rewritten
 * because of hard-coded dependencies on the {@link SitePlugin} class itself.
 * So, there is some reuse of interfaces, mainly.
 * 
 * @author Jeremy Thomerson
 */
@SuppressWarnings("deprecation")
public abstract class HierarchicalNodePlugin implements Plugin
{
	public static final String NAMESPACE = "brixhierarchicalnode";
	public static final String NS_PREFIX = NAMESPACE + ":";

	private final Brix brix;

	public HierarchicalNodePlugin(Brix brix)
	{
		this.brix = brix;
	}

	protected void initializeExtensionPoints(Brix brix)
	{
		ExtensionPointRegistry registry = brix.getConfig().getRegistry();
		registry.register(RepositoryInitializer.POINT, new HierarchicalRepoInitializer());
		registry.register(JcrNodeWrapperFactory.POINT, TitledNode.FACTORY);
		registry.register(getManageNodeTabFactoryExtensionPoint(), new ManageFolderNodeTabFactory(
				getPluginLocator()));
	}

	protected abstract IModel<String> getTabName();

	protected abstract String getRootNodeName();

	protected abstract HierarchicalPluginLocator getPluginLocator();

	protected abstract ExtensionPoint<? extends NodeEditorPlugin> getNodeEditorPluginExtensionPoint();

	protected abstract ExtensionPoint<ManageNodeTabFactory> getManageNodeTabFactoryExtensionPoint();

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

	public Collection<? extends ManageNodeTabFactory> getManageNodeTabFactories()
	{
		return brix.getConfig().getRegistry().lookupCollection(
				getManageNodeTabFactoryExtensionPoint());
	}

	public String getRootNodePath()
	{
		return brix.getRootPath() + "/" + getRootNodeName();
	}

	public Brix getBrix()
	{
		return brix;
	}

	public void refreshNavigationTree(Component component)
	{
		NodeTreeParentComponent panel = findContainer(component);
		if (panel != null)
		{
			panel.updateTree();
		}
		else
		{
			throw new IllegalStateException(
					"Can't call refreshNaviagtionTree with component outside of the hierarchy.");
		}
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

	public boolean canViewNode(BrixNode node, Context context)
	{
		Action action = new SiteNodeAction(context, Type.NODE_VIEW, node);
		return brix.getAuthorizationStrategy().isActionAuthorized(action);
	}

	public boolean canViewNodeChildren(BrixNode node, Context context)
	{
		Action action = new SiteNodeAction(context, Type.NODE_VIEW_CHILDREN, node);
		return brix.getAuthorizationStrategy().isActionAuthorized(action);
	}

	public boolean canEditNode(BrixNode node, Context context)
	{
		if (!isNodeEditable(node))
		{
			return false;
		}
		Action action = new SiteNodeAction(context, Type.NODE_EDIT, node);
		return brix.getAuthorizationStrategy().isActionAuthorized(action);
	}

	public boolean canDeleteNode(BrixNode node, Context context)
	{
		if (!isNodeEditable(node))
		{
			return false;
		}
		Action action = new SiteNodeAction(context, Type.NODE_DELETE, node);
		return brix.getAuthorizationStrategy().isActionAuthorized(action);
	}

	public boolean canRenameNode(BrixNode node, Context context)
	{
		if (!isNodeEditable(node))
		{
			return false;
		}
		Action action = new SiteNodeAction(context, Type.NODE_DELETE, node);
		return brix.getAuthorizationStrategy().isActionAuthorized(action);
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
