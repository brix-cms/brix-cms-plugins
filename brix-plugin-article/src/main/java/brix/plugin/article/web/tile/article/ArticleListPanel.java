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

import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;

import brix.jcr.wrapper.BrixNode;
import brix.plugin.article.articlenode.ArticleNode;
import brix.web.generic.BrixGenericPanel;
import brix.web.nodepage.BrixPageParameters;
import brix.web.nodepage.PageParametersAware;

/**
 * @author wickeria at gmail.com
 */
public class ArticleListPanel extends BrixGenericPanel<BrixNode> implements PageParametersAware {

	private static final long serialVersionUID = 1L;
	private String articleNodeName;

	public ArticleListPanel(String id, IModel<BrixNode> model) {
		super(id, model);
		setOutputMarkupId(true);
		DataView<ArticleNode> dataView = new DataView<ArticleNode>("data", new ArticleDataProvider(model), (int) model.getObject()
				.getProperty(ArticleListTileEditorPanel.ARTICLES_PER_PAGE).getLong()) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<ArticleNode> item) {
				boolean selected = !Strings.isEmpty(articleNodeName) && articleNodeName.equals(item.getModelObject().getName());
				// FIXME: hard coded 30 - make it configurable
				item.add(new ArticleLinkPanel("linkPanel", item.getModel(), selected, 30));
			}

		};
		add(dataView);
		add(new AjaxPagingNavigator("navigator", dataView));
	}

	@Override
	public void contributeToPageParameters(BrixPageParameters params) {

	}

	@Override
	public void initializeFromPageParameters(BrixPageParameters params) {
		articleNodeName = params.getIndexedParam(0).toString();
	}

}
