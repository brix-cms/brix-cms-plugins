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

package brix.plugin.file.admin.add.upload;

import java.io.InputStream;
import java.util.Arrays;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.MinimumValidator;

import brix.plugin.file.util.ImageInfo;
import brix.plugin.file.util.ImageMagicProcessor;

/**
 * @author wickeria at gmail.com
 */
public abstract class UploadPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private final UploadFileConfiguration configuration;

	public UploadPanel(String id, UploadFileConfiguration configuration) {
		super(id);
		this.configuration = configuration;
	}

	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
		if (!hasBeenRendered()) {
			init();
		}
	}

	private void init() {
		Form<Void> form = new Form<Void>("form");
		add(form);
		form.setMultiPart(true);
		final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
		feedbackPanel.setOutputMarkupId(true);
		form.add(feedbackPanel);
		final FileUploadField fileUploadField = new FileUploadField("fileUploadField");
		fileUploadField.setRequired(true);
		form.add(fileUploadField);
		final IModel<Integer> resultSizeModel = new Model<Integer>(configuration.getResultImageMaxSize());
		final TextField<Integer> resultSize = new TextField<Integer>("resultSize", resultSizeModel, Integer.class) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return configuration.isAllowResultImageMaxSizeEdit();
			};
		};
		form.add(resultSize.setRequired(true).add(new MinimumValidator<Integer>(30)));
		form.add(new AjaxSubmitLink("ok") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				try {
					FileUpload fileUpload = fileUploadField.getFileUpload();
					InputStream is = fileUpload.getInputStream();
					String mime = fileUpload.getContentType();
					if (Arrays.asList(ImageInfo.MIME_TYPE_STRINGS).contains(mime)) {
						Integer resultSize = resultSizeModel.getObject();
						if (resultSize != null) {
							is = ImageMagicProcessor.createThumbnail(is, resultSize, resultSize, false);
						}
					}
					saveFile(is, fileUpload.getClientFileName(), mime, target);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.addComponent(feedbackPanel);
			}

		});
		form.add(new AjaxLink<Void>("cancel") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				onCancel(target);
			}

		});
	}

	protected void onCancel(AjaxRequestTarget target) {

	}

	protected abstract void saveFile(InputStream is, String fileName, String mimeType, AjaxRequestTarget target);

}
