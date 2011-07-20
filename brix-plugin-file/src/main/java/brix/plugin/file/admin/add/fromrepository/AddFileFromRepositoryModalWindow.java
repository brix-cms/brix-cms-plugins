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

package brix.plugin.file.admin.add.fromrepository;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.web.generic.IGenericComponent;

/**
 * @author wickeria at gmail.com
 */
public class AddFileFromRepositoryModalWindow extends ModalWindow implements IGenericComponent<BrixNode> {

	private static final long serialVersionUID = 1L;
	private final String workspace;

	public AddFileFromRepositoryModalWindow(String id, IModel<BrixNode> model, String workspace) {
		super(id);
		setModel(model);
		setWidthUnit("em");
		setInitialWidth(64);
		setUseInitialHeight(false);
		setResizable(false);
		setCookieName("add-file-from-repository");
		setTitle(new ResourceModel("title"));
		this.workspace = workspace;
	}

	private void initContent() {
		setContent(new NodePickerFormPanel(getContentId(), getModel(), workspace) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onOk(AjaxRequestTarget target) {
				AddFileFromRepositoryModalWindow.this.onOk(target);
			}

			@Override
			protected void onCancel(AjaxRequestTarget target) {
				AddFileFromRepositoryModalWindow.this.onCancel(target);
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

	protected void onOk(AjaxRequestTarget target) {
		close(target);
	}

	@SuppressWarnings("unchecked")
	@Override
	public IModel<BrixNode> getModel() {
		return (IModel<BrixNode>) getDefaultModel();
	}

	@Override
	public BrixNode getModelObject() {
		return (BrixNode) getDefaultModelObject();
	}

	@Override
	public void setModel(IModel<BrixNode> model) {
		setDefaultModel(model);
	}

	@Override
	public void setModelObject(BrixNode object) {
		setDefaultModelObject(object);
	}

}
