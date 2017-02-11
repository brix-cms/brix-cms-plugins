package org.brixcms.plugin.content.auth;

import org.brixcms.auth.AbstractNodeAction;
import org.brixcms.plugin.content.blog.post.PostNode;

/**
 * @author dan.simko@gmail.com
 */
public class PostNodeAction extends AbstractNodeAction {

    private final Type type;

    public PostNodeAction(Context context, Type type, PostNode node) {
        super(context, node);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public PostNode getNode() {
        return (PostNode) super.getNode();
    }

    @Override
    public String toString() {
        return "PostNodeAction{" + "type=" + type + "} " + super.toString();
    }

    public enum Type {
        VIEW, CREATE, EDIT, DELETE
    }
}
