package org.brixcms.plugin.content.blog.post;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.content.ContentPlugin;
import org.brixcms.plugin.content.blog.post.admin.CreatePostPanel;
import org.brixcms.plugin.hierarchical.admin.NodeEditorPlugin;
import org.brixcms.plugin.site.SimpleCallback;
import org.brixcms.registry.ExtensionPoint;

/**
 * @author dan.simko@gmail.com
 */
public class PostNodePlugin implements NodeEditorPlugin {

    public static final String TYPE = ContentPlugin.NS_PREFIX + "post";

    public static final ExtensionPoint<NodeEditorPlugin> POINT = new ExtensionPoint<NodeEditorPlugin>() {
        @Override
        public Multiplicity getMultiplicity() {
            return Multiplicity.COLLECTION;
        }

        @Override
        public String getUuid() {
            return PostNodePlugin.class.getName();
        }
    };

    @Override
    public String getName() {
        return "post";
    }

    @Override
    public String getNodeType() {
        return TYPE;
    }

    @Override
    public IModel<String> newCreateNodeCaptionModel(IModel<BrixNode> parentNode) {
        return new ResourceModel("content-plugin.createNewPost");
    }

    @Override
    public Panel newCreateNodePanel(String id, IModel<BrixNode> parentNode, SimpleCallback goBack) {
        return new CreatePostPanel(id, parentNode, goBack);
    }

}
