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

/**
 * 
 */
package brix.plugin.gallery.photo.admin;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;

import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.wrapper.BrixNode;
import brix.plugin.file.util.FilePluginUtils;
import brix.plugin.gallery.GalleryPlugin;
import brix.plugin.gallery.GalleryPlugin.FOLDERS;
import brix.plugin.gallery.photo.PhotoNode;
import org.brixcms.plugin.site.admin.NodeManagerPanel;
import org.brixcms.web.ContainerFeedbackPanel;
import org.brixcms.web.model.ModelBuffer;

/**
 * @author wickeria at gmail.com
 */
abstract class PhotoEditTab extends NodeManagerPanel {

	private static final long serialVersionUID = 1L;

	public PhotoEditTab(String id, final IModel<BrixNode> nodeModel) {
		super(id, nodeModel);

		Form<Void> form = new Form<Void>("form");
		add(form);

		final ModelBuffer adapter = new ModelBuffer(nodeModel);
		IModel<String> titleModel = adapter.forProperty("title");

		form.add(new TextField<String>("title", titleModel).setRequired(true));

		form.add(new ContainerFeedbackPanel("feedback", this));

		form.add(new WebMarkupContainer("img") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.put("src", urlFor(new ResourceReference("file"))
						+ FilePluginUtils.getResourceURLParameters(getNode(FOLDERS.V_768)));
			}
		});

		form.add(new Button("save") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				JcrNode node = nodeModel.getObject();
				node.checkout();
				adapter.apply();
				node.save();
				node.checkin();

				getSession().info(getString("status.saved"));
				goBack();
			}
		});

		form.add(new Link<Void>("cancel") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				getSession().info(getString("status.cancelled"));
				goBack();
			}

		});
	}

	private PhotoNode getPhoto() {
		return (PhotoNode) getModelObject();
	}

	private JcrNode getNode(GalleryPlugin.FOLDERS folder) {
		JcrNode parentNode = getPhoto().getParent();
		return GalleryPlugin.getGallerySession().getNode(
				parentNode.getPath() + "/" + folder.name() + "/" + getPhoto().getName());
	}

	abstract void goBack();

}