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
package brix.plugin.gallery.photo;

import javax.jcr.Node;
import javax.jcr.Session;

import org.brixcms.Brix;
import org.brixcms.jcr.JcrNodeWrapperFactory;
import org.brixcms.jcr.RepositoryUtil;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.wrapper.BrixFileNode;
import org.brixcms.jcr.wrapper.BrixNode;

/**
 * @author wickeria at gmail.com
 */
public class PhotoNode extends BrixFileNode {

	public PhotoNode(Node delegate, JcrSession session) {
		super(delegate, session);
	}

	public static JcrNodeWrapperFactory FACTORY = new JcrNodeWrapperFactory() {
		@Override
		public boolean canWrap(Brix brix, JcrNode node) {
			return PhotoNodePlugin.TYPE.equals(getNodeType(node));
		}

		@Override
		public JcrNode wrap(Brix brix, Node node, JcrSession session) {
			return new PhotoNode(node, session);
		}

		@Override
		public void initializeRepository(Brix brix, Session session) {
			RepositoryUtil.registerNodeType(session.getWorkspace(), PhotoNodePlugin.TYPE, false, false, true);
		}
	};

	private static class Properties {
		public static final String TITLE = Brix.NS_PREFIX + "title";
	}

	public String getTitle() {
		if (hasProperty(Properties.TITLE))
			return getProperty(Properties.TITLE).getString();
		else
			return null;
	}

	public void setTitle(String title) {
		setProperty(Properties.TITLE, title);
	}

	public static PhotoNode initialize(JcrNode node, String mime) {
		BrixNode brixNode = (BrixNode) node;
		BrixFileNode.initialize(node, mime);
		brixNode.setNodeType(PhotoNodePlugin.TYPE);
		return new PhotoNode(node.getDelegate(), node.getSession());
	}

	@Override
	public String getUserVisibleType() {
		return "Photo";
	}

}
