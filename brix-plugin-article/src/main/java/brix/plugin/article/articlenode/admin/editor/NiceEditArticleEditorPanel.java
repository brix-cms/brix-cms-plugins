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

package brix.plugin.article.articlenode.admin.editor;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.visural.wicket.component.nicedit.Button;
import com.visural.wicket.component.nicedit.RichTextEditor;

/**
 * @author wickeria at gmail.com
 */
public class NiceEditArticleEditorPanel extends Panel {

	private static final long serialVersionUID = 1L;

	public NiceEditArticleEditorPanel(String id, final IModel<String> markup) {
		super(id, markup);
		// Form<?> form = findParent(Form.class);
		// form.add(new RichTextEditorFormBehavior());
		RichTextEditor<String> rte = new RichTextEditor<String>("content", markup) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isButtonEnabled(Button button) {
				switch (button) {
				case upload:
					return false;
				default:
					return true;
				}

			}
		};
		add(rte);
	}
}