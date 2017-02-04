package org.brixcms.plugin.content.blog.tile;

import java.io.Serializable;

import org.brixcms.Brix;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.content.ContentPlugin;
import org.brixcms.workspace.Workspace;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public abstract class BaseBlogContainer implements Serializable {

    private final String CONTENT_WORKSPACE_NAME = getType() + "contentWorkspaceName";
    private final String CONTENT_FOLDER_PATH = getType() + "contentFolderPath";
    private final String ITEMS_PER_PAGE = getType() + "itemsPerPage";

    private long itemsPerPage;
    private String contentWorkspaceName;
    private String contentFolderPath;

    protected abstract String getType();

    public Workspace getWorkspace() {
        if (contentWorkspaceName == null) {
            return null;
        }
        // TODO add workspace state
        return ContentPlugin.get().getContentWorkspace(contentWorkspaceName, null);
    }

    public void setWorkspace(Workspace contentWorkspace) {
        if (contentWorkspace != null) {
            this.contentWorkspaceName = contentWorkspace.getAttribute(ContentPlugin.WORKSPACE_ATTRIBUTE_NAME);
        }
    }

    public BrixNode getContentFolder() {
        if (contentWorkspaceName == null || contentFolderPath == null) {
            return null;
        }
        Workspace workspace = getWorkspace();
        if (workspace != null) {
            JcrSession session = Brix.get().getCurrentSession(workspace.getId());
            if (session.itemExists(contentFolderPath)) {
                return (BrixNode) session.getNode(contentFolderPath);
            }
        }
        return null;
    }

    public void setContentFolder(BrixNode contentFolder) {
        if (contentFolder != null) {
            this.contentFolderPath = contentFolder.getPath();
        }
    }

    public void load(BrixNode node) {
        if (node.hasProperty(CONTENT_WORKSPACE_NAME)) {
            setContentWorkspaceName(node.getProperty(CONTENT_WORKSPACE_NAME).getString());
        }
        if (node.hasProperty(CONTENT_FOLDER_PATH)) {
            setContentFolderPath(node.getProperty(CONTENT_FOLDER_PATH).getString());
        }
        if (node.hasProperty(ITEMS_PER_PAGE)) {
            setItemsPerPage(node.getProperty(ITEMS_PER_PAGE).getLong());
        }
    }

    public void save(BrixNode node) {
        node.setProperty(CONTENT_WORKSPACE_NAME, contentWorkspaceName);
        node.setProperty(CONTENT_FOLDER_PATH, contentFolderPath);
        node.setProperty(ITEMS_PER_PAGE, itemsPerPage);
    }

    public long getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(long itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public String getContentWorkspaceName() {
        return contentWorkspaceName;
    }

    public void setContentWorkspaceName(String contentWorkspaceName) {
        this.contentWorkspaceName = contentWorkspaceName;
    }

    public String getContentFolderPath() {
        return contentFolderPath;
    }

    public void setContentFolderPath(String contentFolderPath) {
        this.contentFolderPath = contentFolderPath;
    }

}
