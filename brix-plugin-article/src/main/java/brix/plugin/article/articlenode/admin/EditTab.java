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
package brix.plugin.article.articlenode.admin;

import java.awt.Rectangle;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.wicketstuff.yui.markup.html.cropp.CropImageModalWindow;
import org.wicketstuff.yui.markup.html.cropp.YuiImageCropperSettings;

import brix.Brix;
import brix.BrixNodeModel;
import brix.jcr.api.JcrNode;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.article.articlenode.ArticleNode;
import brix.plugin.article.articlenode.admin.editor.ArticleEditorFactory;
import brix.plugin.article.articlenode.admin.file.ArticleFilesPanel;
import brix.plugin.article.util.ArticlePluginUtils;
import brix.plugin.file.admin.add.fromrepository.AddFileFromRepositoryModalWindow;
import brix.plugin.file.admin.add.upload.UploadFileConfiguration;
import brix.plugin.file.admin.add.upload.UploadFileModalWindow;
import brix.plugin.file.util.ImageMagicProcessor;
import brix.plugin.site.admin.NodeManagerPanel;
import brix.web.ContainerFeedbackPanel;
import brix.web.model.ModelBuffer;

/**
 * @author wickeria at gmail.com
 */
abstract class EditTab extends NodeManagerPanel {
	private static final long serialVersionUID = 1L;
	private String currentEditorFactory;
	private final MarkupContainer contentEditorParent;
	private final IModel<String> contentEditorModel;

	public EditTab(String id, final IModel<BrixNode> nodeModel) {
		super(id, nodeModel);

		Brix brix = getModelObject().getBrix();
		Form<Void> form = new Form<Void>("form");
		add(form);

		final ModelBuffer adapter = new ModelBuffer(nodeModel);
		IModel<String> titleModel = adapter.forProperty("title");
		IModel<Boolean> allowDiscussionModel = adapter.forProperty("allowDiscussion");
		IModel<Date> publishedModel = adapter.forProperty("published");
		IModel<String> authordModel = adapter.forProperty("author");

		form.add(new TextField<String>("title", titleModel).setRequired(true));
		form.add(new TextField<String>("author", authordModel).setRequired(true));
		form.add(new CheckBox("allowDiscussion", allowDiscussionModel));
		form.add(new DateTextField("published", publishedModel).setRequired(true).add(new DatePicker()));

		final ArticleFilesPanel articlePicturesPanel = new ArticleFilesPanel("files", nodeModel);

		form.add(articlePicturesPanel);

		// add file from repository
		final IModel<BrixNode> fileModel = new BrixNodeModel();
		String workspace = nodeModel.getObject().getSession().getWorkspace().getName();
		final AddFileFromRepositoryModalWindow addFileFromRepository = new AddFileFromRepositoryModalWindow(
				"addFileFromRepositoryModal", fileModel, workspace) {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("deprecation")
			@Override
			protected void onOk(AjaxRequestTarget target) {
				super.onOk(target);
				BrixNode brixNode = fileModel.getObject();
				if (brixNode != null) {
					ArticleNode articleNode = (ArticleNode) EditTab.this.getDefaultModelObject();
					articleNode.addFile(brixNode.getIdentifier());
					articleNode.save();
					target.addComponent(articlePicturesPanel);
				}
			};
		};
		add(addFileFromRepository);
		AjaxLink<Void> addFileFromRepositoryShowLink = new AjaxLink<Void>("addFileFromRepositoryShowLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				addFileFromRepository.show(target);
			}
		};
		form.add(addFileFromRepositoryShowLink);

		// upload file
		final UploadFileModalWindow uploadFileModalWindow = new UploadFileModalWindow("uploadWindow",
				new UploadFileConfiguration(1024, true)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void saveFile(InputStream is, String fileName, String mimeType, AjaxRequestTarget target) {
				ArticleNode articleNode = (ArticleNode) EditTab.this.getDefaultModelObject();
				ArticlePluginUtils.saveArticleFile(articleNode, fileName, mimeType, is);
				target.addComponent(articlePicturesPanel);
			}
		};
		add(uploadFileModalWindow);
		AjaxLink<Void> uploadShowLink = new AjaxLink<Void>("uploadShowLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				uploadFileModalWindow.show(target);
			}
		};
		form.add(uploadShowLink);

		// upload cropp image
		final CropImageModalWindow cropImageModalWindow = new CropImageModalWindow("cropImageModalWindow",
				YuiImageCropperSettings.getDefault(false, 300, 300, true)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onCropImage(InputStream is, String fileName, String mimeType, AjaxRequestTarget target) {
				ArticleNode articleNode = (ArticleNode) EditTab.this.getDefaultModelObject();
				ArticlePluginUtils.saveArticleFile(articleNode, fileName, mimeType, is);
				target.addComponent(articlePicturesPanel);
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
		add(cropImageModalWindow);
		AjaxLink<Void> cropImageModalShowLink = new AjaxLink<Void>("cropImageModalShowLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				cropImageModalWindow.show(target);
			}
		};
		form.add(cropImageModalShowLink);

		contentEditorModel = adapter.forProperty("dataAsString");
		contentEditorParent = form;

		Collection<ArticleEditorFactory> editorFactories = brix.getConfig().getRegistry().lookupCollection(
				ArticleEditorFactory.POINT);

		setupEditor(editorFactories.iterator().next().getClass().getName());

		// set up buttons to control editor switching

		RepeatingView editors = new RepeatingView("editors") {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return size() > 1;
			}
		};
		form.add(editors);

		for (ArticleEditorFactory factory : editorFactories) {
			final String cn = factory.getClass().getName();
			editors.add(new Button(editors.newChildId(), new ResourceModel(factory.newStringLabelKey())) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onSubmit() {
					setupEditor(cn);
				}

				@Override
				public boolean isEnabled() {
					return !cn.equals(currentEditorFactory);
				}
			});
		}

		form.add(new ContainerFeedbackPanel("feedback", this));

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

	abstract void goBack();

	private void setupEditor(String cn) {
		final Brix brix = getModelObject().getBrix();

		Collection<ArticleEditorFactory> factories = brix.getConfig().getRegistry().lookupCollection(
				ArticleEditorFactory.POINT);

		for (ArticleEditorFactory factory : factories) {
			if (factory.getClass().getName().equals(cn)) {
				contentEditorParent.addOrReplace(factory.newEditor("content", contentEditorModel));
				currentEditorFactory = factory.getClass().getName();
				return;
			}
		}

		throw new RuntimeException("Unknown markup editor factory class: " + cn);

	};

}