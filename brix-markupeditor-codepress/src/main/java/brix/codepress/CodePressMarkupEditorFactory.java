package brix.codepress;

import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import org.brixcms.plugin.site.page.admin.MarkupEditorFactory;

public class CodePressMarkupEditorFactory implements MarkupEditorFactory
{

    public TextArea<String> newEditor(String id, IModel<String> markup)
    {
        TextArea<String> editor = new TextArea<String>(id, markup);
        editor.add(new CodePressEnabler("html", true));
        return editor;
    }

    public IModel<String> newLabel()
    {
        return new Model<String>("CodePress");
    }

}
