package org.brixcms.plugin.jpa.web.admin;

import java.io.Serializable;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.brixcms.plugin.jpa.Persistable;
import org.brixcms.plugin.site.SimpleCallback;
import org.brixcms.registry.ExtensionPoint;

/**
 * Plugin that handles entity of certain type.
 *
 * @author dan.simko@gmail.com
 */
public interface EntityPlugin<T extends Persistable<ID>, ID extends Serializable> {

    public static ExtensionPoint<EntityPlugin<?, ?>> POINT = new ExtensionPoint<EntityPlugin<?, ?>>() {
        @Override
        public org.brixcms.registry.ExtensionPoint.Multiplicity getMultiplicity() {
            return Multiplicity.COLLECTION;
        }

        @Override
        public String getUuid() {
            return EntityPlugin.class.getName();
        }
    };

    /**
     * Returns the plugin Id. Each JpaEntityPlugin must have unique ID.
     *
     * @return plugin Id
     */
    String getPluginId();

    /**
     * Returns the entity class that this plugin can handle.
     *
     * @return entity class
     */
    Class<T> getEntityClass();

    /**
     * Returns model caption of Create link for this plugin.
     *
     * @return caption model
     */
    IModel<String> newCreateCaptionModel();

    /**
     * Returns an instance of panel that should create new instance of entity
     * this plugin can handle.
     *
     * @param id
     *            panel component id
     * @param goBack
     *            simple callback that should be invoked after entity creation
     *            or on cancel
     * @return panel instance
     */
    Panel newCreateEntityPanel(String id, SimpleCallback goBack);
}
