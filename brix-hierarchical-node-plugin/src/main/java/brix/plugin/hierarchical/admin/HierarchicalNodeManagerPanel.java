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

package brix.plugin.hierarchical.admin;

import brix.plugin.hierarchical.HierarchicalPluginLocator;
import brix.plugin.hierarchical.NodeChildFilter;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.tree.BaseTree;
import org.apache.wicket.markup.html.tree.DefaultTreeState;
import org.apache.wicket.markup.html.tree.ITreeState;
import org.apache.wicket.markup.html.tree.LinkTree;
import org.apache.wicket.model.IModel;
import org.brixcms.BrixNodeModel;
import org.brixcms.auth.Action.Context;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.NodeTreeRenderer;
import org.brixcms.plugin.site.SimpleCallback;
import org.brixcms.web.generic.BrixGenericPanel;
import org.brixcms.web.picker.common.TreeAwareNode;
import org.brixcms.web.tree.AbstractTreeModel;
import org.brixcms.web.tree.JcrTreeNode;
import org.brixcms.web.tree.NodeFilter;
import org.brixcms.web.tree.TreeNode;
import org.brixcms.web.util.AbstractModel;
import org.brixcms.workspace.Workspace;

import java.util.Arrays;
import java.util.Collection;

public class HierarchicalNodeManagerPanel extends BrixGenericPanel<BrixNode>
		implements
			NodeTreeParentComponent
{
	private static final String EDITOR_ID = "editor";
	private static MetaDataKey<String> EDITOR_NODE_TYPE = new MetaDataKey<String>()
	{
	};

	private final HierarchicalPluginLocator pluginLocator;
	private final IModel<Workspace> workspaceModel;
	private final BaseTree tree;
	private Component lastEditor;
	private Component editor;

	public HierarchicalNodeManagerPanel(String id, IModel<Workspace> workspaceModel,
			IModel<BrixNode> rootNodeModel, HierarchicalPluginLocator pluginLocator)
	{
		super(id, rootNodeModel);
		this.workspaceModel = workspaceModel;
		this.pluginLocator = pluginLocator;
		add(tree = new Tree("tree", new TreeModel()));
	}

	@Override
	protected void onBeforeRender()
	{
		super.onBeforeRender();
		if (get("createNodesContainer") == null)
		{
			WebMarkupContainer createNodesContainer = new WebMarkupContainer("createNodesContainer")
			{
				@Override
				public boolean isVisible()
				{
					BrixNode folderNode = getNewNodeParent().getObject();
					return pluginLocator.getPlugin().canAddNodeChild(folderNode,
							Context.ADMINISTRATION);
				}
			};
			add(createNodesContainer);

			final NodeEditorPluginEntriesModel createNodesModel = new NodeEditorPluginEntriesModel(
					pluginLocator, getNewNodeParent());
			createNodesContainer.add(new ListView<NodeEditorPluginEntry>("createNodes",
					createNodesModel)
			{
				@Override
				protected void populateItem(final ListItem<NodeEditorPluginEntry> item)
				{
					Link<Void> link;
					item.add(link = new Link<Void>("link")
					{
						@Override
						public void onClick()
						{
							NodeEditorPlugin plugin = item.getModelObject().getPlugin();
							final Component currentEditor = getEditor();

							// remember the last editor that is not a create
							// node
							// panel
							if (lastEditor == null
									|| currentEditor.getMetaData(EDITOR_NODE_TYPE) == null)
							{
								lastEditor = currentEditor;
							}
							SimpleCallback goBack = new SimpleCallback()
							{
								public void execute()
								{
									setupEditor(lastEditor);
								}
							};
							IModel<BrixNode> parent = getNewNodeParent(plugin.getNodeType());
							Panel panel = plugin.newCreateNodePanel(EDITOR_ID, parent, goBack);
							panel.setMetaData(EDITOR_NODE_TYPE, plugin.getNodeType());
							setupEditor(panel);
						}

						@Override
						protected void onComponentTag(ComponentTag tag)
						{
							super.onComponentTag(tag);
							NodeEditorPlugin plugin = item.getModelObject().getPlugin();
							String editorNodeType = getEditor().getMetaData(EDITOR_NODE_TYPE);
							if (plugin.getNodeType().equals(editorNodeType))
							{
								CharSequence klass = tag.getString("class");
								if (klass == null)
								{
									klass = "selected";
								}
								else
								{
									klass = klass + " selected";
								}
								tag.put("class", klass);
							}
						}
					});
					item.add(new WebMarkupContainer("separator")
					{
						@Override
						public boolean isVisible()
						{
							return item.getIndex() != createNodesModel.getObject().size() - 1;
						}
					});
					IModel<BrixNode> parent = getNewNodeParent();
					NodeEditorPlugin plugin = item.getModelObject().getPlugin();
					link.add(new Label("label", plugin.newCreateNodeCaptionModel(parent)));
				}

			}.setReuseItems(false));

			editor = new WebMarkupContainer(EDITOR_ID);
			add(editor);
			setupDefaultEditor();
		}

	}

	private void setupEditor(Component newEditor)
	{
		editor.replaceWith(newEditor);
		editor = newEditor;
	}

	private void setupDefaultEditor()
	{
		setupEditor(new NodeEditorPanel(EDITOR_ID, getModel(), pluginLocator));
	}

	private Component getEditor()
	{
		return get(EDITOR_ID);
	};

	private IModel<BrixNode> getNewNodeParent()
	{
		return getNewNodeParent(null);
	}

	private IModel<BrixNode> getNewNodeParent(String nodeType)
	{
		return getNewNodeParent(nodeType, getModel());
	};

	private IModel<BrixNode> getNewNodeParent(String nodeType, IModel<BrixNode> currentModel)
	{
		BrixNode current = currentModel.getObject();
		if (nodeType != null && current instanceof NodeChildFilter)
		{
			NodeChildFilter ncf = (NodeChildFilter)current;
			if (ncf.isNodeTypeAllowedAsChild(nodeType))
			{
				return currentModel;
			}
			else
			{
				return getNewNodeParent(nodeType, new BrixNodeModel((BrixNode)current.getParent()));
			}
		}
		if (current.isFolder())
		{
			return getModel();
		}
		else
		{
			return new BrixNodeModel((BrixNode)current.getParent());
		}
	};

	private class Tree extends LinkTree
	{

		public Tree(String id, TreeModel model)
		{
			super(id, model);
			setLinkType(LinkType.REGULAR);
			getTreeState().expandNode(model.getRoot());
		}

		@Override
		protected Component newNodeComponent(String id, IModel<Object> model)
		{
			JcrTreeNode node = (JcrTreeNode)model.getObject();
			BrixNode n = node.getNodeModel().getObject();
			Collection<NodeTreeRenderer> renderers = n.getBrix().getConfig().getRegistry()
					.lookupCollection(NodeTreeRenderer.POINT);
			for (NodeTreeRenderer renderer : renderers)
			{
				Component component = renderer.newNodeComponent(id, Tree.this, model);
				if (component != null)
				{
					return component;
				}
			}
			return super.newNodeComponent(id, model);
		}

		@Override
		protected Component newJunctionLink(MarkupContainer parent, String id, Object node)
		{
			LinkType old = getLinkType();
			setLinkType(LinkType.AJAX);
			Component c = super.newJunctionLink(parent, id, node);
			setLinkType(old);
			return c;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected IModel getNodeTextModel(final IModel nodeModel)
		{
			return new AbstractModel<String>()
			{
				@Override
				public String getObject()
				{
					JcrTreeNode node = (JcrTreeNode)nodeModel.getObject();
					BrixNode n = node.getNodeModel().getObject();
					return n.getUserVisibleName();
				}
			};
		}

		@Override
		protected ITreeState newTreeState()
		{
			return new TreeState();
		}
	};

	private class TreeState extends DefaultTreeState
	{
		@Override
		public void selectNode(Object node, boolean selected)
		{
			if (selected)
			{
				JcrTreeNode n = (JcrTreeNode)node;
				HierarchicalNodeManagerPanel.this.setModel(n.getNodeModel());
				setupDefaultEditor();
				expandParents(n.getNodeModel().getObject());
			}
		}

		private void expandParents(BrixNode node)
		{
			BrixNode parent = (BrixNode)node.getParent();
			while (parent.getDepth() > 0)
			{
				expandNode(getTreeNode(parent));
				parent = (BrixNode)parent.getParent();
			}
		}

		@Override
		public boolean isNodeSelected(Object node)
		{
			JcrTreeNode n = (JcrTreeNode)node;
			IModel<BrixNode> model = n.getNodeModel();
			return model != null && model.equals(HierarchicalNodeManagerPanel.this.getModel());
		}

		@Override
		public Collection<Object> getSelectedNodes()
		{
			JcrTreeNode node = getTreeNode(getModelObject());
			return Arrays.asList(new Object[] { node });
		}
	};

	private class TreeModel extends AbstractTreeModel
	{
		public TreeNode getRoot()
		{
			Workspace workspace = workspaceModel.getObject();
			return getTreeNode(pluginLocator.getPlugin().getRootNode(workspace.getId()));
		}
	};

	public static final NodeFilter SHOW_ALL_NON_NULL_NODES_FILTER = new NodeFilter()
	{
		public boolean isNodeAllowed(BrixNode node)
		{
			return node != null;
		}
	};

	private JcrTreeNode getTreeNode(BrixNode node)
	{
		return TreeAwareNode.Util.getTreeNode(node, SHOW_ALL_NON_NULL_NODES_FILTER);
	}

	public void selectNode(BrixNode node)
	{
		tree.getTreeState().selectNode(getTreeNode(node), true);
	}

	public void updateTree()
	{
		tree.invalidateAll();
		tree.updateTree();
	}
}
