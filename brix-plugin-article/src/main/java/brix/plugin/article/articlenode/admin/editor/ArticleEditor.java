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

import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import org.brixcms.web.model.ModelBuffer;

/**
 * @author wickeria at gmail.com
 */
public class ArticleEditor extends Panel {

	private static final long serialVersionUID = 1L;

	public ArticleEditor(String id, ModelBuffer model) {
		super(id);

		// content field
		IModel<String> contentModel = model.forProperty("dataAsString");
		add(new TextArea<String>("content", contentModel));
	}

}
