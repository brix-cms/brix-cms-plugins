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

package brix.plugin.article.articlenode.admin.file;

import java.util.List;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Bytes;

import brix.jcr.wrapper.BrixFileNode;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.article.articlenode.ArticleNode;
import brix.plugin.file.util.FilePluginUtils;
import brix.web.generic.BrixGenericPanel;

/**
 * @author wickeria at gmail.com
 */
public class ArticleFilesPanel extends BrixGenericPanel<BrixNode> {

	private static final long serialVersionUID = 1L;

	public ArticleFilesPanel(String id, final IModel<BrixNode> model) {
		super(id, model);
		setOutputMarkupId(true);
		add(new ListView<String>("files", new FilesModel()) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<String> item) {
				BrixFileNode fileNode = getFile(item.getModelObject());
				if (fileNode == null || !FilePluginUtils.isImage(fileNode)) {
					item.add(new Image("img", new ResourceReference(ArticleFilesPanel.class, "download_icon.png")));
				} else {
					item.add(new Image("img", new ResourceReference("file"), FilePluginUtils
							.getResourceParameters(fileNode)));
				}
				item.add(new Label("tag", FilePluginUtils.getResourceTag(fileNode, this)));
				item.add(new Label("size", new Model<String>() {
					private static final long serialVersionUID = 1L;

					@Override
					public String getObject() {
						BrixFileNode node = getFile(item.getModelObject());
						if (node != null) {
							return Bytes.bytes(node.getContentLength()).toString() + " bytes";
						}
						return "";
					}
				}));
				item.add(new Label("resolution", new Model<String>() {
					private static final long serialVersionUID = 1L;

					@Override
					public String getObject() {
						BrixFileNode node = getFile(item.getModelObject());
						if (node != null) {
							return FilePluginUtils.getResolution(node);
						}
						return "";
					}
				}) {
					private static final long serialVersionUID = 1L;

					@Override
					public boolean isVisible() {
						return FilePluginUtils.isImage(getFile(item.getModelObject()));
					}
				});
				item.add(new Link<Void>("remove") {
					private static final long serialVersionUID = 1L;

					@SuppressWarnings( { "deprecation", "unchecked" })
					@Override
					public void onClick() {
						ArticleNode articleNode = (ArticleNode) model.getObject();
						articleNode.removeFile(item.getModelObject());
						articleNode.save();
						findParent(ListView.class).setModel(new FilesModel());
					}

					@Override
					protected void onComponentTag(ComponentTag tag) {
						super.onComponentTag(tag);
						String confirm = ArticleFilesPanel.this.getString("removeConfirmation");
						tag.put("onclick", "if (!confirm('" + confirm + "')) return false; "
								+ tag.getAttributes().get("onclick"));
					}

				});
			}
		});
	}

	private class FilesModel extends LoadableDetachableModel<List<String>> {
		private static final long serialVersionUID = 1L;

		@Override
		protected List<String> load() {
			ArticleNode articleNode = (ArticleNode) ArticleFilesPanel.this.getModelObject();
			return articleNode.getFiles();
		}
	}

	private BrixFileNode getFile(String id) {
		try {
			return (BrixFileNode) ArticleFilesPanel.this.getModelObject().getSession().getNodeByIdentifier(id);
		} catch (Exception e) {
			return null;
		}
	}
}
