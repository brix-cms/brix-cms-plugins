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

package brix.plugin.hierarchical.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import org.brixcms.jcr.wrapper.BrixNode;
import brix.plugin.hierarchical.HierarchicalPluginLocator;

public class NodeEditorPluginEntriesModel
		extends
			LoadableDetachableModel<List<NodeEditorPluginEntry>>
{

	private final HierarchicalPluginLocator pluginLocator;
	private final IModel<BrixNode> parentNode;

	public NodeEditorPluginEntriesModel(HierarchicalPluginLocator pluginLocator,
			IModel<BrixNode> parentNode)
	{
		this.parentNode = parentNode;
		this.pluginLocator = pluginLocator;
	}

	@Override
	protected List<NodeEditorPluginEntry> load()
	{
		return convert(pluginLocator.getPlugin().getNodeEditorPlugins());
	}

	private List<NodeEditorPluginEntry> convert(
			Collection<? extends NodeEditorPlugin> nodeEditorPlugins)
	{
		List<NodeEditorPluginEntry> list = new ArrayList<NodeEditorPluginEntry>();
		for (NodeEditorPlugin plugin : nodeEditorPlugins)
		{
			if (plugin.newCreateNodeCaptionModel(parentNode) != null)
			{
				list.add(new NodeEditorPluginEntry(plugin, pluginLocator));
			}
		}
		return list;
	}


}
