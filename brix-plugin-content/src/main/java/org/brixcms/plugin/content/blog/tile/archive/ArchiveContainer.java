package org.brixcms.plugin.content.blog.tile.archive;

import org.brixcms.plugin.content.blog.tile.BaseBlogContainer;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class ArchiveContainer extends BaseBlogContainer {

    @Override
    protected String getType() {
        return "archive";
    }

}
