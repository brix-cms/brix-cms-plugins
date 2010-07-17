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

package brix.plugin.article.articlenode.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;

import brix.auth.Action.Context;
import brix.jcr.wrapper.BrixFileNode;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.article.ArticlePlugin;
import brix.web.generic.BrixGenericPanel;
import brix.web.tab.BrixTabbedPanel;
import brix.web.tab.CachingAbstractTab;
import brix.web.tab.IBrixTab;

/**
 * @author wickeria at gmail.com
 */
public class ViewTab extends BrixGenericPanel<BrixNode> {

	private static final long serialVersionUID = 1L;

	public ViewTab(String id, final IModel<BrixNode> model) {
		super(id, model);

		add(new Label("title", new PropertyModel<String>(model, "title")));
		add(new Label("author", new PropertyModel<String>(model, "author")));
		add(new Label("published", new PropertyModel<String>(model, "published")));
		add(new Label("allowDiscussion", new PropertyModel<String>(model, "allowDiscussion")));
		List<IBrixTab> tabs = new ArrayList<IBrixTab>();
		tabs.add(new CachingAbstractTab(new ResourceModel("textPreview")) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newPanel(String panelId) {
				return new TextPreviewPanel(panelId);
			}
		});

		tabs.add(new CachingAbstractTab(new ResourceModel("pagePreview")) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newPanel(String panelId) {
				return new IframePreviewPanel(panelId);
			}
		});

		add(new BrixTabbedPanel("previewTabbedPanel", tabs));
		add(new Link<Void>("edit") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				EditTab edit = new EditTab(ViewTab.this.getId(), ViewTab.this.getModel()) {
					private static final long serialVersionUID = 1L;

					@Override
					void goBack() {
						replaceWith(ViewTab.this);
					}
				};
				ViewTab.this.replaceWith(edit);
			}

			@Override
			public boolean isVisible() {
				BrixNode node = ViewTab.this.getModelObject();
				return ArticlePlugin.get().canEditNode(node, Context.ADMINISTRATION);
			}
		});
	}

	private class TextPreviewPanel extends Panel {

		private static final long serialVersionUID = 1L;

		public TextPreviewPanel(String id) {
			super(id);

			IModel<String> labelModel = new Model<String>() {
				private static final long serialVersionUID = 1L;

				@Override
				public String getObject() {
					BrixFileNode node = (BrixFileNode) ViewTab.this.getModel().getObject();
					return node.getDataAsString();
				}
			};

			add(new Label("label", labelModel));
		}
	}

	private class IframePreviewPanel extends Panel {
		private static final long serialVersionUID = 1L;

		public IframePreviewPanel(String id) {
			super(id);
			add(new PreviewArticleIFrame("preview", ViewTab.this.getModel()));
		}
	};

}
