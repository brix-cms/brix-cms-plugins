package org.brixcms.plugin.content.folder;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.content.ContentPlugin;
import org.brixcms.plugin.hierarchical.HierarchicalNodePlugin;
import org.brixcms.plugin.hierarchical.admin.NodeEditorPlugin;
import org.brixcms.plugin.site.SimpleCallback;

/**
 * @author dan.simko@gmail.com
 */
public class FolderNodePlugin implements NodeEditorPlugin {

    public static final String TYPE = HierarchicalNodePlugin.NS_PREFIX + "folder";

    public FolderNodePlugin(ContentPlugin contentPlugin) {
    }

    @Override
    public String getName() {
        return "folder";
    }

    @Override
    public String getNodeType() {
        return TYPE;
    }

    @Override
    public IModel<String> newCreateNodeCaptionModel(IModel<BrixNode> parentNode) {
        return new ResourceModel("content-plugin.createNewFolder");
    }

    @Override
    public Panel newCreateNodePanel(String id, IModel<BrixNode> parentNode, SimpleCallback goBack) {
        return new CreateFolderNodePanel(id, parentNode, getNodeType(), goBack);
    }

}
