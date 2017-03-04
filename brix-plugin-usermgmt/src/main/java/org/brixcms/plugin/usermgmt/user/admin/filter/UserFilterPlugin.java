package org.brixcms.plugin.usermgmt.user.admin.filter;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.brixcms.plugin.jpa.web.admin.filter.FilterPlugin;
import org.brixcms.plugin.usermgmt.user.User;
import org.brixcms.plugin.usermgmt.user.UserFilter;

/**
 * @author dan.simko@gmail.com
 */
public class UserFilterPlugin implements FilterPlugin<User, Long, UserFilter> {

    @Override
    public String getPluginId() {
        return UserFilterPlugin.class.getName();
    }

    @Override
    public UserFilter newFilter() {
        return new UserFilter();
    }

    @Override
    public Panel newFilterPanel(String id, IModel<UserFilter> model) {
        return new UserFilterPanel(id, model);
    }

    @Override
    public List<Predicate> createPredicate(CriteriaBuilder builder, Root<User> root, UserFilter filter) {
        List<Predicate> predicates = new ArrayList<>();
        if (filter.getId() != null) {
            predicates.add(builder.equal(root.get("id"), filter.getId()));
        }
        if (filter.getEmail() != null) {
            predicates.add(builder.like(root.get("email"), filter.getEmail() + "%"));
        }
        if (filter.getUsername() != null) {
            predicates.add(builder.like(root.get("username"), filter.getUsername() + "%"));
        }
        if (filter.getFirstName() != null) {
            predicates.add(builder.like(root.get("firstName"), filter.getFirstName() + "%"));
        }
        if (filter.getLastName() != null) {
            predicates.add(builder.like(root.get("lastName"), filter.getLastName() + "%"));
        }
        if (filter.getVerified() != null) {
            predicates.add(builder.equal(root.get("verified"), filter.getVerified()));
        }
        return predicates;
    }

}
