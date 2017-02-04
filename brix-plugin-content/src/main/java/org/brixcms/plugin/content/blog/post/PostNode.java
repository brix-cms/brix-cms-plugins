package org.brixcms.plugin.content.blog.post;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.wicket.util.string.Strings;
import org.brixcms.Brix;
import org.brixcms.jcr.JcrNodeWrapperFactory;
import org.brixcms.jcr.RepositoryUtil;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.wrapper.BrixFileNode;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.content.blog.post.comment.CommentNode;
import org.brixcms.plugin.content.blog.post.comment.Commentable;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class PostNode extends BrixFileNode implements Commentable, Comparable<PostNode> {

    public static final int MAX_PERMALINK_LENGTH = 250;
    private static final String COMMNETS_FOLDER_NAME = "comments";

    public static enum State {
        Draft, PendingReview, Published
    }

    public static enum Visibility {
        // TODO implement feature "Stick this post to the front page"
        Public, PublicHighlighted, PasswordProtected, Private
    }

    public PostNode(Node delegate, JcrSession session) {
        super(delegate, session);
    }

    public static JcrNodeWrapperFactory FACTORY = new JcrNodeWrapperFactory() {
        @Override
        public boolean canWrap(Brix brix, JcrNode node) {
            return PostNodePlugin.TYPE.equals(getNodeType(node));
        }

        @Override
        public JcrNode wrap(Brix brix, Node node, JcrSession session) {
            return new PostNode(node, session);
        }

        @Override
        public void initializeRepository(Brix brix, Session session) {
            RepositoryUtil.registerNodeType(session.getWorkspace(), PostNodePlugin.TYPE, false, false, true);
        }
    };

    private static class Properties {
        public static final String TITLE = Brix.NS_PREFIX + "title";
        public static final String STATE = Brix.NS_PREFIX + "state";
        public static final String VISIBILITY = Brix.NS_PREFIX + "visibility";
        // TODO implement: immediately, date
        public static final String PUBLISH = Brix.NS_PREFIX + "publish";
        // TODO implement: uncategorized, ...
        public static final String CATEGORIES = Brix.NS_PREFIX + "categories";
        // TODO implement
        public static final String TAGS = Brix.NS_PREFIX + "tags";
        // TODO implement
        public static final String FEATURED_IMAGE = Brix.NS_PREFIX + "featuredImage";
    }

    public String getTitle() {
        return loadStringProperty(Properties.TITLE);
    }

    public void setTitle(String title) {
        setProperty(Properties.TITLE, title);
    }

    public State getState() {
        String state = loadStringProperty(Properties.STATE);
        if (Strings.isEmpty(state)) {
            return null;
        }
        return State.valueOf(state);
    }

    public void setState(State state) {
        if (state != null) {
            setProperty(Properties.STATE, state.name());
        }
    }

    public Visibility getVisibility() {
        String visibility = loadStringProperty(Properties.VISIBILITY);
        if (Strings.isEmpty(visibility)) {
            return null;
        }
        return Visibility.valueOf(visibility);
    }

    public void setVisibility(Visibility visibility) {
        if (visibility != null) {
            setProperty(Properties.VISIBILITY, visibility.name());
        }
    }

    public Date getPublish() {
        return loadDateAttribute(Properties.PUBLISH);
    }

    public void setPublish(Date date) {
        setDateAttribute(Properties.PUBLISH, date);
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
        JcrNode node = comments.addNode(UUID.randomUUID().toString(), "nt:file");
        CommentNode commentNode = CommentNode.initialize(node);
        commentNode.setData(comment);
        comments.save();
    }

    public static PostNode initialize(JcrNode node) {
        BrixNode brixNode = (BrixNode) node;
        BrixFileNode.initialize(node, "text/html");
        brixNode.setNodeType(PostNodePlugin.TYPE);
        brixNode.addNode(COMMNETS_FOLDER_NAME, "nt:folder");
        return new PostNode(node.getDelegate(), node.getSession());
    }

    private Date loadDateAttribute(String attribute) {
        Date result = null;
        if (hasProperty(attribute)) {
            result = getProperty(attribute).getDate().getTime();
        }
        return result;
    }

    private void setDateAttribute(String attribute, Date date) {
        Calendar calendar = null;
        if (date != null) {
            calendar = Calendar.getInstance();
            calendar.setTime(date);
        }
        setProperty(attribute, calendar);
    }

    private String loadStringProperty(String attribute) {
        String result = null;
        if (hasProperty(attribute)) {
            result = getProperty(attribute).getString();
        }
        return result;
    }

    @Override
    public String getUserVisibleType() {
        return "Post";
    }

    @Override
    public int compareTo(PostNode o) {
        if (getPublish() != null && o.getPublish() != null)
            return o.getPublish().compareTo(getPublish());
        if (getCreated() != null && o.getCreated() != null)
            return o.getCreated().compareTo(getCreated());
        return 0;
    }
}
