package org.brixcms.plugin.jpa.web.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.plugin.jpa.JpaPluginLocator;
import org.brixcms.plugin.jpa.Persistable;
import org.brixcms.plugin.jpa.web.admin.filter.FilterPanel.FilterPluginEntry;

import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridSortState;
import com.inmethod.grid.IGridSortState.ISortStateColumn;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class GridDataSource<T extends Persistable<ID>, ID extends Serializable, F extends Serializable> implements IDataSource<T> {

    @SpringBean
    private EntityManager entityManager;

    private final JpaPluginLocator<T, ID, F> pluginLocator;
    private final List<FilterPluginEntry<T, ID, F>> filters = new ArrayList<>();

    public GridDataSource(JpaPluginLocator<T, ID, F> pluginLocator) {
        Injector.get().inject(this);
        this.pluginLocator = pluginLocator;
    }

    @Override
    public IModel<T> model(T object) {
        return new EntityModel<T, ID>(object);
    }

    @Override
    public void query(IQuery query, IQueryResult<T> result) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = builder.createQuery(pluginLocator.getPlugin().getEntityClass());
        CriteriaQuery<Long> countCriteriaQuery = builder.createQuery(Long.class);
        Root<T> root = criteriaQuery.from(pluginLocator.getPlugin().getEntityClass());
        Root<T> countRoot = countCriteriaQuery.from(pluginLocator.getPlugin().getEntityClass());

        applyFilter(builder, criteriaQuery, root);
        applyFilter(builder, countCriteriaQuery, countRoot);

        // is there any sorting
        if (query.getSortState().getColumns().size() > 0) {
            // get the most relevant column
            ISortStateColumn<Object> state = (ISortStateColumn<Object>) query.getSortState().getColumns().get(0);

            // get the column sort properties
            String sortProperty = state.getPropertyName().toString();
            boolean sortAsc = state.getDirection() == IGridSortState.Direction.ASC;
            criteriaQuery.orderBy(sortAsc ? builder.asc(root.get(sortProperty)) : builder.desc(root.get(sortProperty)));
        }

        result.setTotalCount(entityManager.createQuery(countCriteriaQuery.select(builder.count(countRoot))).getSingleResult());
        result.setItems(entityManager.createQuery(criteriaQuery.select(root)).setFirstResult((int) query.getFrom())
                .setMaxResults((int) query.getCount()).getResultList().iterator());
    }

    private void applyFilter(CriteriaBuilder builder, CriteriaQuery<?> criteriaQuery, Root<T> root) {
        for (FilterPluginEntry<T, ID, F> filterEntry : filters) {
            List<Predicate> predicates = filterEntry.getPlugin().createPredicate(builder, root, filterEntry.getFilter());
            if (!predicates.isEmpty()) {
                criteriaQuery.where(builder.and(predicates.toArray(new Predicate[0])));
            }
        }
    }

    public List<FilterPluginEntry<T, ID, F>> getFilters() {
        return filters;
    }

    @Override
    public void detach() {
    }
}
