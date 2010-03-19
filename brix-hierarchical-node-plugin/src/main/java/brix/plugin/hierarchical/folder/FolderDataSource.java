package brix.plugin.hierarchical.folder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.wicket.model.IModel;

import brix.BrixNodeModel;
import brix.auth.Action.Context;
import brix.jcr.api.JcrNodeIterator;
import brix.jcr.wrapper.BrixFileNode;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.hierarchical.HierarchicalPluginLocator;
import brix.plugin.hierarchical.admin.HierarchicalNodeManagerPanel;

import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridSortState;
import com.inmethod.grid.IGridSortState.ISortStateColumn;

abstract class FolderDataSource implements IDataSource
{
	
	private final HierarchicalPluginLocator pluginLocator;
	
	public FolderDataSource(HierarchicalPluginLocator pluginLocator)
	{
		this.pluginLocator = pluginLocator;
	}
	
	public void detach()
	{

	}

	public IModel<?> model(Object object)
	{
		return new BrixNodeModel((BrixNode)object);
	}

	public void query(IQuery query, IQueryResult result)
	{
		BrixNode node = getFolderNode();
		List<BrixNode> visibleNodes = visibleNodes(node.getNodes());
		if (query.getSortState().getColumns().isEmpty() == false)
		{
			sort(visibleNodes, query.getSortState());
		}
		else
		{
			sort(visibleNodes, PROPERTY_NAME, IGridSortState.Direction.ASC);
		}

		if (node.getPath().equals(pluginLocator.getPlugin().getRootNodePath()) == false)
		{
			BrixNode parent = (BrixNode)node.getParent();
			if (canShowNode(parent))
			{
				visibleNodes.add(0, parent);
			}
		}

		result.setItems(visibleNodes.iterator());
		result.setTotalCount(visibleNodes.size());
	}

	private void sort(List<BrixNode> node, IGridSortState state)
	{
		int max = Math.min(state.getColumns().size() - 1, 2);
		for (int i = max; i >= 0; --i)
		{
			ISortStateColumn column = state.getColumns().get(i);
			sort(node, column.getPropertyName(), column.getDirection());
		}
	}

	abstract BrixNode getFolderNode();

	private void sort(List<BrixNode> original, final String property,
			final IGridSortState.Direction direction)
	{
		Collections.sort(original, new Comparator<BrixNode>()
		{
			public int compare(BrixNode o1, BrixNode o2)
			{
				int res = compareNodes(o1, o2, property);
				if (direction == IGridSortState.Direction.DESC)
				{
					res = -res;
				}
				return res;
			}
		});
	};

	private <T> int compare(Comparable<T> c1, T c2)
	{
		if (c1 == null || c2 == null)
		{
			return 0;
		}
		else
		{
			if (c1 instanceof String)
			{
				return ((String)c1).compareToIgnoreCase((String)c2);
			}
			else
			{
				return c1.compareTo(c2);
			}
		}
	}

	public static final String PROPERTY_NAME = "name";
	public static final String PROPERTY_TYPE = "type";
	public static final String PROPERTY_CREATED = "created";
	public static final String PROPERTY_CREATED_BY = "createdBy";
	public static final String PROPERTY_LAST_MODIFIED = "lastModified";
	public static final String PROPERTY_LAST_MODIFIED_BY = "lastModifiedBy";
	public static final String PROPERTY_MIME_TYPE = "mimeType";
	public static final String PROPERTY_SIZE = "size";

	private int compareNodes(BrixNode n1, BrixNode n2, String property)
	{
		// always put folders first if we sort by name, size or mime type
		if (PROPERTY_NAME.equals(property) || PROPERTY_SIZE.equals(property)
				|| PROPERTY_MIME_TYPE.equals(property))
		{
			if (n1.isFolder() && !n2.isFolder())
			{
				return -1;
			}
			else if (n2.isFolder() && !n1.isFolder())
			{
				return 1;
			}
		}

		if (PROPERTY_NAME.equals(property))
		{
			return compare(n1.getUserVisibleName(), n2.getUserVisibleName());
		}
		else if (PROPERTY_TYPE.equals(property))
		{
			return compare(n1.getUserVisibleType(), n2.getUserVisibleType());
		}
		else if (PROPERTY_CREATED.equals(property))
		{
			return compare(n1.getCreated(), n2.getCreated());
		}
		else if (PROPERTY_CREATED_BY.equals(property))
		{
			return compare(n1.getCreatedBy(), n2.getCreatedBy());
		}
		else if (PROPERTY_LAST_MODIFIED.equals(property))
		{
			return compare(n1.getLastModified(), n2.getLastModified());
		}
		else if (PROPERTY_LAST_MODIFIED_BY.equals(n2.getLastModifiedBy()))
		{
			return compare(n1.getLastModifiedBy(), n2.getLastModifiedBy());
		}
		else if (PROPERTY_MIME_TYPE.equals(property))
		{
			String mime1 = (n1 instanceof BrixFileNode)
					? ((BrixFileNode)n1).getMimeType(true)
					: null;
			String mime2 = (n2 instanceof BrixFileNode)
					? ((BrixFileNode)n2).getMimeType(true)
					: null;
			return compare(mime1, mime2);
		}
		else if (PROPERTY_SIZE.equals(property))
		{
			Long size1 = (n1 instanceof BrixFileNode)
					? ((BrixFileNode)n1).getContentLength()
					: null;
			Long size2 = (n2 instanceof BrixFileNode)
					? ((BrixFileNode)n2).getContentLength()
					: null;
			return compare(size1, size2);
		}
		return 0;
	}

	private boolean canShowNode(BrixNode node)
	{

		if (!node.isHidden() && HierarchicalNodeManagerPanel.SHOW_ALL_NON_NULL_NODES_FILTER.isNodeAllowed(node)
				&& pluginLocator.getPlugin().canViewNode(node, Context.ADMINISTRATION))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	private List<BrixNode> visibleNodes(JcrNodeIterator iterator)
	{
		List<BrixNode> res = new ArrayList<BrixNode>();
		while (iterator.hasNext())
		{
			BrixNode node = (BrixNode)iterator.nextNode();
			if (canShowNode(node))
			{
				res.add(node);
			}
		}
		return res;
	}
}