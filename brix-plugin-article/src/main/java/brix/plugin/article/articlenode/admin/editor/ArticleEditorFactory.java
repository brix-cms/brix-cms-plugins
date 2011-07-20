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

import org.brixcms.registry.ExtensionPoint;

/**
 * A factory that can create an editor to edit markup. The user can then chose
 * from the list of available editors when editing markup throught he web
 * interface.
 * 
 * @author wickeria at gmail.com
 * 
 */
public interface ArticleEditorFactory {
	/**
	 * Extension point used to register repository initializers
	 */
	public static final ExtensionPoint<ArticleEditorFactory> POINT = new ExtensionPoint<ArticleEditorFactory>() {

		public Multiplicity getMultiplicity() {
			return Multiplicity.COLLECTION;
		}

		public String getUuid() {
			return ArticleEditorFactory.class.getName();
		}

	};

	/**
	 * Create the panel that will represent the editor
	 * 
	 * @param id
	 *            component id
	 * @param markup
	 *            markup model
	 * @return editor component
	 */
	Panel newEditor(String id, IModel<String> markup);

	/**
	 * Create a model that will display the editor name in the menu
	 * 
	 * @return
	 */
	String newStringLabelKey();
}
