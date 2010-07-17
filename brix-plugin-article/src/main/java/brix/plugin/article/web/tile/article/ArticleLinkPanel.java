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

package brix.plugin.article.web.tile.article;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import brix.plugin.article.articlenode.ArticleNode;
import brix.web.generic.BrixGenericPanel;
import brix.web.nodepage.BrixPageParameters;
import brix.web.nodepage.PageParametersLink;

/**
 * @author wickeria at gmail.com
 */
public class ArticleLinkPanel extends BrixGenericPanel<ArticleNode> {

	private static final long serialVersionUID = 1L;

	public ArticleLinkPanel(String id, final IModel<ArticleNode> model, boolean selected) {
		super(id, model);
		PageParametersLink link = new PageParametersLink("link") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void contributeToPageParameters(BrixPageParameters parameters) {
				super.contributeToPageParameters(parameters);
				parameters.setIndexedParam(0, model.getObject().getName());
			}
		};
		add(link);
		link.add(new Label("title", new PropertyModel<ArticleNode>(model, "title")));
		if (selected) {
			link.add(new SimpleAttributeModifier("class", "selected-article"));
		}

	}

}
