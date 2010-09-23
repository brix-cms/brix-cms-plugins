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

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.string.Strings;

import brix.BrixNodeModel;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.article.articlenode.ArticleNode;
import brix.web.generic.BrixGenericPanel;
import brix.web.nodepage.BrixPageParameters;
import brix.web.nodepage.PageParametersAware;

/**
 * @author wickeria at gmail.com
 */
public class ArticleDetailPanel extends BrixGenericPanel<BrixNode> implements PageParametersAware {

	private static final long serialVersionUID = 1L;
	private String articleNodeName;
	private ArticleDataProvider dataProvider;

	public ArticleDetailPanel(String id, IModel<BrixNode> model) {
		super(id, model);
		dataProvider = new ArticleDataProvider(model, (int) model.getObject().getProperty(
				ArticleDetailTileEditorPanel.DISPLAYED_ARTICLES_COUNT).getLong());
		add(new ListView<ArticleNode>("articles", new LoadableDetachableModel<List<ArticleNode>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<ArticleNode> load() {
				return getArticleNodes();
			}
		}) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<ArticleNode> item) {
				item.add(new ArticlePanel("articlePanel", new BrixNodeModel(item.getModelObject())));
			}
		});

	}

	public void contributeToPageParameters(BrixPageParameters params) {

	}

	public void initializeFromPageParameters(BrixPageParameters params) {
		articleNodeName = params.getIndexedParam(0).toString();
	}

	private List<ArticleNode> getArticleNodes() {
		List<ArticleNode> list = new ArrayList<ArticleNode>();
		if (!Strings.isEmpty(articleNodeName)) {
			list.add((ArticleNode) getFolderNode().getNode(articleNodeName));
		} else {
			list.addAll(dataProvider.getArticleNodes());
		}
		return list;
	}

	private BrixNode getFolderNode() {
		return (BrixNode) getModelObject().getProperty(ArticleDetailTileEditorPanel.ARTICLE_PAGE).getNode();
	}

}
