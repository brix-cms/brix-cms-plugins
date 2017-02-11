package org.brixcms.plugin.content.blog.post.admin.editor;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * @author dan.simko@gmail.com
 */
public class TinymcePostEditorFactory implements PostEditorFactory {

    @Override
    public Panel newEditor(String id, IModel<String> markup) {
        return new TinymcePostEditorPanel(id, markup);
    }

    @Override
    public String newStringLabelKey() {
        return "content-plugin.tinymceEditor";
    }
}
