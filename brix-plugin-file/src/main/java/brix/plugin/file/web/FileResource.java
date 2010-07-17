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

package brix.plugin.file.web;

import java.io.IOException;
import java.io.InputStream;

import javax.jcr.RepositoryException;

import org.apache.wicket.Resource;
import org.apache.wicket.Response;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;

import brix.Brix;
import brix.jcr.api.JcrNode;
import brix.jcr.wrapper.BrixFileNode;
import brix.plugin.file.util.FilePluginUtils;
import brix.plugin.hierarchical.nodes.TitledNode;

/**
 * @author wickeria at gmail.com
 */
public class FileResource extends Resource {

	private static final long serialVersionUID = 1L;

	@Override
	public IResourceStream getResourceStream() {
		return new AbstractResourceStream() {

			private static final long serialVersionUID = 1L;

			@Override
			public InputStream getInputStream() throws ResourceStreamNotFoundException {
				return getImageStream();
			}

			@Override
			public void close() throws IOException {

			}
		};
	}

	@Override
	protected final void configureResponse(final Response response) {
		JcrNode node = getNode();
		if (node != null && isNodeAttachment(node)) {
			((WebResponse) response).setAttachmentHeader(getNodeTitle(node));
		}
	}

	private InputStream getImageStream() throws ResourceStreamNotFoundException {
		JcrNode node = getNode();
		if (node != null) {
			if (BrixFileNode.isFileNode(node)) {
				BrixFileNode fileNode = new BrixFileNode(node, node.getSession());
				return fileNode.getDataAsStream();
			} else if (node instanceof NodeWithPicture) {
				try {
					NodeWithPicture folderNode = (NodeWithPicture) node;
					if (folderNode.getPicture() != null) {
						return folderNode.getPicture().getStream();
					}
				} catch (RepositoryException e) {
					throw new ResourceStreamNotFoundException(e);
				}
			}
		}
		return this.getClass().getResourceAsStream("no_image.gif");
		// return new NullInputStream(0);
	}

	private String getNodeTitle(JcrNode node) {
		if (node instanceof TitledNode) {
			return ((TitledNode) node).getTitle();
		}
		return node.getName();
	}

	private JcrNode getNode() {
		String workspace = getParameters().getString("ws");
		String id = getParameters().getString("id");
		try {
			return Brix.get().getCurrentSession(workspace).getNodeByIdentifier(id);
		} catch (Exception e) {
			return null;
		}
	}

	private boolean isNodeAttachment(JcrNode node) {
		if (BrixFileNode.isFileNode(node)) {
			BrixFileNode fileNode = new BrixFileNode(node, node.getSession());
			return !FilePluginUtils.isImage(fileNode);
		}
		return false;
	}
}
