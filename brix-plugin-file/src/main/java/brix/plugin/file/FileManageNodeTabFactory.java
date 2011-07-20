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

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import org.brixcms.auth.Action.Context;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.jcr.wrapper.ResourceNode;
import brix.plugin.file.admin.resource.ResourcePreviewPanel;
import brix.plugin.hierarchical.HierarchicalPluginLocator;
import brix.plugin.hierarchical.folder.ListFolderNodesTab;
import org.brixcms.plugin.site.ManageNodeTabFactory;
import org.brixcms.registry.ExtensionPoint;
import org.brixcms.web.tab.CachingAbstractTab;
import org.brixcms.web.tab.IBrixTab;

/**
 * @author wickeria at gmail.com
 */
public class FileManageNodeTabFactory implements ManageNodeTabFactory {

	private final HierarchicalPluginLocator pluginLocator;

	public static final ExtensionPoint<FileManageNodeTabFactory> POINT = new ExtensionPoint<FileManageNodeTabFactory>() {
		public brix.registry.ExtensionPoint.Multiplicity getMultiplicity() {
			return Multiplicity.COLLECTION;
		}

		public String getUuid() {
			return FileManageNodeTabFactory.class.getName();
		}
	};

	public FileManageNodeTabFactory(HierarchicalPluginLocator pluginLocator) {
		this.pluginLocator = pluginLocator;
	}

	public List<IBrixTab> getManageNodeTabs(IModel<BrixNode> nodeModel) {
		List<IBrixTab> result = new ArrayList<IBrixTab>();

		BrixNode node = nodeModel.getObject();

		if (node.isFolder()) {
			return getFolderTabs(nodeModel, pluginLocator);
		} else {
			if (node instanceof ResourceNode) {
				return getResourceTabs(nodeModel, pluginLocator);
			}
		}
		return result;
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
		tabs.add(listingTab);
		return tabs;
	}

	public static List<IBrixTab> getResourceTabs(final IModel<BrixNode> fileModel,
			final HierarchicalPluginLocator pluginLocator) {
		List<IBrixTab> tabs = new ArrayList<IBrixTab>(1);
		IBrixTab previewTab = new CachingAbstractTab(new ResourceModel("view", "View")) {

			private static final long serialVersionUID = 1L;

			@Override
			public Panel newPanel(String panelId) {
				return new ResourcePreviewPanel(panelId, fileModel);
			}

			@Override
			public boolean isVisible() {
				return pluginLocator.getPlugin().canViewNode(fileModel.getObject(), Context.ADMINISTRATION);
			}

		};
		tabs.add(previewTab);
		return tabs;
	}

}
