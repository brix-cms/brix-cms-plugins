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
package brix.plugin.gallery.util;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.NodeIterator;

import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.api.JcrNodeIterator;
import org.brixcms.jcr.wrapper.BrixFileNode;
import org.brixcms.jcr.wrapper.BrixNode;
import brix.plugin.gallery.GalleryPlugin;
import brix.plugin.gallery.album.AlbumFolderNode;
import brix.plugin.gallery.photo.PhotoNode;

/**
 * @author wickeria at gmail.com
 */
public class GalleryPluginUtils {

	public static AlbumFolderNode getFolder(String searchPath) {
		JcrNode jcrNode = GalleryPlugin.getGallerySession().getNode(searchPath);
		if (jcrNode instanceof AlbumFolderNode) {
			AlbumFolderNode folderNode = (AlbumFolderNode) jcrNode;
			if (!folderNode.isHidden()) {
				return folderNode;
			}
		}
		return null;
	}

	public static List<AlbumFolderNode> getFolders(String searchPath) {
		List<AlbumFolderNode> result = new ArrayList<AlbumFolderNode>();
		JcrNode jcrNode = GalleryPlugin.getGallerySession().getNode(searchPath);
		JcrNodeIterator ni = jcrNode.getNodes();
		while (ni.hasNext()) {
			BrixNode node = (BrixNode) ni.nextNode();
			if (node instanceof AlbumFolderNode) {
				AlbumFolderNode folderNode = (AlbumFolderNode) node;
				if (!folderNode.isHidden()) {
					result.add(folderNode);
				}
			}
		}
		return result;
	}

	public static List<PhotoNode> searchImages(String searchPath) {
		List<PhotoNode> result = new ArrayList<PhotoNode>();
		try {
			if (!GalleryPlugin.getGallerySession().nodeExists(searchPath)) {
				return result;
			}
			JcrNode node = GalleryPlugin.getGallerySession().getNode(searchPath);
			NodeIterator ni = node.getNodes();
			while (ni.hasNext()) {
				JcrNode file = (JcrNode) ni.nextNode();
				if (BrixFileNode.isFileNode(file)) {
					result.add(new PhotoNode(file, GalleryPlugin.getGallerySession()));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Error while searching files", e);
		}
		return result;
	}
}
