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
package brix.plugin.gallery.webdav;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.http.WebRequest;

import brix.plugin.gallery.GalleryPlugin;
import brix.plugin.site.SimpleCallback;

public class WebdavPanel extends Panel {
	private static final long serialVersionUID = 1L;

	public WebdavPanel(String id, final SimpleCallback goBack) {
		super(id);

		add(new WebMarkupContainer("webdav") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				tag.put("href", getWorkspaceUrl());
			}
		});

		add(new Link<Void>("cancel") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				goBack.execute();
			}
		});

	}

	private String getWorkspaceUrl() {
		HttpServletRequest request = ((WebRequest) getRequest()).getHttpServletRequest();
		StringBuilder url = new StringBuilder();
		url.append("http://");
		url.append(request.getServerName());
		if (request.getServerPort() != 80) {
			url.append(":");
			url.append(request.getServerPort());
		}
		url.append(request.getContextPath());
		url.append("/");
		url.append("gallery-webdav");
		url.append("/");
		url.append(GalleryPlugin.getGallerySession().getWorkspace().getName());

		return url.toString();

	}

}
