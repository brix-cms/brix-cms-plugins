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

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import org.brixcms.BrixNodeModel;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.page.tile.admin.GenericTileEditorPanel;
import org.brixcms.web.picker.node.NodePickerPanel;

/**
 * @author wickeria at gmail.com
 */
public class ArticleDetailTileEditorPanel extends GenericTileEditorPanel<BrixNode> {

	private static final long serialVersionUID = 1L;
	public static final String ARTICLE_PAGE = "articlePage";
	public static final String DISPLAYED_ARTICLES_COUNT = "displayedArticleCount";

	public ArticleDetailTileEditorPanel(String id, IModel<BrixNode> tileContainerNode) {
		super(id, tileContainerNode);
		String workspace = tileContainerNode.getObject().getSession().getWorkspace().getName();
		NodePickerPanel picker = new ArticleNodePickerPanel("nodePicker", targetNodeModel, workspace);
		picker.setRequired(true);
		add(picker);
		add(new TextField<Long>("count", displayedArticleCountNodeModel, Long.class).setRequired(true));
	}

	private IModel<BrixNode> targetNodeModel = new BrixNodeModel();
	private IModel<Long> displayedArticleCountNodeModel = new Model<Long>(1l);

	@Override
	public void load(BrixNode node) {
		if (node.hasProperty(ARTICLE_PAGE)) {
			BrixNode pageNode = (BrixNode) node.getProperty(ARTICLE_PAGE).getNode();
			targetNodeModel.setObject(pageNode);
		}
		if (node.hasProperty(DISPLAYED_ARTICLES_COUNT)) {
			displayedArticleCountNodeModel.setObject(node.getProperty(DISPLAYED_ARTICLES_COUNT).getLong());
		}

	}

	@Override
	public void save(BrixNode node) {
		node.setProperty(ARTICLE_PAGE, targetNodeModel.getObject());
		node.setProperty(DISPLAYED_ARTICLES_COUNT, displayedArticleCountNodeModel.getObject());
	}

}
