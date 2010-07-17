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

package brix.plugin.file.admin.resource;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Bytes;

import brix.jcr.wrapper.BrixFileNode;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.file.util.FilePluginUtils;
import brix.plugin.site.resource.ResourceRequestTarget;
import brix.web.generic.BrixGenericPanel;

/**
 * @author wickeria at gmail.com
 */
public class ResourcePreviewPanel extends BrixGenericPanel<BrixNode> {

	private static final long serialVersionUID = 1L;

	public ResourcePreviewPanel(String id, final IModel<BrixNode> model) {
		super(id, model);
		add(new Link<Void>("download") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				getRequestCycle().setRequestTarget(new ResourceRequestTarget(model, true));
			}
		});
		add(new Label("tag", FilePluginUtils.getResourceTag(model.getObject(), this)));
		add(new Label("size", new Model<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				return Bytes.bytes(getFileNode().getContentLength()).toString() + " bytes";
			}
		}));
		add(new Label("resolution", new Model<String>() {

			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				return FilePluginUtils.getResolution(getFileNode());
			}
		}) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return FilePluginUtils.isImage(getFileNode());
			}
		});

		add(new WebMarkupContainer("img") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.put("src", urlFor(new ResourceReference("file")) + "?ws="
						+ getModelObject().getSession().getWorkspace().getName() + "&id="
						+ getModelObject().getIdentifier());
			}

			@Override
			public boolean isVisible() {
				return FilePluginUtils.isImage(getFileNode());
			}

		});

	}

	private BrixFileNode getFileNode() {
		return (BrixFileNode) getModelObject();
	}

}
