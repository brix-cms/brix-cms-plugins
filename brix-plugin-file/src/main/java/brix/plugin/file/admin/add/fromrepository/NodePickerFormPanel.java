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

package brix.plugin.file.admin.add.fromrepository;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.plugin.file.FilePlugin;
import brix.plugin.hierarchical.admin.HierarchicalNodeManagerPanel;
import brix.web.generic.BrixGenericPanel;
import brix.web.picker.common.TreeAwareNode;
import brix.web.picker.node.FileNodeFilter;
import brix.web.picker.node.NodePicker;

/**
 * @author wickeria at gmail.com
 */
public class NodePickerFormPanel extends BrixGenericPanel<BrixNode> {

	private static final long serialVersionUID = 1L;

	public NodePickerFormPanel(String id, IModel<BrixNode> model, String workspace) {
		super(id, model);
		Form<Void> form = new Form<Void>("form");
		form.add(new NodePicker("nodePicker", getModel(), TreeAwareNode.Util.getTreeNode(FilePlugin.get().getRootNode(
				workspace)), HierarchicalNodeManagerPanel.SHOW_ALL_NON_NULL_NODES_FILTER, FileNodeFilter.INSTANCE));
		add(form);
		add(new AjaxLink<Void>("cancel") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				onCancel(target);
			}
		});
		add(new AjaxLink<Void>("ok") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				onOk(target);
			}
		});

	}

	protected void onCancel(AjaxRequestTarget target) {

	}

	protected void onOk(AjaxRequestTarget target) {

	}

}
