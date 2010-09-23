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

import java.util.*;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import brix.BrixNodeModel;
import brix.auth.Action.Context;
import brix.jcr.api.JcrNodeIterator;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.article.ArticlePlugin;
import brix.plugin.article.articlenode.ArticleNode;
import brix.plugin.article.articlenode.ArticleNodePlugin;
import brix.plugin.hierarchical.admin.HierarchicalNodeManagerPanel;

/**
 * @author wickeria at gmail.com
 */
public class ArticleDataProvider implements IDataProvider<ArticleNode> {

	private static final long serialVersionUID = 1L;

	private IModel<BrixNode> model;
	private int displayedArticleCount = Integer.MAX_VALUE;

	public ArticleDataProvider(IModel<BrixNode> model, Integer displayedArticleCount) {
		this.model = model;
		this.displayedArticleCount = displayedArticleCount;
	}

	public ArticleDataProvider(IModel<BrixNode> model) {
		this.model = model;
	}

	public Iterator<ArticleNode> iterator(int first, int count) {
		return getArticleNodes().subList(first, first + count).iterator();
	}

	@SuppressWarnings("unchecked")
	public IModel<ArticleNode> model(ArticleNode object) {
		IModel model = new BrixNodeModel(object);
		return model;
	}

	public int size() {
		return getArticleNodes().size();
	}

	public void detach() {
		model.detach();

	}

	private BrixNode getFolderNode() {
		BrixNode brixNode = model.getObject();
		return (BrixNode) brixNode.getProperty(ArticleDetailTileEditorPanel.ARTICLE_PAGE).getNode();
	}

	private boolean canShowNode(BrixNode node) {
        boolean result = false;
        if (!node.isHidden() && HierarchicalNodeManagerPanel.SHOW_ALL_NON_NULL_NODES_FILTER.isNodeAllowed(node)
				&& ArticlePlugin.get().canViewNode(node, Context.PRESENTATION)
				&& ArticleNodePlugin.TYPE.equals(node.getNodeType())) {
            result = true;
            long time = new Date().getTime();
            Date startDate = ((ArticleNode) node).getStartDate();
            if (startDate != null && startDate.getTime() > time) {
                result = false;
            }
            Date endDate = ((ArticleNode)node).getEndDate();
            if (endDate != null && endDate.getTime() < time) {
                result = false;
            }
        }
        return result;
    }

	public List<ArticleNode> getArticleNodes() {
		JcrNodeIterator iterator = getFolderNode().getNodes();
		List<ArticleNode> res = new ArrayList<ArticleNode>();
		while (iterator.hasNext()) {
			BrixNode node = (BrixNode) iterator.nextNode();
			if (canShowNode(node)) {
				res.add((ArticleNode) node);
			}
		}
		Collections.sort(res);
		if (displayedArticleCount < res.size()) {
			return res.subList(0, displayedArticleCount);
		}
		return res;
	}

}
