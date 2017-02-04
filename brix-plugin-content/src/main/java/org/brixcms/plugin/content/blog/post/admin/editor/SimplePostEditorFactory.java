package org.brixcms.plugin.content.blog.post.admin.editor;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * Default implementation of post editor factory. Uses a simple textarea.
 * 
 * @author dan.simko@gmail.com
 */
public class SimplePostEditorFactory implements PostEditorFactory {

    @Override
    public Panel newEditor(String id, IModel<String> markup) {
        return new SimplePostEditorPanel(id, markup);
    }

    @Override
    public String newStringLabelKey() {
        return "content-plugin.simpleTextEditor";
    }

}
