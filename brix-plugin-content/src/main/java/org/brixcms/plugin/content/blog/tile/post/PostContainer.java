package org.brixcms.plugin.content.blog.tile.post;

import org.brixcms.plugin.content.blog.tile.BaseBlogContainer;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class PostContainer extends BaseBlogContainer {

    @Override
    protected String getType() {
        return "post";
    }

}
