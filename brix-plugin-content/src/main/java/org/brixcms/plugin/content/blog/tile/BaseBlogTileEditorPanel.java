package org.brixcms.plugin.content.blog.tile;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.brixcms.Brix;
import org.brixcms.auth.Action.Context;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.content.ContentPlugin;
import org.brixcms.plugin.content.folder.FolderNodePlugin;
import org.brixcms.plugin.hierarchical.admin.HierarchicalNodeManagerPanel;
import org.brixcms.plugin.site.page.tile.admin.GenericTileEditorPanel;
import org.brixcms.web.picker.common.TreeAwareNode;
import org.brixcms.web.picker.node.NodePickerPanel;
import org.brixcms.web.picker.node.NodeTypeFilter;
import org.brixcms.workspace.Workspace;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class BaseBlogTileEditorPanel extends GenericTileEditorPanel<BrixNode> {

    protected final BaseBlogContainer currentEntry;

    public BaseBlogTileEditorPanel(String id, IModel<BrixNode> tileContainerNode, BaseBlogContainer blogContainer) {
        super(id, tileContainerNode);
        this.currentEntry = blogContainer;
        setOutputMarkupId(true);
    }

    @Override
    public void load(BrixNode node) {
        currentEntry.load(node);
    }

    @Override
    public void save(BrixNode node) {
        currentEntry.save(node);
    }

    protected DropDownChoice<Workspace> newWorkspaceSwitcher(String id) {
        WorkspacesModel workspacesModel = new WorkspacesModel();
        DropDownChoice<Workspace> switcher = new DropDownChoice<Workspace>(id, new PropertyModel<Workspace>(currentEntry, "workspace"),
                workspacesModel, new ChoiceRenderer<Workspace>() {
                    @Override
                    public Object getDisplayValue(Workspace workspace) {
                        return ContentPlugin.get().getUserVisibleName(workspace, false);
                    }

                    @Override
                    public String getIdValue(Workspace object, int index) {
                        return object.getId();
                    }
                });
        add(switcher);
        switcher.setNullValid(false);
        switcher.add(new OnChangeAjaxBehavior() {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(BaseBlogTileEditorPanel.this);
            }

        });
        return switcher;
    }

    protected WebMarkupContainer newNodePickerPanel(String id) {
        // TODO workspace state
        Workspace workspace = ContentPlugin.get().getContentWorkspace(currentEntry.getContentWorkspaceName(), null);
        if (workspace == null) {
            return new WebMarkupContainer(id);
        }
        NodePickerPanel picker = new NodePickerPanel(id, new PropertyModel<BrixNode>(currentEntry, "contentFolder"),
                TreeAwareNode.Util.getTreeNode(ContentPlugin.get().getRootNode(workspace.getId())),
                HierarchicalNodeManagerPanel.SHOW_ALL_NON_NULL_NODES_FILTER, new NodeTypeFilter(FolderNodePlugin.TYPE));
        picker.setRequired(true);
        return picker;
    }

    private static class WorkspacesModel extends LoadableDetachableModel<List<Workspace>> {
        @Override
        protected List<Workspace> load() {
            return Brix.get().filterVisibleWorkspaces(ContentPlugin.get().getWorkspaces(null, false), Context.ADMINISTRATION);
        }
    }
}
