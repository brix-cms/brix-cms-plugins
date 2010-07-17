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
package brix.plugin.gallery.web.tile;

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.LoadableDetachableModel;

import brix.BrixNodeModel;
import brix.plugin.gallery.GalleryPlugin;
import brix.plugin.gallery.album.AlbumFolderNode;
import brix.plugin.gallery.photo.PhotoNode;
import brix.plugin.gallery.util.GalleryPluginUtils;
import brix.web.nodepage.BrixPageParameters;
import brix.web.nodepage.PageParametersLink;

import com.visural.wicket.component.fancybox.FancyboxGroup;

/**
 * @author wickeria at gmail.com
 */
public class GalleryPanel extends BaseGalleryPanel {

	private static final long serialVersionUID = 1L;
	private FancyboxGroup group;

	public GalleryPanel(String id) {
		super(id);
		add(new PageParametersLink("all") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void contributeToPageParameters(BrixPageParameters parameters) {
			}

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				if (getAlbumParams().size() == 0) {
					tag.put("class", "breadcrumb-last");
				}
			}

		});
		add(new ListView<String>("breadcrumbs", new AbstractReadOnlyModel<List<String>>() {
			private static final long serialVersionUID = 1L;

			@Override
			public List<String> getObject() {
				return getAlbumParams();
			}
		}) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<String> item) {
				final LinkedList<String> params = new LinkedList<String>();
				for (int i = 0; i <= item.getIndex(); i++) {
					params.add(getAlbumParams().get(i));
				}
				PageParametersLink link = new PageParametersLink("link") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void contributeToPageParameters(BrixPageParameters parameters) {
						for (int i = 0; i <= item.getIndex(); i++) {
							parameters.setIndexedParam(i, getAlbumParams().get(i));
						}
					}

					@Override
					protected void onComponentTag(ComponentTag tag) {
						super.onComponentTag(tag);
						if (item.getIndex() == (getAlbumParams().size() - 1)) {
							tag.put("class", "breadcrumb-last");
						}
					}

				};
				item.add(link);
				link.add(new Label("name", GalleryPluginUtils.getFolder(createPathFromParams(params)).getTitle()));
			}
		});

		add(new ListView<AlbumFolderNode>("albums", new LoadableDetachableModel<List<AlbumFolderNode>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<AlbumFolderNode> load() {
				return GalleryPluginUtils.getFolders(createPathFromParams(getAlbumParams()));
			}
		}) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<AlbumFolderNode> item) {
				item
						.add(new AlbumFolderPanel("folderPanel", new BrixNodeModel(item.getModelObject()),
								getAlbumParams()));
			}
		});

		add(new ListView<PhotoNode>("photos", new LoadableDetachableModel<List<PhotoNode>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<PhotoNode> load() {
				return GalleryPluginUtils.searchImages(createPathFromParams(getAlbumParams()) + "/"
						+ GalleryPlugin.FOLDERS.V_96.name());
			}
		}) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<PhotoNode> item) {
				item.add(new PhotoPanel("thumb", new BrixNodeModel(item.getModelObject()), group, item.getIndex(),
						getViewSize()));
			}
		});
	}

	@Override
	protected void onBeforeRender() {
		group = FancyboxGroup.get();
		super.onBeforeRender();
	}
}
