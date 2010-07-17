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

package brix.demo.web;

import javax.jcr.Session;

import brix.Brix;
import brix.Plugin;
import brix.auth.AuthorizationStrategy;
import brix.config.BrixConfig;
import brix.jcr.api.JcrSession;
import brix.plugin.article.ArticlePlugin;
import brix.plugin.article.web.tile.article.ArticleDetailTile;
import brix.plugin.article.web.tile.article.ArticleListTile;
import brix.plugin.article.web.tile.message.GuestBookTile;
import brix.plugin.file.FilePlugin;
import brix.plugin.gallery.GalleryPlugin;
import brix.plugin.gallery.web.tile.GalleryTile;
import brix.plugin.menu.MenuPlugin;
import brix.plugin.prototype.PrototypePlugin;
import brix.plugin.site.page.tile.Tile;
import brix.plugin.snapshot.SnapshotPlugin;
import brix.plugin.webdavurl.WebdavUrlPlugin;

/**
 * Subclass of {@link Brix} that configures demo-specific settings such as
 * plugins, tiles, etc.
 * 
 * @author igor.vaynberg
 * 
 */
public class DemoBrix extends Brix {
	/**
	 * Constructor
	 * 
	 * @param config
	 */
	public DemoBrix(BrixConfig config) {
		super(config);

		config.getRegistry().register(Tile.POINT, new GuestBookTile());

		// register plugins
		config.getRegistry().register(Plugin.POINT, new MenuPlugin(this));
		config.getRegistry().register(Plugin.POINT, new SnapshotPlugin(this));
		config.getRegistry().register(Plugin.POINT, new PrototypePlugin(this));
		config.getRegistry().register(Plugin.POINT, new WebdavUrlPlugin());
		config.getRegistry().register(Plugin.POINT, new FilePlugin(this));
		config.getRegistry().register(Plugin.POINT, new ArticlePlugin(this));
		config.getRegistry().register(Plugin.POINT, new GalleryPlugin(this));

		// register tiles

		config.getRegistry().register(Tile.POINT, new GuestBookTile());
		config.getRegistry().register(Tile.POINT, new ArticleListTile());
		config.getRegistry().register(Tile.POINT, new ArticleDetailTile());
		config.getRegistry().register(Tile.POINT, new GalleryTile());
	}

	/** {@inheritDoc} */
	@Override
	public AuthorizationStrategy newAuthorizationStrategy() {
		// register our simple demo auth strategy
		return new DemoAuthorizationStrategy();
	}

	@Override
	public JcrSession getCurrentSession(String workspace) {
		Session session = getConfig().getSessionFactory().getCurrentSession(workspace);
		if (GalleryPlugin.get().isGalleryWorkspace(getWorkspaceManager().getWorkspace(workspace))) {
			GalleryPlugin.get().addGalleryEventListener(session, getConfig().getSessionFactory());
		}
		return wrapSession(session);
	}

}
