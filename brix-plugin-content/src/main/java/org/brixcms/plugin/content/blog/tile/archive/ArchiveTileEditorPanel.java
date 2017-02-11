package org.brixcms.plugin.content.blog.tile.archive;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.content.blog.tile.BaseBlogTileEditorPanel;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class ArchiveTileEditorPanel extends BaseBlogTileEditorPanel {

    public ArchiveTileEditorPanel(String id, IModel<BrixNode> tileContainerNode) {
        super(id, tileContainerNode, new ArchiveContainer());
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
