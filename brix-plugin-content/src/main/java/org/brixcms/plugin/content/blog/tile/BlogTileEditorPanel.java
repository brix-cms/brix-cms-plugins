package org.brixcms.plugin.content.blog.tile;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.content.blog.tile.post.PostContainer;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class BlogTileEditorPanel extends BaseBlogTileEditorPanel {

    public BlogTileEditorPanel(String id, IModel<BrixNode> tileContainerNode) {
        super(id, tileContainerNode, new PostContainer());
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        removeAll();
        add(newWorkspaceSwitcher("workspace"));
        add(newNodePickerPanel("nodePicker"));
        add(new TextField<Long>("count", new PropertyModel<Long>(currentEntry, "itemsPerPage"), Long.class).setRequired(true));
    }

}
