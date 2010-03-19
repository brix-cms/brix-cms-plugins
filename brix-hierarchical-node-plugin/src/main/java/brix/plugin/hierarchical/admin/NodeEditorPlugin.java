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

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.SimpleCallback;

public interface NodeEditorPlugin
{

	/**
	 * Returns the node type of nodes that this plugin can handle.
	 * 
	 * @see BrixNode#setNodeType(String)
	 * @return
	 */
	String getNodeType();

	/**
	 * Returns the user readable name of this plugin.
	 * 
	 * @return
	 */
	String getName();

	/**
	 * Returns model caption of Create link for this plugin.
	 * 
	 * @param parentNode
	 * @return
	 */
	public IModel<String> newCreateNodeCaptionModel(IModel<BrixNode> parentNode);

	/**
	 * Returns an instance of panel that should create node of type this plugin
	 * can handle.
	 * 
	 * @param id
	 *            panel component id
	 * @param parentNode
	 *            parent node of the new node
	 * @param goBack
	 *            simple callback that should be invoked after node creation or
	 *            on cancel
	 * @return panel instance
	 */
	Panel newCreateNodePanel(String id, IModel<BrixNode> parentNode, SimpleCallback goBack);
}
