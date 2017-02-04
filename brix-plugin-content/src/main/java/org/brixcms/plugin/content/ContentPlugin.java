package org.brixcms.plugin.content;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.string.Strings;
import org.brixcms.Brix;
import org.brixcms.auth.Action;
import org.brixcms.auth.Action.Context;
import org.brixcms.jcr.JcrNodeWrapperFactory;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.api.JcrNodeIterator;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.content.auth.PostNodeAction;
import org.brixcms.plugin.content.auth.PostNodeAction.Type;
import org.brixcms.plugin.content.blog.post.PostNode;
import org.brixcms.plugin.content.blog.post.PostNodePlugin;
import org.brixcms.plugin.content.blog.post.PostNodeTreeRenderer;
import org.brixcms.plugin.content.blog.post.admin.editor.SimplePostEditorFactory;
import org.brixcms.plugin.content.blog.post.admin.editor.TinymcePostEditorFactory;
import org.brixcms.plugin.content.blog.post.comment.CommentNode;
import org.brixcms.plugin.content.blog.tile.BlogTile;
import org.brixcms.plugin.content.blog.tile.archive.BlogArchiveTile;
import org.brixcms.plugin.content.folder.FolderNode;
import org.brixcms.plugin.content.folder.FolderNodePlugin;
import org.brixcms.plugin.hierarchical.HierarchicalNodePlugin;
import org.brixcms.plugin.hierarchical.HierarchicalPluginLocator;
import org.brixcms.plugin.hierarchical.admin.NodeEditorPlugin;
import org.brixcms.plugin.site.ManageNodeTabFactory;
import org.brixcms.plugin.site.NodeTreeRenderer;
import org.brixcms.plugin.site.page.tile.Tile;
import org.brixcms.registry.ExtensionPoint;
import org.brixcms.registry.ExtensionPointRegistry;
import org.brixcms.workspace.Workspace;

/**
 *
 * @author dan.simko@gmail.com
 */
// TODO add doc
public class ContentPlugin extends HierarchicalNodePlugin {

    public static final String WORKSPACE_ATTRIBUTE_STATE = "brix:content-state";
    public static final String WORKSPACE_ATTRIBUTE_NAME = "brix:content-name";
    private static final String ID = ContentPlugin.class.getName();
    private static final String WORKSPACE_TYPE = "brix:content";
    private static final String CONTETNT_NODE_NAME = Brix.NS_PREFIX + "content";

    private final Brix brix;

    public ContentPlugin(Brix brix) {
        super(brix);
        this.brix = brix;
        initializeExtensionPoints(brix);
        ExtensionPointRegistry registry = brix.getConfig().getRegistry();
        registry.register(JcrNodeWrapperFactory.POINT, ContentRootNode.FACTORY);
        registry.register(JcrNodeWrapperFactory.POINT, FolderNode.FACTORY);
        registry.register(JcrNodeWrapperFactory.POINT, PostNode.FACTORY);
        registry.register(JcrNodeWrapperFactory.POINT, CommentNode.FACTORY);

        registry.register(MNTF_POINT, new ContentManageNodeTabFactory());
        registry.register(NEP_POINT, new FolderNodePlugin());
        registry.register(NEP_POINT, new PostNodePlugin());

        registry.register(SimplePostEditorFactory.POINT, new SimplePostEditorFactory());
        registry.register(TinymcePostEditorFactory.POINT, new TinymcePostEditorFactory());
        registry.register(NodeTreeRenderer.POINT, new PostNodeTreeRenderer());

        registry.register(Tile.POINT, new BlogTile());
        registry.register(Tile.POINT, new BlogArchiveTile());
    }

    @Override
    public void initWorkspace(Workspace workspace, JcrSession workspaceSession) {
        if (isContentWorkspace(workspace)) {
            JcrNode root;
            if (workspaceSession.itemExists(brix.getRootPath())) {
                root = (JcrNode) workspaceSession.getItem(getBrix().getRootPath());
            } else {
                root = workspaceSession.getRootNode().addNode(Brix.ROOT_NODE_NAME, "nt:folder");
            }
            if (!root.isNodeType(BrixNode.JCR_TYPE_BRIX_NODE)) {
                root.addMixin(BrixNode.JCR_TYPE_BRIX_NODE);
            }
            if (!root.hasNode(CONTETNT_NODE_NAME)) {
                JcrNode content = root.addNode(CONTETNT_NODE_NAME, "nt:folder");
                content.addMixin(BrixNode.JCR_TYPE_BRIX_NODE);
                JcrNodeIterator nodes = root.getNodes();
                while (nodes.hasNext()) {
                    BrixNode node = (BrixNode) nodes.nextNode();
                    if (node.isSame(content) == false) {
                        JcrSession session = root.getSession();
                        session.move(node.getPath(), content.getPath() + "/" + node.getName());
                    }
                }
            }
        }
    }

    @Override
    public List<Workspace> getWorkspaces(Workspace currentWorkspace, boolean isFrontend) {
        if (isFrontend) {
            return Collections.emptyList();
        }
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put(Brix.WORKSPACE_ATTRIBUTE_TYPE, WORKSPACE_TYPE);
        List<Workspace> workspaces = new ArrayList<Workspace>(brix.getWorkspaceManager().getWorkspacesFiltered(attributes));

        Collections.sort(workspaces, new Comparator<Workspace>() {
            @Override
            public int compare(Workspace o1, Workspace o2) {
                String n1 = getWorkspaceName(o1);
                String n2 = getWorkspaceName(o2);

                int r = n1.compareTo(n2);
                if (r == 0) {
                    String s1 = getWorkspaceState(o1);
                    String s2 = getWorkspaceState(o2);

                    if (s1 != null && s2 != null) {
                        return s1.compareTo(s2);
                    } else {
                        return 0;
                    }
                } else {
                    return r;
                }
            }
        });

        return workspaces;
    }

    @Override
    public boolean isPluginWorkspace(Workspace workspace) {
        return isContentWorkspace(workspace);
    }

    @Override
    public String getUserVisibleName(Workspace workspace, boolean isFrontend) {
        String name = "Content - " + getWorkspaceName(workspace);
        String state = getWorkspaceState(workspace);
        if (!Strings.isEmpty(state)) {
            name = name + " - " + state;
        }
        return name;
    }

    public boolean isContentWorkspace(Workspace workspace) {
        return WORKSPACE_TYPE.equals(workspace.getAttribute(Brix.WORKSPACE_ATTRIBUTE_TYPE));
    }

    private static final ExtensionPoint<NodeEditorPlugin> NEP_POINT = new ExtensionPoint<NodeEditorPlugin>() {
        @Override
        public Multiplicity getMultiplicity() {
            return Multiplicity.COLLECTION;
        }

        @Override
        public String getUuid() {
            return ContentPlugin.class.getName() + ":NodeEditorPluginExtensionPoint";
        }
    };

    private static final ExtensionPoint<ManageNodeTabFactory> MNTF_POINT = new ExtensionPoint<ManageNodeTabFactory>() {

        @Override
        public Multiplicity getMultiplicity() {
            return Multiplicity.COLLECTION;
        }

        @Override
        public String getUuid() {
            return ContentPlugin.class.getName() + ":ManageNodeTabFactoryExtensionPoint";
        }

    };

    @Override
    protected ExtensionPoint<? extends NodeEditorPlugin> getNodeEditorPluginExtensionPoint() {
        return NEP_POINT;
    }

    @Override
    protected ExtensionPoint<ManageNodeTabFactory> getManageNodeTabFactoryExtensionPoint() {
        return MNTF_POINT;
    }

    public static ContentPlugin get(Brix brix) {
        return (ContentPlugin) brix.getPlugin(ID);
    }

    public static ContentPlugin get() {
        return get(Brix.get());
    }

    @Override
    protected HierarchicalPluginLocator getPluginLocator() {
        return new ContentPluginLocator();
    }

    @Override
    protected IModel<String> getTabName() {
        return new ResourceModel("content-plugin.pluginName");
    }

    @Override
    public String getId() {
        return ID;
    }

    public boolean contentExists(String name, String state) {
        return getContentWorkspace(name, state) != null;
    }

    public Workspace getContentWorkspace(String name, String state) {
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put(Brix.WORKSPACE_ATTRIBUTE_TYPE, WORKSPACE_TYPE);
        attributes.put(WORKSPACE_ATTRIBUTE_NAME, name);

        if (state != null) {
            attributes.put(WORKSPACE_ATTRIBUTE_STATE, state);
        }
        List<Workspace> res = brix.getWorkspaceManager().getWorkspacesFiltered(attributes);
        return res.isEmpty() ? null : res.get(0);
    }

    public Workspace createContent(String name, String state) {
        Workspace workspace = brix.getWorkspaceManager().createWorkspace();
        workspace.setAttribute(Brix.WORKSPACE_ATTRIBUTE_TYPE, WORKSPACE_TYPE);
        setWorkspaceName(workspace, name);
        setWorkspaceState(workspace, state);
        return workspace;
    }

    public void setWorkspaceName(Workspace workspace, String name) {
        workspace.setAttribute(WORKSPACE_ATTRIBUTE_NAME, name);
    }

    public void setWorkspaceState(Workspace workspace, String state) {
        workspace.setAttribute(WORKSPACE_ATTRIBUTE_STATE, state);
    }

    public String getWorkspaceName(Workspace workspace) {
        return workspace.getAttribute(WORKSPACE_ATTRIBUTE_NAME);
    }

    public String getWorkspaceState(Workspace workspace) {
        return workspace.getAttribute(WORKSPACE_ATTRIBUTE_STATE);
    }

    @Override
    protected String getRootNodeName() {
        return CONTETNT_NODE_NAME;
    }

    public boolean canViewPostNode(PostNode node, Context context) {
        Action action = new PostNodeAction(context, Type.VIEW, node);
        return brix.getAuthorizationStrategy().isActionAuthorized(action);
    }

    public static String normalizeValue(final String value, int maxLength) {
        if (value != null) {
            String normalized = Normalizer.normalize(value, Normalizer.Form.NFKD).replaceAll("[^\\p{ASCII}]+", "");
            normalized = normalized.replaceAll("[^a-zA-Z0-9]", "-");
            normalized = normalized.replaceAll("-+", "-");
            if (normalized.length() > maxLength) {
                normalized = normalized.substring(0, maxLength);
            }
            normalized = normalized.toLowerCase();
            return normalized;
        }
        return null;
    }

    @Override
    protected int getTabPriority() {
        return 1000;
    }
}
