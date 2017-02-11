package org.brixcms.plugin.content.blog.tile.post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.brixcms.BrixNodeModel;
import org.brixcms.auth.Action.Context;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.api.JcrNodeIterator;
import org.brixcms.plugin.content.ContentPlugin;
import org.brixcms.plugin.content.blog.post.PostNode;
import org.brixcms.plugin.content.blog.tile.BaseBlogContainer;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class PostDataProvider implements IDataProvider<PostNode> {

    private BaseBlogContainer blogContainer;

    public PostDataProvider(BaseBlogContainer blogContainer) {
        this.blogContainer = blogContainer;
    }

    @Override
    public Iterator<PostNode> iterator(long first, long count) {
        return getPostNodes().subList((int) first, (int) (first + count)).iterator();
    }

    @Override
    public IModel<PostNode> model(PostNode object) {
        return new BrixNodeModel<>(object);
    }

    @Override
    public long size() {
        return getPostNodes().size();
    }

    private boolean canShowNode(PostNode node) {
        if (node != null && !node.isHidden() && ContentPlugin.get().canViewPostNode(node, Context.PRESENTATION)) {
            long time = new Date().getTime();
            Date startDate = node.getPublish();
            if (startDate != null && startDate.getTime() > time) {
                return false;
            }
            return true;
        }
        return false;
    }

    private List<PostNode> getPostNodes() {
        List<PostNode> res = new ArrayList<PostNode>();
        JcrNode contentFolder = blogContainer.getContentFolder();
        if (contentFolder != null) {
            JcrNodeIterator iterator = contentFolder.getNodes();
            while (iterator.hasNext()) {
                JcrNode node = iterator.nextNode();
                if (node instanceof PostNode) {
                    PostNode postNode = (PostNode) node;
                    if (canShowNode(postNode)) {
                        res.add(postNode);
                    }
                }
            }
            Collections.sort(res);
        }
        return res;
    }
}
