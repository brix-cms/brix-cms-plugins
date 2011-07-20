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

package brix.plugin.gallery.album;

import javax.jcr.Binary;
import javax.jcr.Node;

import org.brixcms.Brix;
import org.brixcms.jcr.JcrNodeWrapperFactory;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.wrapper.BrixNode;
import brix.plugin.file.web.NodeWithPicture;
import brix.plugin.gallery.GalleryPlugin;
import brix.plugin.hierarchical.nodes.SimpleFolderNode;
import org.brixcms.web.picker.common.TreeAwareNode;
import org.brixcms.web.tree.AbstractJcrTreeNode;
import org.brixcms.web.tree.JcrTreeNode;

/**
 * @author wickeria at gmail.com
 */
public class AlbumFolderNode extends SimpleFolderNode implements TreeAwareNode, NodeWithPicture {

	public AlbumFolderNode(Node delegate, JcrSession session) {
		super(delegate, session);
	}

	@Override
	public JcrTreeNode getTreeNode(BrixNode node) {
		return new GalleryTreeNode(node);
	}

	private static class GalleryTreeNode extends AbstractJcrTreeNode {
		private static final long serialVersionUID = 1L;

		public GalleryTreeNode(BrixNode node) {
			super(node);
		}
	};

	public static final JcrNodeWrapperFactory FACTORY = new JcrNodeWrapperFactory() {

		@Override
		public boolean canWrap(Brix brix, JcrNode node) {

			if (!node.isNodeType("nt:folder")) {
				return false;
			}

			GalleryPlugin galleryPlugin = GalleryPlugin.get(brix);
			if (galleryPlugin == null) {
				return false;
			}

			return node.getPath().startsWith(galleryPlugin.getRootNodePath());
		}

		@Override
		public JcrNode wrap(Brix brix, Node node, JcrSession session) {
			return new AlbumFolderNode(node, session);
		}
	};

	public static AlbumFolderNode initialize(JcrNode node) {
		BrixNode brixNode = (BrixNode) node;
		brixNode.setNodeType(AlbumFolderNodePlugin.TYPE);
		return new AlbumFolderNode(node.getDelegate(), node.getSession());
	}

	private static class Properties {
		public static final String PREV_IMG = GalleryPlugin.NS_PREFIX + "previewImage";
	}

	public Binary getPreviewImage() {
		if (hasProperty(Properties.PREV_IMG))
			return getProperty(Properties.PREV_IMG).getBinary();
		else
			return null;
	}

	@Override
	public Binary getPicture() {
		return getPreviewImage();
	}

	public void setPreviewImage(Binary binary) {
		setProperty(Properties.PREV_IMG, binary);
	}

	@Override
	public String getUserVisibleType() {
		return "Album";
	}
}
