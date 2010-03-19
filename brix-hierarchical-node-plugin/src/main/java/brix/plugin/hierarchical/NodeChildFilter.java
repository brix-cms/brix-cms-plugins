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

package brix.plugin.hierarchical;

/**
 * If your node implements this interface, it can indicate whether it allows a
 * certain node type as a child. This will be used when creating a new node in
 * the hierarchical manager panel.
 * 
 * For instance, say that you have a tree that needs to be structured like this:
 * 
 * <pre>
 * - news root node
 *   - news article
 *     - article section
 *       - article content
 *       - article content
 *     - article section
 *       - article content
 *       - article content
 * </pre>
 * 
 * If someone is currently editing an article section and clicks
 * "create new article", you don't want it to be a child of the article section.
 * You want it to be a child of the root node. So, if your section, article, and
 * root nodes all implement this interface, the section and article nodes can
 * return false. The root node would return true (or not implement the
 * interface, meaning that it accepts anything). Then, no matter where you were
 * in that tree when you created a new article, it would end up being a child of
 * the root. Or, if you created a section, it would end up, not as a child of
 * the section you were previously editing, but as a child of the article.
 * 
 * @author Jeremy Thomerson
 */
public interface NodeChildFilter
{

	public boolean isNodeTypeAllowedAsChild(String nodeType);

}
