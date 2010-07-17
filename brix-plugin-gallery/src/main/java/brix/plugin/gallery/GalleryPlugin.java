/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package brix.plugin.gallery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventListener;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import brix.Brix;
import brix.SessionAwarePlugin;
import brix.jcr.JcrNodeWrapperFactory;
import brix.jcr.JcrSessionFactory;
import brix.jcr.RepositoryInitializer;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.jcr.base.BrixSession;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.gallery.album.AlbumFolderNode;
import brix.plugin.gallery.album.AlbumFolderNodePlugin;
import brix.plugin.gallery.photo.PhotoNode;
import brix.plugin.gallery.photo.PhotoNodePlugin;
import brix.plugin.gallery.webdav.WebdavPlugin;
import brix.plugin.hierarchical.HierarchicalNodePlugin;
import brix.plugin.hierarchical.HierarchicalPluginLocator;
import brix.plugin.hierarchical.HierarchicalRepoInitializer;
import brix.plugin.hierarchical.admin.NodeEditorPlugin;
import brix.plugin.hierarchical.nodes.SimpleFolderNode;
import brix.plugin.site.ManageNodeTabFactory;
import brix.plugin.site.NodeTreeRenderer;
import brix.registry.ExtensionPoint;
import brix.registry.ExtensionPointRegistry;
import brix.workspace.Workspace;

/**
 * @author wickeria at gmail.com
 */
public class GalleryPlugin extends HierarchicalNodePlugin implements SessionAwarePlugin {

	public static final String ID = GalleryPlugin.class.getName();
	public static final String ROOT_NODE_NAME = "Gallery";
	public static final String WORKSPACE_TYPE = NS_PREFIX + "Gallery";

	public static final int ALBUM_FOLDER_PREV_IMG_SIZE = 108;

	// TODO: make it configurable thru admin page
	public static enum FOLDERS {
		V_96(96, true), V_768(768, false), V_1024(1024, false);

		private Integer size;
		private boolean square;

		FOLDERS(Integer size, boolean square) {
			this.size = size;
			this.square = square;
		}

		public Integer getSize() {
			return size;
		}

		public boolean isSquare() {
			return square;
		}
	}

	private static final ExtensionPoint<NodeEditorPlugin> NEP_POINT = new ExtensionPoint<NodeEditorPlugin>() {
		public brix.registry.ExtensionPoint.Multiplicity getMultiplicity() {
			return Multiplicity.COLLECTION;
		}

		public String getUuid() {
			return GalleryPlugin.class.getName() + ":NodeEditorPluginExtensionPoint";
		}
	};

	private static final ExtensionPoint<ManageNodeTabFactory> MNTF_POINT = new ExtensionPoint<ManageNodeTabFactory>() {

		public brix.registry.ExtensionPoint.Multiplicity getMultiplicity() {
			return Multiplicity.COLLECTION;
		}

		public String getUuid() {
			return GalleryPlugin.class.getName() + ":ManageNodeTabFactoryExtensionPoint";
		}

	};

	public GalleryPlugin(Brix brix) {
		super(brix);
		ExtensionPointRegistry registry = brix.getConfig().getRegistry();
		registry.register(RepositoryInitializer.POINT, new HierarchicalRepoInitializer());
		registry.register(JcrNodeWrapperFactory.POINT, AlbumFolderNode.FACTORY);
		registry.register(JcrNodeWrapperFactory.POINT, PhotoNode.FACTORY);
		registry.register(NEP_POINT, new AlbumFolderNodePlugin(getPluginLocator()));
		registry.register(NEP_POINT, new PhotoNodePlugin(getPluginLocator()));
		registry.register(NEP_POINT, new WebdavPlugin());
		registry.register(MNTF_POINT, new GalleryManageNodeTabFactory(getPluginLocator()));

		registry.register(NodeTreeRenderer.POINT, new PhotoNodeTreeRenderer());

		// registry.register(NEP_POINT, new PreviewPlugin());

	}

	@Override
	protected ExtensionPoint<? extends NodeEditorPlugin> getNodeEditorPluginExtensionPoint() {
		return NEP_POINT;
	}

	@Override
	protected ExtensionPoint<ManageNodeTabFactory> getManageNodeTabFactoryExtensionPoint() {
		return MNTF_POINT;
	}

	public static GalleryPlugin get(Brix brix) {
		return (GalleryPlugin) brix.getPlugin(ID);
	}

	public static GalleryPlugin get() {
		return get(Brix.get());
	}

	@Override
	protected HierarchicalPluginLocator getPluginLocator() {
		return new GalleryPluginLocator();
	}

	@Override
	protected String getRootNodeName() {
		return ROOT_NODE_NAME;
	}

	@Override
	protected IModel<String> getTabName() {
		return new ResourceModel("gallery");
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getUserVisibleName(Workspace workspace, boolean isFrontend) {
		return null;
	}

	@Override
	public BrixNode getRootNode(String workspaceID) {
		return (BrixNode) getGallerySession().getNode(getRootNodePath());
	}

	public static JcrSession getGallerySession() {
		JcrSession session = get().getBrix().getCurrentSession(get().getGalleryWorkspace().getId());
		return session;
	}

	public Workspace getGalleryWorkspace() {
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(Brix.WORKSPACE_ATTRIBUTE_TYPE, WORKSPACE_TYPE);
		List<Workspace> res = getBrix().getWorkspaceManager().getWorkspacesFiltered(attributes);
		if (res.isEmpty()) {
			return null;
		} else {
			return res.get(0);
		}
	}

	public boolean isGalleryWorkspace(Workspace ws) {
		if (ws == null) {
			return false;
		}
		return WORKSPACE_TYPE.equals(ws.getAttribute(Brix.WORKSPACE_ATTRIBUTE_TYPE));
	}

	private Workspace createGalleryWorkspace() {
		Workspace workspace = getBrix().getWorkspaceManager().createWorkspace();
		workspace.setAttribute(Brix.WORKSPACE_ATTRIBUTE_TYPE, WORKSPACE_TYPE);
		JcrNode root = getBrix().getCurrentSession(workspace.getId()).getRootNode();
		JcrNode brixRoot = null;
		if (root.hasNode(Brix.ROOT_NODE_NAME)) {
			brixRoot = root.getNode(Brix.ROOT_NODE_NAME);
		} else {
			brixRoot = root.addNode(Brix.ROOT_NODE_NAME, SimpleFolderNode.JCR_PRIMARY_TYPE);
		}
		if (brixRoot != null) {
			if (!brixRoot.isNodeType(BrixNode.JCR_TYPE_BRIX_NODE)) {
				brixRoot.addMixin(BrixNode.JCR_TYPE_BRIX_NODE);
			}
		}
		JcrNode pluginRoot = null;
		if (brixRoot.hasNode(getRootNodeName())) {
			pluginRoot = brixRoot.getNode(getRootNodeName());
		} else {
			pluginRoot = brixRoot.addNode(getRootNodeName(), SimpleFolderNode.JCR_PRIMARY_TYPE);
		}
		if (pluginRoot != null) {
			if (!pluginRoot.isNodeType(BrixNode.JCR_TYPE_BRIX_NODE)) {
				pluginRoot.addMixin(BrixNode.JCR_TYPE_BRIX_NODE);
			}
		}
		root.getSession().save();
		return workspace;
	}

	@Override
	public void initWorkspace(Workspace workspace, JcrSession workspaceSession) {
		if (getGalleryWorkspace() == null) {
			createGalleryWorkspace();
		}
	}

	public void addGalleryEventListener(Session session, JcrSessionFactory sessionFactory) {
		EventListener listener = new GalleryEventListener(sessionFactory, get().getGalleryWorkspace().getId());
		int events = Event.NODE_ADDED | Event.NODE_REMOVED;
		try {
			session.getWorkspace().getObservationManager().addEventListener(listener, events, getRootNodePath(), true,
					null, null, false);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void onWebDavSession(BrixSession session) {
		addGalleryEventListener(session, Brix.get().getConfig().getSessionFactory());
	}
}
