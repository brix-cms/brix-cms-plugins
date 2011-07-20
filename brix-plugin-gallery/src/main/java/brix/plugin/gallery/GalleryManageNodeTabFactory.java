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

import brix.plugin.gallery.album.admin.AlbumViewTab;
import brix.plugin.gallery.photo.admin.PhotoViewTab;
import brix.plugin.hierarchical.HierarchicalPluginLocator;
import brix.plugin.hierarchical.folder.ListFolderNodesTab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.auth.Action.Context;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.ManageNodeTabFactory;
import org.brixcms.registry.ExtensionPoint;
import org.brixcms.web.tab.CachingAbstractTab;
import org.brixcms.web.tab.IBrixTab;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wickeria at gmail.com
 */
public class GalleryManageNodeTabFactory implements ManageNodeTabFactory {

	private final HierarchicalPluginLocator pluginLocator;

	public static final ExtensionPoint<GalleryManageNodeTabFactory> POINT = new ExtensionPoint<GalleryManageNodeTabFactory>() {
		public org.brixcms.registry.ExtensionPoint.Multiplicity getMultiplicity() {
			return Multiplicity.COLLECTION;
		}

		public String getUuid() {
			return GalleryManageNodeTabFactory.class.getName();
		}
	};

	public GalleryManageNodeTabFactory(HierarchicalPluginLocator pluginLocator) {
		this.pluginLocator = pluginLocator;
	}

	public List<IBrixTab> getManageNodeTabs(IModel<BrixNode> nodeModel) {
		if (nodeModel.getObject().isFolder()) {
			return getFolderTabs(nodeModel, pluginLocator);
		} else {
			return getImageTabs(nodeModel, pluginLocator);
		}
	}

	public static List<IBrixTab> getFolderTabs(final IModel<BrixNode> folderModel,
			final HierarchicalPluginLocator pluginLocator) {
		List<IBrixTab> tabs = new ArrayList<IBrixTab>(2);
		IBrixTab listingTab = new CachingAbstractTab(new ResourceModel("listing", "Listing"), 100) {

			private static final long serialVersionUID = 1L;

			@Override
			public Panel newPanel(String panelId) {
				return new ListFolderNodesTab(panelId, folderModel, pluginLocator);
			}

			@Override
			public boolean isVisible() {
				return pluginLocator.getPlugin().canViewNodeChildren(folderModel.getObject(), Context.ADMINISTRATION);
			}

		};
		IBrixTab previewTab = new CachingAbstractTab(new ResourceModel("view", "View")) {

			private static final long serialVersionUID = 1L;

			@Override
			public Panel newPanel(String panelId) {
				return new AlbumViewTab(panelId, folderModel);
			}

			@Override
			public boolean isVisible() {
				return pluginLocator.getPlugin().canViewNodeChildren(folderModel.getObject(), Context.ADMINISTRATION);
			}

		};
		tabs.add(listingTab);
		tabs.add(previewTab);
		return tabs;
	}

	public static List<IBrixTab> getImageTabs(final IModel<BrixNode> fileModel,
			final HierarchicalPluginLocator pluginLocator) {
		List<IBrixTab> tabs = new ArrayList<IBrixTab>(1);
		IBrixTab previewTab = new CachingAbstractTab(new ResourceModel("view", "View")) {

			private static final long serialVersionUID = 1L;

			@Override
			public Panel newPanel(String panelId) {
				return new PhotoViewTab(panelId, fileModel);
			}

			@Override
			public boolean isVisible() {
				return pluginLocator.getPlugin().canViewNodeChildren(fileModel.getObject(), Context.ADMINISTRATION);
			}

		};
		tabs.add(previewTab);
		return tabs;
	}

}
