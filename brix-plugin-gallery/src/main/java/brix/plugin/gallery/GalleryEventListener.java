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

package brix.plugin.gallery;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;

import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.core.observation.SynchronousEventListener;
import org.apache.jackrabbit.value.BinaryImpl;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.brixcms.jcr.JcrSessionFactory;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.wrapper.BrixFileNode;
import org.brixcms.jcr.wrapper.BrixNode;
import brix.plugin.file.util.ImageMagicProcessor;
import brix.plugin.gallery.album.AlbumFolderNode;
import brix.plugin.gallery.photo.PhotoNode;

/**
 * Creates and deletes thumbnails
 * 
 * @author wickeria at gmail.com
 */
public class GalleryEventListener implements SynchronousEventListener {

	private static final Logger log = LoggerFactory.getLogger(GalleryEventListener.class);

	private final JcrSessionFactory sf;
	private final String workspaceId;

	public GalleryEventListener(JcrSessionFactory sessionFactory, String galleryWSId) {
		this.sf = sessionFactory;
		this.workspaceId = galleryWSId;
	}

	public void onEvent(EventIterator events) {
		Session session = sf.createSession(workspaceId);
		try {
			while (events.hasNext()) {
				processEvent(events.nextEvent(), session);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (session.isLive()) {
				session.logout();
			}

		}
	}

	private void processEvent(Event event, Session session) throws Exception {
		String path = event.getPath();
		switch (event.getType()) {
		case Event.NODE_ADDED:
			AddNodeHelper.process(path, session);
			break;
		case Event.NODE_REMOVED:
			RemoveNodeHelper.process(path, session);
			break;
		}

	}

	private static class AddNodeHelper {

		public static void process(String path, Session session) throws Exception {
			if (session.nodeExists(path)) {
				Node node = session.getNode(path);
				BrixNode brixNode = new BrixNode(node, JcrSession.Wrapper.wrap(session));

				if (brixNode.isFolder()) {
					AlbumFolderNode folderNode = new AlbumFolderNode(brixNode, brixNode.getSession());
					if (!folderNode.isHidden()) {
						processFolder(folderNode);
					}
				}
				if (BrixFileNode.isFileNode(brixNode)) {
					PhotoNode fileNode = new PhotoNode(brixNode, brixNode.getSession());
					if (!fileNode.isHidden()) {
						processImage(fileNode);
					}
				}
			}
		}

		private static void processFolder(AlbumFolderNode folderNode) {
			folderNode.setTitle(folderNode.getName());
			folderNode.getSession().save();
			log.info("createGalleryFolder(): {}", folderNode);
		}

		private static void processImage(PhotoNode fileNode) throws Exception {
			PhotoNode.initialize(fileNode, fileNode.getMimeType());
			fileNode.setTitle(fileNode.getName());
			AlbumFolderNode parentFolder = new AlbumFolderNode(fileNode.getParent(), fileNode.getSession());
			for (GalleryPlugin.FOLDERS folder : GalleryPlugin.FOLDERS.values()) {
				AlbumFolderNode newFolder = new AlbumFolderNode(JcrUtils.getOrAddFolder(parentFolder, folder.name()),
						fileNode.getSession());
				PhotoNode file = new PhotoNode(JcrUtils.putFile(newFolder, fileNode.getName(), fileNode.getMimeType(),
						ImageMagicProcessor.createThumbnail(fileNode.getDataAsStream(), folder.getSize(), folder
								.getSize(), folder.isSquare())), fileNode.getSession());
				newFolder.setHidden(true);
				file.setHidden(true);
			}
			parentFolder.getSession().save();
			if (parentFolder.getPreviewImage() == null) {
				parentFolder.setPreviewImage(new BinaryImpl(ImageMagicProcessor.createThumbnail(fileNode
						.getDataAsStream(), GalleryPlugin.ALBUM_FOLDER_PREV_IMG_SIZE,
						GalleryPlugin.ALBUM_FOLDER_PREV_IMG_SIZE, true)));
			}
			parentFolder.getSession().save();
			log.info("createGalleryImage(): {}", fileNode);
		}
	}

	private static class RemoveNodeHelper {

		public static void process(String path, Session session) throws Exception {
			String removedNodeName = Strings.afterLast(path, '/');
			if (!"jcr:content".equals(removedNodeName)) {
				String parentFolderPath = Strings.beforeLast(path, '/');
				for (GalleryPlugin.FOLDERS folder : GalleryPlugin.FOLDERS.values()) {
					String thmunbPath = parentFolderPath + "/" + folder.name() + "/" + removedNodeName;
					if (session.nodeExists(thmunbPath)) {
						session.removeItem(thmunbPath);
						session.save();
					}
				}
			}
		}
	}

}
