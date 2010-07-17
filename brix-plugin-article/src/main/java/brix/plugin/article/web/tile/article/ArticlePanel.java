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

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import brix.jcr.wrapper.BrixNode;
import brix.plugin.article.articlenode.ArticleNode;
import brix.plugin.article.web.tile.message.MessagesPanel;
import brix.web.generic.BrixGenericPanel;

/**
 * @author wickeria at gmail.com
 */
public class ArticlePanel extends BrixGenericPanel<BrixNode> {

	private static final long serialVersionUID = 1L;

	public ArticlePanel(String id, IModel<BrixNode> model) {
		super(id, model);
		setOutputMarkupId(true);
		add(new Label("title", new PropertyModel<BrixNode>(model, "title")));
		add(new Label("author", new PropertyModel<BrixNode>(model, "author")));
		add(new Label("published", new PropertyModel<BrixNode>(model, "published")));
		Label label = new Label("label", new Model<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				return getArticle().getDataAsString();
			}
		});
		label.setEscapeModelStrings(false);
		add(new MessagesPanel("comments", model, false) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return getArticle().isAllowDiscussion();
			}
		});
		add(label);

	}

	private ArticleNode getArticle() {
		return (ArticleNode) getModelObject();
	}
}
