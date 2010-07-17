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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.ResourceModel;

/**
 * @author wickeria at gmail.com
 */
public abstract class UploadFileModalWindow extends ModalWindow {

	private static final long serialVersionUID = 1L;

	private final UploadFileConfiguration configuration;

	public UploadFileModalWindow(String id, UploadFileConfiguration configuration) {
		super(id);
		this.configuration = configuration;
		setWidthUnit("em");
		setInitialWidth(40);
		setUseInitialHeight(false);
		setResizable(false);
		setCookieName("upload-file");
		setTitle(new ResourceModel("title"));
	}

	private void initContent() {
		setContent(new UploadPanel(getContentId(), configuration) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void saveFile(InputStream is, String fileName, String mimeType, AjaxRequestTarget target) {
				UploadFileModalWindow.this.saveFile(is, fileName, mimeType, target);
				close(target);
			}

			@Override
			protected void onCancel(AjaxRequestTarget target) {
				UploadFileModalWindow.this.onCancel(target);
			}

		});
	}

	@Override
	public void show(AjaxRequestTarget target) {
		if (isShown() == false) {
			initContent();
		}
		super.show(target);
	}

	protected void onCancel(AjaxRequestTarget target) {
		close(target);
	}

	protected abstract void saveFile(InputStream is, String fileName, String mimeType, AjaxRequestTarget target);

}
