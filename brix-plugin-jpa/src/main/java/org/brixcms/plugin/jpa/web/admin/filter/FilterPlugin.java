
package org.brixcms.plugin.jpa.web.admin.filter;

import java.io.Serializable;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.brixcms.plugin.jpa.Persistable;

/**
 * Plugin that handles filtering for certain entity type.
 *
 * @author dan.simko@gmail.com
 */
public interface FilterPlugin<T extends Persistable<ID>, ID extends Serializable, F extends Serializable> {

    /**
     * Returns the plugin Id. Each FilterPlugin must have unique ID.
     *
     * @return plugin Id
     */
    String getPluginId();

    /**
     * Returns new instance of filter DTO.
     * 
     * @return new filter
     */
    F newFilter();

    /**
     * Creates a new Panel which contains form components (e.g. TextField) for
     * given Filter.
     * 
     * @param id
     * @param model
     * @return new Filter Panel
     */
    Panel newFilterPanel(String id, IModel<F> model);

    /**
     * Creates list of Predicates based on given Filter.
     * 
     * @param builder
     * @param root
     * @param filter
     * @return list of Predicates
     */
    List<Predicate> createPredicate(CriteriaBuilder builder, Root<T> root, F filter);

}
