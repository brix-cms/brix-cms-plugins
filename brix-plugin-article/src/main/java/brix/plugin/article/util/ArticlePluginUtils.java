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

package brix.plugin.article.util;

import java.io.InputStream;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.wicket.util.string.Strings;

import org.brixcms.Brix;
import org.brixcms.jcr.api.JcrNode;
import brix.plugin.article.articlenode.ArticleNode;
import brix.plugin.file.FilePlugin;

/**
 * @author wickeria at gmail.com
 */
public class ArticlePluginUtils {

	public static void saveArticleFile(ArticleNode articleNode, String fileName, String mimeType, InputStream is) {
		try {
			String path = articleNode.getPath().replace(Brix.get().getRootPath(), "");
			String[] nodes = Strings.split(path, '/');
			JcrNode jcrNode = FilePlugin.get().getRootNode(articleNode.getSession().getWorkspace().getName());
			for (String nodeName : nodes) {
				if (!Strings.isEmpty(nodeName)) {
					try {
						jcrNode = (JcrNode) JcrUtils.getOrAddFolder(jcrNode, nodeName);
					} catch (RepositoryException e) {
						throw new RuntimeException(e);
					}
					jcrNode.getParent().save();
				}
			}
			Node node = JcrUtils.putFile(jcrNode, fileName, mimeType, is);
			node.getParent().save();
			articleNode.addFile(node.getIdentifier());
			articleNode.save();
		} catch (RepositoryException e) {
			throw new RuntimeException(e);
		}
	}

}
