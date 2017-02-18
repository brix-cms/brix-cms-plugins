package org.brixcms.plugin.content.blog.post.comment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Session;

import org.brixcms.Brix;
import org.brixcms.jcr.JcrNodeWrapperFactory;
import org.brixcms.jcr.RepositoryUtil;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.wrapper.BrixFileNode;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.content.ContentPlugin;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class CommentNode extends BrixFileNode implements Commentable, Comparable<CommentNode> {

    public static final String TYPE = ContentPlugin.NS_PREFIX + "comment";
    protected static final String COMMNETS_FOLDER_NAME = "comments";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SS");

    public CommentNode(Node delegate, JcrSession session) {
        super(delegate, session);
    }

    public static JcrNodeWrapperFactory FACTORY = new JcrNodeWrapperFactory() {
        @Override
        public boolean canWrap(Brix brix, JcrNode node) {
            return TYPE.equals(getNodeType(node));
        }

        @Override
        public JcrNode wrap(Brix brix, Node node, JcrSession session) {
            return new CommentNode(node, session);
        }

        @Override
        public void initializeRepository(Brix brix, Session session) {
            RepositoryUtil.registerNodeType(session.getWorkspace(), TYPE, false, false, true);
        }
    };

    public static CommentNode initialize(JcrNode node) {
        BrixFileNode brixNode = BrixFileNode.initialize(node, "text/html");
        BrixNode content = (BrixNode) node.getNode("jcr:content");
        content.setHidden(true);
        brixNode.setNodeType(TYPE);
        brixNode.addNode(COMMNETS_FOLDER_NAME, "nt:folder");
        return new CommentNode(node.getDelegate(), node.getSession());
    }

    @Override
    public String getUserVisibleType() {
        return "Comment";
    }

    @Override
    public List<CommentNode> getComments(int level) {
        List<CommentNode> comments = new ArrayList<>();
        getNode(COMMNETS_FOLDER_NAME).accept(new CommentsCollector(comments, level));
        Collections.sort(comments);
        return comments;
    }

    @Override
    public void addComment(String comment) {
        JcrNode comments = getNode(COMMNETS_FOLDER_NAME);
        JcrNode node = comments.addNode(LocalDateTime.now().format(FORMATTER), "nt:file");
        CommentNode commentNode = CommentNode.initialize(node);
        commentNode.setData(comment);
        comments.save();
    }

    @Override
    public boolean isFolder() {
        return true;
    }

    @Override
    public int compareTo(CommentNode o) {
        if (getCreated() != null && o.getCreated() != null)
            return o.getCreated().compareTo(getCreated());
        return 0;
    }

}
