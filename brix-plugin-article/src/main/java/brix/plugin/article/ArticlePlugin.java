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
package brix.plugin.article;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import brix.Brix;
import brix.jcr.JcrNodeWrapperFactory;
import brix.jcr.RepositoryInitializer;
import brix.plugin.article.articlenode.ArticleNode;
import brix.plugin.article.articlenode.ArticleNodePlugin;
import brix.plugin.article.articlenode.ArticleNodeTreeRenderer;
import brix.plugin.article.articlenode.admin.editor.NiceEditArticleEditorFactory;
import brix.plugin.article.articlenode.admin.editor.SimpleArticleEditorFactory;
import brix.plugin.article.web.tile.article.ArticleDetailTile;
import brix.plugin.article.web.tile.article.ArticleListTile;
import brix.plugin.article.web.tile.message.GuestBookTile;
import brix.plugin.file.FolderNode;
import brix.plugin.file.admin.folder.FolderNodePlugin;
import brix.plugin.hierarchical.HierarchicalNodePlugin;
import brix.plugin.hierarchical.HierarchicalPluginLocator;
import brix.plugin.hierarchical.HierarchicalRepoInitializer;
import brix.plugin.hierarchical.admin.NodeEditorPlugin;
import brix.plugin.site.ManageNodeTabFactory;
import brix.plugin.site.NodeTreeRenderer;
import brix.plugin.site.page.tile.Tile;
import brix.registry.ExtensionPoint;
import brix.registry.ExtensionPointRegistry;
import brix.workspace.Workspace;

/**
 * @author wickeria at gmail.com
 */
public class ArticlePlugin extends HierarchicalNodePlugin {

	public static final String ID = ArticlePlugin.class.getName();
	public static final String ROOT_NODE_NAME = "Articles";

	private static final ExtensionPoint<NodeEditorPlugin> NEP_POINT = new ExtensionPoint<NodeEditorPlugin>() {
		public brix.registry.ExtensionPoint.Multiplicity getMultiplicity() {
			return Multiplicity.COLLECTION;
		}

		public String getUuid() {
			return ArticlePlugin.class.getName() + ":NodeEditorPluginExtensionPoint";
		}
	};

	private static final ExtensionPoint<ManageNodeTabFactory> MNTF_POINT = new ExtensionPoint<ManageNodeTabFactory>() {

		public brix.registry.ExtensionPoint.Multiplicity getMultiplicity() {
			return Multiplicity.COLLECTION;
		}

		public String getUuid() {
			return ArticlePlugin.class.getName() + ":ManageNodeTabFactoryExtensionPoint";
		}

	};

	public ArticlePlugin(Brix brix) {
		super(brix);
		ExtensionPointRegistry registry = brix.getConfig().getRegistry();
		registry.register(RepositoryInitializer.POINT, new HierarchicalRepoInitializer());
		registry.register(JcrNodeWrapperFactory.POINT, ArticleNode.FACTORY);
		registry.register(JcrNodeWrapperFactory.POINT, FolderNode.FACTORY(getPluginLocator()));
		registry.register(NEP_POINT, new FolderNodePlugin(getPluginLocator()));
		registry.register(NEP_POINT, new ArticleNodePlugin(getPluginLocator()));
		registry.register(MNTF_POINT, new ArticleManageNodeTabFactory(getPluginLocator()));
		registry.register(SimpleArticleEditorFactory.POINT, new SimpleArticleEditorFactory());
		registry.register(NiceEditArticleEditorFactory.POINT, new NiceEditArticleEditorFactory());
		registry.register(NodeTreeRenderer.POINT, new ArticleNodeTreeRenderer());

		registry.register(Tile.POINT, new GuestBookTile());
		registry.register(Tile.POINT, new ArticleListTile());
		registry.register(Tile.POINT, new ArticleDetailTile());

	}

	@Override
	protected ExtensionPoint<? extends NodeEditorPlugin> getNodeEditorPluginExtensionPoint() {
		return NEP_POINT;
	}

	@Override
	protected ExtensionPoint<ManageNodeTabFactory> getManageNodeTabFactoryExtensionPoint() {
		return MNTF_POINT;
	}

	public static ArticlePlugin get(Brix brix) {
		return (ArticlePlugin) brix.getPlugin(ID);
	}

	public static ArticlePlugin get() {
		return get(Brix.get());
	}

	@Override
	protected HierarchicalPluginLocator getPluginLocator() {
		return new ArticlePluginLocator();
	}

	@Override
	protected String getRootNodeName() {
		return ROOT_NODE_NAME;
	}

	@Override
	protected IModel<String> getTabName() {
		return new ResourceModel("articles");
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
