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
package brix.plugin.gallery.web.tile;

import java.util.List;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.model.IModel;

import org.brixcms.jcr.wrapper.BrixNode;
import brix.plugin.file.util.FilePluginUtils;
import brix.plugin.gallery.album.AlbumFolderNode;
import org.brixcms.web.generic.BrixGenericPanel;
import org.brixcms.web.nodepage.BrixPageParameters;
import org.brixcms.web.nodepage.PageParametersLink;

/**
 * gallery folders - http://www.flickr.com/galleries/
 */

/**
 * @author wickeria at gmail.com
 */

public class AlbumFolderPanel extends BrixGenericPanel<BrixNode> {

	private static final long serialVersionUID = 1L;

	public AlbumFolderPanel(String id, final IModel<BrixNode> model, final List<String> albumParams) {
		super(id, model);
		setOutputMarkupId(true);
		AlbumFolderNode galleryFolder = (AlbumFolderNode) getModelObject();
		final String title = galleryFolder.getTitle();
		PageParametersLink link1 = new PageParametersLink("link1") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void contributeToPageParameters(BrixPageParameters parameters) {
				int i = 0;
				for (String album : albumParams) {
					parameters.setIndexedParam(i++, album);
				}
				parameters.setIndexedParam(i, model.getObject().getName());
			}
		};
		link1.add(new SimpleAttributeModifier("alt", title));
		link1.add(new SimpleAttributeModifier("title", title));
		add(link1);
		link1.add(new NonCachingImage("img", new ResourceReference("file"), FilePluginUtils
				.getResourceParameters(getModelObject())));
		PageParametersLink link2 = new PageParametersLink("link2") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void contributeToPageParameters(BrixPageParameters parameters) {
				int i = 0;
				for (String album : albumParams) {
					parameters.setIndexedParam(i++, album);
				}
				parameters.setIndexedParam(i, model.getObject().getName());
			}
		};
		link2.add(new Label("title", title));
		link2.add(new SimpleAttributeModifier("title", title));
		add(link2);
	}

}
