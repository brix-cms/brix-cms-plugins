package org.brixcms.plugin.content.resource.admin;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.content.ContentPlugin;
import org.brixcms.plugin.content.resource.FileResourceReference;
import org.brixcms.plugin.content.resource.image.admin.ManageImageTabFactory;
import org.brixcms.plugin.hierarchical.admin.NodeEditorPlugin;
import org.brixcms.plugin.site.SimpleCallback;

/**
 * @author dan.simko@gmail.com
 */
public class ResourceNodePlugin implements NodeEditorPlugin {

    public static final String TYPE = org.brixcms.plugin.site.resource.ResourceNodePlugin.TYPE;

    public static final String WS_PARAM_NAME = "ws";
    public static final String ID_PARAM_NAME = "is";
    private static final String PATH = "content-plugin/${" + WS_PARAM_NAME + "}/${" + ID_PARAM_NAME + "}";

    public ResourceNodePlugin(ContentPlugin contentPlugin) {
        contentPlugin.registerManageNodeTabFactory(new ManageResourceNodeTabFactory());
        contentPlugin.registerManageNodeTabFactory(new ManageImageTabFactory());
        WebApplication.get().mountResource(PATH, FileResourceReference.INSTANCE);
    }

    @Override
    public String getName() {
        return "resource";
    }

    @Override
    public String getNodeType() {
        return TYPE;
    }

    @Override
    public IModel<String> newCreateNodeCaptionModel(IModel<BrixNode> parentNode) {
        return new ResourceModel("content-plugin.uploadFiles");
    }

    @Override
    public Panel newCreateNodePanel(String id, IModel<BrixNode> parentNode, SimpleCallback goBack) {
        return new UploadResourcesPanel(id, parentNode, goBack);
    }

}
