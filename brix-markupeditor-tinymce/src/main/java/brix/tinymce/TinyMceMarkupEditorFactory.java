package brix.tinymce;

import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import org.brixcms.plugin.site.page.admin.MarkupEditorFactory;

public class TinyMceMarkupEditorFactory implements MarkupEditorFactory
{

    public TextArea<String> newEditor(String id, IModel<String> markup)
    {
        TextArea<String> editor = new TextArea<String>(id, markup);
        editor.add(new TinyMceEnabler());
        return editor;
    }

    public IModel<String> newLabel()
    {
        return new Model<String>("TinyMCE WYSIWYG");
    }

}
