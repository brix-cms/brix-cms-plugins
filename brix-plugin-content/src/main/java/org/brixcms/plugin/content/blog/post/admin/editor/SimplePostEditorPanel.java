package org.brixcms.plugin.content.blog.post.admin.editor;

import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class SimplePostEditorPanel extends Panel {

	public SimplePostEditorPanel(String id, final IModel<String> markup) {
		super(id, markup);
		add(new TextArea<String>("content", markup));
	}
}