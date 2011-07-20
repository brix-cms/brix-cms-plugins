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
package brix.plugin.article.articlenode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Session;

import org.brixcms.Brix;
import org.brixcms.jcr.JcrNodeWrapperFactory;
import org.brixcms.jcr.RepositoryUtil;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.api.JcrValue;
import org.brixcms.jcr.wrapper.BrixFileNode;
import org.brixcms.jcr.wrapper.BrixNode;

/**
 * @author wickeria at gmail.com
 */
public class ArticleNode extends BrixFileNode implements Comparable<ArticleNode> {

	public ArticleNode(Node delegate, JcrSession session) {
		super(delegate, session);
	}

	public static JcrNodeWrapperFactory FACTORY = new JcrNodeWrapperFactory() {
		@Override
		public boolean canWrap(Brix brix, JcrNode node) {
			return ArticleNodePlugin.TYPE.equals(getNodeType(node));
		}

		@Override
		public JcrNode wrap(Brix brix, Node node, JcrSession session) {
			return new ArticleNode(node, session);
		}

		@Override
		public void initializeRepository(Brix brix, Session session) {
			RepositoryUtil.registerNodeType(session.getWorkspace(), ArticleNodePlugin.TYPE, false, false, true);
		}
	};

	private static class Properties {
		public static final String TITLE = Brix.NS_PREFIX + "title";
		public static final String TEASER = Brix.NS_PREFIX + "teaser";
		public static final String FILES = Brix.NS_PREFIX + "files";
		public static final String ALLOW_DISCUSSION = Brix.NS_PREFIX + "allowDiscussion";
		public static final String PUBLISHED = Brix.NS_PREFIX + "published";
		public static final String AUTHOR = Brix.NS_PREFIX + "author";
		public static final String START_DATE = Brix.NS_PREFIX + "startDate";
		public static final String END_DATE = Brix.NS_PREFIX + "endDate";
	}

	public String getTitle() {
        return loadStringProperty(Properties.TITLE);
	}

	public void setTitle(String title) {
		setProperty(Properties.TITLE, title);
	}

	public List<String> getFiles() {
		List<String> list = new ArrayList<String>();
		if (hasProperty(Properties.FILES)) {
			try {
				JcrValue[] values = getProperty(Properties.FILES).getValues();
				for (JcrValue jcrValue : values) {
					list.add(jcrValue.getString());
				}
			} catch (Exception e) {
				list.add(getProperty(Properties.FILES).getString());
			}
		}
		return list;
	}

	public void setFiles(List<String> values) {
		setProperty(Properties.FILES, values.toArray(new String[values.size()]));
	}

	public void removeFile(String imageId) {
		List<String> list = new ArrayList<String>();
		for (String id : getFiles()) {
			if (!id.equals(imageId)) {
				list.add(id);
			}
		}
		setFiles(list);
	}

	public void addFile(String fileId) {
		List<String> list = getFiles();
		if (!list.contains(fileId)) {
			list.add(fileId);
		}
		setFiles(list);
	}

	public static ArticleNode initialize(JcrNode node) {
		BrixNode brixNode = (BrixNode) node;
		BrixFileNode.initialize(node, "text/html");
		brixNode.setNodeType(ArticleNodePlugin.TYPE);

		return new ArticleNode(node.getDelegate(), node.getSession());
	}

	public void setAllowDiscussion(boolean allowDiscussion) {
		setProperty(Properties.ALLOW_DISCUSSION, allowDiscussion);
	}

	public boolean isAllowDiscussion() {
        return hasProperty(Properties.ALLOW_DISCUSSION) && getProperty(Properties.ALLOW_DISCUSSION).getBoolean();
	}

	public Date getPublished() {
        return loadDateAttribute(Properties.PUBLISHED);
    }

    public void setPublished(Date published) {
        setDateAttribute(Properties.PUBLISHED, published);
	}

	public Date getStartDate() {
        return loadDateAttribute(Properties.START_DATE);
    }

    public void setStartDate(Date date) {
        setDateAttribute(Properties.START_DATE, date);
	}

	public Date getEndDate() {
        return loadDateAttribute(Properties.END_DATE);
    }

    public void setEndDate(Date date) {
        setDateAttribute(Properties.END_DATE, date);
	}

    private Date loadDateAttribute(String attribute) {
        Date result = null;
        if (hasProperty(attribute)) {
            result = getProperty(attribute).getDate().getTime();
        }
        return result;
    }

    private void setDateAttribute(String attribute, Date date) {
        Calendar calendar = null;
        if (date != null) {
            calendar = Calendar.getInstance();
            calendar.setTime(date);
        }
        setProperty(attribute, calendar);
    }

    public String getAuthor() {
        return loadStringProperty(Properties.AUTHOR);
	}

	public void setAuthor(String author) {
		setProperty(Properties.AUTHOR, author);
	}

	public String getTeaser() {
        return loadStringProperty(Properties.TEASER);
	}

    private String loadStringProperty(String attribute) {
        String result = null;
        if (hasProperty(attribute)) {
            result = getProperty(attribute).getString();
        }
        return result;
    }

    public void setTeaser(String author) {
		setProperty(Properties.TEASER, author);
	}

    

	@Override
	public String getUserVisibleType() {
		return "Article";
	}

	public int compareTo(ArticleNode o) {
		if (getPublished() != null && o.getPublished() != null)
			return getPublished().compareTo(o.getPublished()) * (-1);
		return 0;
	}
}
