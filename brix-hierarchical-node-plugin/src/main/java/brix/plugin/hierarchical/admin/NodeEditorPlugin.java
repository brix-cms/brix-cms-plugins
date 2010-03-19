package brix.plugin.hierarchical.admin;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.SimpleCallback;

public interface NodeEditorPlugin
{

	/**
	 * Returns the node type of nodes that this plugin can handle.
	 * 
	 * @see BrixNode#setNodeType(String)
	 * @return
	 */
	String getNodeType();

	/**
	 * Returns the user readable name of this plugin.
	 * 
	 * @return
	 */
	String getName();

	/**
	 * Returns model caption of Create link for this plugin.
	 * 
	 * @param parentNode
	 * @return
	 */
	public IModel<String> newCreateNodeCaptionModel(IModel<BrixNode> parentNode);

	/**
	 * Returns an instance of panel that should create node of type this plugin
	 * can handle.
	 * 
	 * @param id
	 *            panel component id
	 * @param parentNode
	 *            parent node of the new node
	 * @param goBack
	 *            simple callback that should be invoked after node creation or
	 *            on cancel
	 * @return panel instance
	 */
	Panel newCreateNodePanel(String id, IModel<BrixNode> parentNode, SimpleCallback goBack);
}
