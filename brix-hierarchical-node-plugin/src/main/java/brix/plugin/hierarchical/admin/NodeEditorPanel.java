package brix.plugin.hierarchical.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.jcr.ReferentialIntegrityException;

import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import brix.Path;
import brix.auth.Action.Context;
import brix.jcr.exception.JcrException;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.hierarchical.HierarchicalPluginLocator;
import brix.plugin.site.ManageNodeTabFactory;
import brix.plugin.site.admin.NodeManagerTabbedPanel;
import brix.plugin.site.admin.RenamePanel;
import brix.web.generic.BrixGenericPanel;
import brix.web.tab.IBrixTab;
import brix.web.util.PathLabel;

@SuppressWarnings("deprecation")
public class NodeEditorPanel extends BrixGenericPanel<BrixNode>
{

	private final HierarchicalPluginLocator pluginLocator;
	
	public NodeEditorPanel(String id, IModel<BrixNode> model, final HierarchicalPluginLocator pluginLocator)
	{
        super(id, model);
        this.pluginLocator = pluginLocator;

        String root = pluginLocator.getPlugin().getRootNodePath();
        add(new PathLabel("path2", model, root)
        {
            @Override
            protected void onPathClicked(Path path)
            {
                BrixNode node = (BrixNode) getNode().getSession().getItem(path.toString());
                selectNode(node, false);
            }
        });

        add(new Link<Void>("rename")
        {
            @Override
            public void onClick()
            {
                String id = NodeEditorPanel.this.getId();
                Panel renamePanel = new RenamePanel(id, NodeEditorPanel.this.getModel())
                {
                    @Override
                    protected void onLeave()
                    {
                        pluginLocator.getPlugin().refreshNavigationTree(this);
                        replaceWith(NodeEditorPanel.this);
                    }
                };
                NodeEditorPanel.this.replaceWith(renamePanel);
            }

            @Override
            public boolean isVisible()
            {
                BrixNode node = NodeEditorPanel.this.getModelObject();
                String path = node.getPath();
                String root = pluginLocator.getPlugin().getRootNodePath();
                return pluginLocator.getPlugin().canRenameNode(node, Context.ADMINISTRATION) &&
                        path.length() > root.length() && path.startsWith(root);
            }
        });

        add(new Link<Void>("makeVersionable")
        {
			@Override
            public void onClick()
            {
                if (!getNode().isNodeType("mix:versionable"))
                {
                    getNode().addMixin("mix:versionable");
                    getNode().save();
                    getNode().checkin();
                }
            }

            @Override
            public boolean isVisible()
            {
                // TODO: Implement proper versioning support!
                return false;

                /*return getNode() != null && getNode().isNodeType("nt:file") &&
                        !getNode().isNodeType("mix:versionable") &&
                        pluginLocator.getPlugin().canEditNode(getNode(), Context.ADMINISTRATION);*/
            }
        });

        add(new Link<Void>("delete")
        {

            @Override
            public void onClick()
            {
                BrixNode node = getNode();
                BrixNode parent = (BrixNode)node.getParent();

                node.remove();
                try
                {
                    parent.save();
                    selectNode(parent, true);
                }
                catch (JcrException e)
                {
                    if (e.getCause() instanceof ReferentialIntegrityException)
                    {
                        parent.getSession().refresh(false);
                        NodeEditorPanel.this.getModel().detach();
                        // parent.refresh(false);
                        selectNode(NodeEditorPanel.this.getModelObject(), true);
                        getSession().error(
                            NodeEditorPanel.this.getString("referenceIntegrityError"));
                    }
                    else
                    {
                        throw e;
                    }
                }
            }

            @Override
            public boolean isVisible()
            {
                BrixNode node = NodeEditorPanel.this.getModelObject();
                String path = node.getPath();
                String root = pluginLocator.getPlugin().getRootNodePath();

                return pluginLocator.getPlugin().canDeleteNode(getNode(), Context.ADMINISTRATION) &&
                        path.length() > root.length() && path.startsWith(root);
            }

        });

        add(new SessionFeedbackPanel("sessionFeedback"));

        add(new NodeManagerTabbedPanel("tabbedPanel", getTabs(getModel())));
    }

    public BrixNode getNode()
    {
        return getModelObject();
    }

    private void selectNode(BrixNode node, boolean refresh)
    {
        pluginLocator.getPlugin().selectNode(this, node, refresh);
    }

    private List<IBrixTab> getTabs(IModel<BrixNode> nodeModel)
    {
        BrixNode node = nodeModel.getObject();

        final Collection<? extends ManageNodeTabFactory> factories;
        if (node != null)
        {
            factories = pluginLocator.getPlugin().getManageNodeTabFactories();
        }
        else
        {
            factories = Collections.emptyList();
        }

        if (factories != null && !factories.isEmpty())
        {
            List<IBrixTab> result = new ArrayList<IBrixTab>();
            for (ManageNodeTabFactory f : factories)
            {
                List<IBrixTab> tabs = f.getManageNodeTabs(nodeModel);
                if (tabs != null)
                    result.addAll(tabs);
            }
            return result;
        }
        else
        {
            return Collections.emptyList();
        }
    }

    private static class SessionFeedbackPanel extends FeedbackPanel
    {

        public SessionFeedbackPanel(String id)
        {
            super(id, new Filter());
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean isVisible()
        {
            List messages = (List) getFeedbackMessagesModel().getObject();
            return messages != null && !messages.isEmpty();
        }

        private static class Filter implements IFeedbackMessageFilter
        {
            public boolean accept(FeedbackMessage message)
            {
                return message.getReporter() == null;
            }
        };
    };
}
