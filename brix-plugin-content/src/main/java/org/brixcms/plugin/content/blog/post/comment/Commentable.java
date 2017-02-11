package org.brixcms.plugin.content.blog.post.comment;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.util.TraversingItemVisitor;

/**
 * @author dan.simko@gmail.com
 */
// TODO add doc
public interface Commentable extends Serializable {

    List<CommentNode> getComments(int level);

    void addComment(String comment);

    public static class CommentsCollector extends TraversingItemVisitor.Default {

        private final Collection<CommentNode> comments;

        public CommentsCollector(List<CommentNode> comments, int level) {
            super(false, level);
            this.comments = comments;
        }

        @Override
        protected void entering(javax.jcr.Node node, int level) throws RepositoryException {
            if (level > 0) {
                if (node instanceof CommentNode) {
                    comments.add((CommentNode) node);
                }
            }
        }

    }

}
