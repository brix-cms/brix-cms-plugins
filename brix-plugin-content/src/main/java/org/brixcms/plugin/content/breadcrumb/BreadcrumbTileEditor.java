package org.brixcms.plugin.content.breadcrumb;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.brixcms.BrixNodeModel;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.page.PageSiteNodePlugin;
import org.brixcms.plugin.site.page.tile.admin.GenericTileEditorPanel;
import org.brixcms.plugin.site.picker.node.SiteNodePickerPanel;
import org.brixcms.web.picker.node.NodePickerPanel;
import org.brixcms.web.picker.node.NodeTypeFilter;
import org.brixcms.web.tree.NodeFilter;

@SuppressWarnings("serial")
public class BreadcrumbTileEditor extends GenericTileEditorPanel<BrixNode> {

    public static final String HOME_NODE = "homeNode";
    public static final String HOME_TITLE = "homeTitle";
    private IModel<BrixNode> targetNodeModel = new BrixNodeModel<>();
    private String title = "Home";

    public BreadcrumbTileEditor(String id, IModel<BrixNode> tileContainerNode) {
        this(id, tileContainerNode, new NodeTypeFilter(PageSiteNodePlugin.TYPE));
    }

    public BreadcrumbTileEditor(String id, IModel<BrixNode> tileContainerNode, NodeFilter filter) {
        super(id, tileContainerNode);
        NodePickerPanel picker = new SiteNodePickerPanel("nodePicker", targetNodeModel,
                tileContainerNode.getObject().getSession().getWorkspace().getName(), filter);
        picker.setRequired(true);
        add(picker);
        add(new TextField<String>("title", new PropertyModel<String>(this, "title")).setRequired(true));
    }

    @Override
    protected void detachModel() {
        if (targetNodeModel != null) {
            targetNodeModel.detach();
        }
        super.detachModel();
    }

    @Override
    public void load(BrixNode node) {
        if (node.hasProperty(HOME_NODE)) {
            BrixNode pageNode = (BrixNode) node.getProperty(HOME_NODE).getNode();
            targetNodeModel.setObject(pageNode);
        }
        if (node.hasProperty(HOME_TITLE)) {
            title = node.getProperty(HOME_TITLE).getString();
        }
    }

    @Override
    public void save(BrixNode node) {
        node.setProperty(HOME_NODE, targetNodeModel.getObject());
        node.setProperty(HOME_TITLE, title);
    }

}
