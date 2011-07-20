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

import java.awt.Rectangle;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.jackrabbit.value.BinaryImpl;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.wicketstuff.yui.markup.html.cropp.CropImageModalWindow;
import org.wicketstuff.yui.markup.html.cropp.YuiImageCropperSettings;

import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.wrapper.BrixNode;
import brix.plugin.file.util.FilePluginUtils;
import brix.plugin.file.util.ImageMagicProcessor;
import brix.plugin.gallery.GalleryPlugin;
import brix.plugin.gallery.album.AlbumFolderNode;
import org.brixcms.plugin.site.admin.NodeManagerPanel;
import org.brixcms.web.ContainerFeedbackPanel;
import org.brixcms.web.model.ModelBuffer;

/**
 * @author wickeria at gmail.com
 */
abstract class AlbumEditTab extends NodeManagerPanel {

	private static final long serialVersionUID = 1L;

	public AlbumEditTab(String id, final IModel<BrixNode> nodeModel) {
		super(id, nodeModel);

		setOutputMarkupId(true);
		Form<Void> form = new Form<Void>("form");
		add(form);

		final ModelBuffer adapter = new ModelBuffer(nodeModel);
		IModel<String> titleModel = adapter.forProperty("title");

		form.add(new TextField<String>("title", titleModel).setRequired(true));

		form.add(new ContainerFeedbackPanel("feedback", this));
		form.add(new NonCachingImage("img", new ResourceReference("file"), FilePluginUtils
				.getResourceParameters(AlbumEditTab.this.getModel().getObject())));

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

		final CropImageModalWindow modal = new CropImageModalWindow("modal", YuiImageCropperSettings.getDefault(true,
				GalleryPlugin.ALBUM_FOLDER_PREV_IMG_SIZE, GalleryPlugin.ALBUM_FOLDER_PREV_IMG_SIZE, false)) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onCropImage(InputStream is, String fileName, String mimeType, AjaxRequestTarget target) {
				AlbumFolderNode folderNode = (AlbumFolderNode) AlbumEditTab.this.getDefaultModelObject();
				try {
					folderNode.setPreviewImage(new BinaryImpl(is));
					folderNode.getSession().save();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				target.addComponent(AlbumEditTab.this);
			}

			@Override
			protected InputStream createCrop(FileInputStream fileInputStream, Rectangle rectangle) {
				return ImageMagicProcessor.createCrop(fileInputStream, rectangle);
			}

			@Override
			protected InputStream createThumbnail(InputStream is, int resultImageWidth, int resultImageHeight,
					boolean ratio) {

				try {
					return ImageMagicProcessor.createThumbnail(is, resultImageWidth, resultImageHeight, ratio);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		};
		add(modal);
		AjaxLink<Void> changePrevLink = new AjaxLink<Void>("changePrevLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				modal.show(target);
			}
		};
		form.add(changePrevLink);

	}

	abstract void goBack();

}