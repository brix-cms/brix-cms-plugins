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

import org.apache.wicket.ResourceReference;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.IModel;

import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.wrapper.BrixNode;
import brix.plugin.file.util.FilePluginUtils;
import brix.plugin.gallery.GalleryPlugin;
import brix.plugin.gallery.GalleryPlugin.FOLDERS;
import brix.plugin.gallery.photo.PhotoNode;
import org.brixcms.web.generic.BrixGenericPanel;

import com.visural.wicket.component.fancybox.Fancybox;
import com.visural.wicket.component.fancybox.FancyboxGroup;
import com.visural.wicket.util.images.ImageReferenceFactory;

/**
 * @author wickeria at gmail.com
 */
public class PhotoPanel extends BrixGenericPanel<BrixNode> {

	private static final long serialVersionUID = 1L;

	public PhotoPanel(String id, IModel<BrixNode> model, final FancyboxGroup group, int index, int size) {
		super(id, model);
		add(new Label("title", getOrigPhotoNode().getTitle()));
		final Fancybox fancybox = new Fancybox("v1", ImageReferenceFactory.fromURL(""
				+ urlFor(new ResourceReference("file"))
				+ FilePluginUtils.getResourceURLParameters(getPhotoNode(FOLDERS.V_768)))).setGroup(group)
				.setBoxTitle(createTitle(index, size));
		add(fancybox);
		WebMarkupContainer link = new WebMarkupContainer("link");
		link.add(new SimpleAttributeModifier("onClick", "$('#" + fancybox.getMarkupId() + "').click();"));

		link.add(new Image("img", new ResourceReference("file"), FilePluginUtils
				.getResourceParameters(getModelObject())).add(new SimpleAttributeModifier("title",
				getOrigPhotoNode().getTitle())));
		add(link);

		add(new WebMarkupContainer("orig").add(new SimpleAttributeModifier("href",
				urlFor(new ResourceReference("file"))
						+ FilePluginUtils.getResourceURLParameters(getPhotoNode(FOLDERS.V_768)))));

	}

	private String createTitle(int index, int size) {
		return getOrigPhotoNode().getTitle() + " (" + (index + 1) + "/" + size + ")";
	}

	private JcrNode getPhotoNode(GalleryPlugin.FOLDERS folder) {
		return GalleryPlugin.getGallerySession().getNode(
				getModelObject().getPath().replace(GalleryPlugin.FOLDERS.V_96.name(), folder.name()));
	}

	private PhotoNode getOrigPhotoNode() {
		return (PhotoNode) GalleryPlugin.getGallerySession().getNode(
				getModelObject().getPath().replace(GalleryPlugin.FOLDERS.V_96.name() + "/", ""));
	}
}
