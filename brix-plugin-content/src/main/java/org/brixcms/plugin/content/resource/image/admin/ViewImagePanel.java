package org.brixcms.plugin.content.resource.image.admin;


import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.IModel;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.content.resource.FileResourceReference;
import org.brixcms.plugin.content.resource.ResourceUtils;
import org.brixcms.web.generic.BrixGenericPanel;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class ViewImagePanel extends BrixGenericPanel<BrixNode> {

	public ViewImagePanel(String id, final IModel<BrixNode> model) {
		super(id, model);
		add(new Image("img", FileResourceReference.INSTANCE, ResourceUtils.getResourceParameters(getModelObject())));
	}

}
