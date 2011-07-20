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

package brix.plugin.file;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import org.brixcms.Brix;
import org.brixcms.jcr.JcrNodeWrapperFactory;
import org.brixcms.jcr.RepositoryInitializer;
import brix.plugin.file.admin.folder.FolderNodePlugin;
import brix.plugin.file.admin.resource.UploadFilePlugin;
import brix.plugin.hierarchical.HierarchicalNodePlugin;
import brix.plugin.hierarchical.HierarchicalPluginLocator;
import brix.plugin.hierarchical.HierarchicalRepoInitializer;
import brix.plugin.hierarchical.admin.NodeEditorPlugin;
import org.brixcms.plugin.site.ManageNodeTabFactory;
import org.brixcms.registry.ExtensionPoint;
import org.brixcms.registry.ExtensionPointRegistry;
import org.brixcms.workspace.Workspace;

/**
 * @author wickeria at gmail.com
 */
public class FilePlugin extends HierarchicalNodePlugin {

	public static final String ID = FilePlugin.class.getName();
	public static final String ROOT_NODE_NAME = "Files";

	private static final ExtensionPoint<NodeEditorPlugin> NEP_POINT = new ExtensionPoint<NodeEditorPlugin>() {
		public brix.registry.ExtensionPoint.Multiplicity getMultiplicity() {
			return Multiplicity.COLLECTION;
		}

		public String getUuid() {
			return FilePlugin.class.getName() + ":NodeEditorPluginExtensionPoint";
		}
	};

	private static final ExtensionPoint<ManageNodeTabFactory> MNTF_POINT = new ExtensionPoint<ManageNodeTabFactory>() {

		public brix.registry.ExtensionPoint.Multiplicity getMultiplicity() {
			return Multiplicity.COLLECTION;
		}

		public String getUuid() {
			return FilePlugin.class.getName() + ":ManageNodeTabFactoryExtensionPoint";
		}

	};

	public FilePlugin(Brix brix) {
		super(brix);
		ExtensionPointRegistry registry = brix.getConfig().getRegistry();
		registry.register(RepositoryInitializer.POINT, new HierarchicalRepoInitializer());
		registry.register(JcrNodeWrapperFactory.POINT, FolderNode.FACTORY(getPluginLocator()));
		registry.register(NEP_POINT, new FolderNodePlugin(getPluginLocator()));
		registry.register(NEP_POINT, new UploadFilePlugin(getPluginLocator()));
		registry.register(MNTF_POINT, new FileManageNodeTabFactory(getPluginLocator()));
	}

	@Override
	protected ExtensionPoint<? extends NodeEditorPlugin> getNodeEditorPluginExtensionPoint() {
		return NEP_POINT;
	}

	@Override
	protected ExtensionPoint<ManageNodeTabFactory> getManageNodeTabFactoryExtensionPoint() {
		return MNTF_POINT;
	}

	public static FilePlugin get(Brix brix) {
		return (FilePlugin) brix.getPlugin(ID);
	}

	public static FilePlugin get() {
		return get(Brix.get());
	}

	@Override
	protected HierarchicalPluginLocator getPluginLocator() {
		return new FilePluginLocator();
	}

	@Override
	protected String getRootNodeName() {
		return ROOT_NODE_NAME;
	}

	@Override
	protected IModel<String> getTabName() {
		return new ResourceModel("files");
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getUserVisibleName(Workspace workspace, boolean isFrontend) {
		return null;
	}

	// @Override
	// public void initWorkspace(Workspace workspace, JcrSession
	// workspaceSession) {
	// if (!GalleryPlugin.get().isGalleryWorkspace(workspace)) {
	// super.initWorkspace(workspace, workspaceSession);
	// }
	// }

}
