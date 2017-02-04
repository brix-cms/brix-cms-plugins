package org.brixcms.plugin.content.folder;

import org.apache.wicket.model.IModel;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.content.ContentPluginLocator;
import org.brixcms.plugin.hierarchical.admin.CreateTitledNodePanel;
import org.brixcms.plugin.hierarchical.nodes.TitledNode;
import org.brixcms.plugin.site.SimpleCallback;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class CreateFolderNodePanel extends CreateTitledNodePanel {

    public CreateFolderNodePanel(String id, IModel<BrixNode> containerNodeModel, String pluginId, SimpleCallback goBack) {
        super(id, containerNodeModel, pluginId, goBack, new ContentPluginLocator());
    }

    @Override
    protected String getJcrPrimaryType() {
        return "nt:folder";
    }

    @Override
    protected TitledNode initializeNode(JcrNode node) {
        return FolderNode.initialize(node);
    }

}
