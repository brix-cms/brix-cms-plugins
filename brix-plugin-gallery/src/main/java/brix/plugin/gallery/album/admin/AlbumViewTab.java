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

package brix.plugin.gallery.album.admin;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import org.brixcms.auth.Action.Context;
import org.brixcms.jcr.wrapper.BrixNode;
import brix.plugin.file.util.FilePluginUtils;
import brix.plugin.gallery.GalleryPlugin;
import org.brixcms.web.generic.BrixGenericPanel;

/**
 * @author wickeria at gmail.com
 */
public class AlbumViewTab extends BrixGenericPanel<BrixNode> {

	private static final long serialVersionUID = 1L;

	public AlbumViewTab(String id, final IModel<BrixNode> model) {
		super(id, model);

		add(new Label("title", new PropertyModel<String>(model, "title")));
		add(new NonCachingImage("img", new ResourceReference("file"), FilePluginUtils
				.getResourceParameters(AlbumViewTab.this.getModel().getObject())));
		add(new Link<Void>("edit") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				AlbumEditTab edit = new AlbumEditTab(AlbumViewTab.this.getId(), AlbumViewTab.this.getModel()) {
					private static final long serialVersionUID = 1L;

					@Override
					void goBack() {
						replaceWith(AlbumViewTab.this);
					}
				};
				AlbumViewTab.this.replaceWith(edit);
			}

			@Override
			public boolean isVisible() {
				BrixNode node = AlbumViewTab.this.getModelObject();
				return GalleryPlugin.get().canEditNode(node, Context.ADMINISTRATION);
			}
		});
	}

}
