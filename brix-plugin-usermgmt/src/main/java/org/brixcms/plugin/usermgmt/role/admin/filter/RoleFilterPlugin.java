package org.brixcms.plugin.usermgmt.role.admin.filter;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.brixcms.plugin.jpa.web.admin.filter.FilterPlugin;
import org.brixcms.plugin.usermgmt.role.Role;
import org.brixcms.plugin.usermgmt.role.RoleFilter;

/**
 * @author dan.simko@gmail.com
 */
public class RoleFilterPlugin implements FilterPlugin<Role, Long, RoleFilter> {

    @Override
    public String getPluginId() {
        return RoleFilterPlugin.class.getName();
    }

    @Override
    public RoleFilter newFilter() {
        return new RoleFilter();
    }

    @Override
    public Panel newFilterPanel(String id, IModel<RoleFilter> model) {
        return new RoleFilterPanel(id, model);
    }

    @Override
    public List<Predicate> createPredicate(CriteriaBuilder builder, Root<Role> root, RoleFilter filter) {
        List<Predicate> predicates = new ArrayList<>();
        if (filter.getId() != null) {
            predicates.add(builder.equal(root.get("id"), filter.getId()));
        }
        if (filter.getName() != null) {
            predicates.add(builder.like(root.get("name"), filter.getName() + "%"));
        }
        return predicates;
    }

}
