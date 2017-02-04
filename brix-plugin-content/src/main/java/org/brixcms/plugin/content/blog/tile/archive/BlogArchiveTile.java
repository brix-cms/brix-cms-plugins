package org.brixcms.plugin.content.blog.tile.archive;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.page.tile.Tile;
import org.brixcms.plugin.site.page.tile.admin.TileEditorPanel;

/**
 * @author dan.simko@gmail.com
 */
public class BlogArchiveTile implements Tile {

	@Override
    public String getDisplayName() {
		return "Blog Archive";
	}

	@Override
    public String getTypeName() {
		return getClass().getName();
	}

	@Override
    public TileEditorPanel newEditor(String id, IModel<BrixNode> tileContainerNode) {
		return new ArchiveTileEditorPanel(id, tileContainerNode);
	}

	@Override
    public Component newViewer(String id, IModel<BrixNode> tileNode) {
		return new ArchivePanel(id, tileNode);
	}

	@Override
    public boolean requiresSSL(IModel<BrixNode> tileNode) {
		return false;
	}

}
