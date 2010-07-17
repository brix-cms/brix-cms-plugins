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

package brix.plugin.gallery.photo.admin;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import brix.auth.Action.Context;
import brix.jcr.api.JcrNode;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.file.util.FilePluginUtils;
import brix.plugin.gallery.GalleryPlugin;
import brix.plugin.gallery.GalleryPlugin.FOLDERS;
import brix.plugin.gallery.photo.PhotoNode;
import brix.web.generic.BrixGenericPanel;

/**
 * @author wickeria at gmail.com
 */
public class PhotoViewTab extends BrixGenericPanel<BrixNode> {

	private static final long serialVersionUID = 1L;

	public PhotoViewTab(String id, final IModel<BrixNode> model) {
		super(id, model);

		add(new Label("title", new PropertyModel<String>(model, "title")));
		add(new WebMarkupContainer("img") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.put("src", urlFor(new ResourceReference("file"))
						+ FilePluginUtils.getResourceURLParameters(getNode(FOLDERS.V_768)));
			}
		});

		add(new Link<Void>("edit") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				PhotoEditTab edit = new PhotoEditTab(PhotoViewTab.this.getId(), PhotoViewTab.this.getModel()) {
					private static final long serialVersionUID = 1L;

					@Override
					void goBack() {
						replaceWith(PhotoViewTab.this);
					}
				};
				PhotoViewTab.this.replaceWith(edit);
			}

			@Override
			public boolean isVisible() {
				BrixNode node = PhotoViewTab.this.getModelObject();
				return GalleryPlugin.get().canEditNode(node, Context.ADMINISTRATION);
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

}
