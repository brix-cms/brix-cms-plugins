package org.brixcms.plugin.content.blog.post.admin.editor;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.brixcms.registry.ExtensionPoint;

/**
 * A factory that can create an editor to edit post. The user can then chose
 * from the list of available editors when editing post throught he web
 * interface.
 * 
 * @author dan.simko@gmail.com
 * 
 */
public interface PostEditorFactory {

    /**
     * Extension point used to register repository initializers
     */
    public static final ExtensionPoint<PostEditorFactory> POINT = new ExtensionPoint<PostEditorFactory>() {

        public Multiplicity getMultiplicity() {
            return Multiplicity.COLLECTION;
        }

        public String getUuid() {
            return PostEditorFactory.class.getName();
        }

    };

    /**
     * Create the panel that will represent the editor
     * 
     * @param id
     *            component id
     * @param markup
     *            markup model
     * @return editor component
     */
    Panel newEditor(String id, IModel<String> markup);

    /**
     * Create a model that will display the editor name in the menu
     * 
     * @return
     */
    String newStringLabelKey();
}
