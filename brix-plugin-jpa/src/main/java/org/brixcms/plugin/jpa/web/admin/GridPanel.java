package org.brixcms.plugin.jpa.web.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.auth.Action.Context;
import org.brixcms.plugin.jpa.JpaPluginLocator;
import org.brixcms.plugin.jpa.Persistable;
import org.brixcms.plugin.jpa.web.admin.filter.FilterPanel.FilterChanged;
import org.brixcms.web.generic.BrixGenericPanel;

import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;
import com.inmethod.grid.column.AbstractColumn;
import com.inmethod.grid.column.CheckBoxColumn;
import com.inmethod.grid.datagrid.DataGrid;
import com.inmethod.grid.datagrid.DefaultDataGrid;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class GridPanel<T extends Persistable<ID>, ID extends Serializable, F extends Serializable> extends Panel {

    private JpaPluginLocator<T, ID, F> pluginLocator;
    private DataGrid<GridDataSource<T, ID, F>, T, Object> grid;
    private Label selectionLabel;
    private GridDataSource<T, ID, F> dataSource;

    public GridPanel(String id, JpaPluginLocator<T, ID, F> locator) {
        super(id);
        setOutputMarkupId(true);
        this.pluginLocator = locator;
        List<IGridColumn<GridDataSource<T, ID, F>, T, Object>> columns = new ArrayList<IGridColumn<GridDataSource<T, ID, F>, T, Object>>();
        columns.add(new IdColumn());
        pluginLocator.getPlugin().addGridColumns(columns);
        columns.add(0, new CheckBoxColumn<GridDataSource<T, ID, F>, T, Object>("checkbox"));
        dataSource = new GridDataSource<T, ID, F>(pluginLocator);
        grid = new DefaultDataGrid<GridDataSource<T, ID, F>, T, Object>("grid", dataSource, columns) {

            @Override
            public void onItemSelectionChanged(IModel<T> item, boolean newValue) {
                super.onItemSelectionChanged(item, newValue);
                getRequestCycle().find(AjaxRequestTarget.class).ifPresent(t -> {
                    t.add(selectionLabel);
                    Collection<IModel<T>> selected = getSelectedItems();
                    if (selected.isEmpty()) {
                        send(getPage(), Broadcast.BREADTH, new SelectionChangedEvent<T>(new EntityModel<>(null)));
                    } else {
                        send(getPage(), Broadcast.BREADTH, new SelectionChangedEvent<T>(selected.iterator().next()));
                    }
                });
            }

            @Override
            protected void onRowClicked(AjaxRequestTarget target, IModel<T> model) {
                super.onRowClicked(target, model);
                selectItem(model, true);
            }
        };
        grid.setRowsPerPage(10);
        grid.setContentHeight(15, SizeUnit.EM);
        grid.setAllowSelectMultiple(false);
        grid.setCleanSelectionOnPageChange(false);
        add(grid);
        add(new CheckBox("allowSelectMultiple", new Model<Boolean>() {
            @Override
            public Boolean getObject() {
                return grid.isAllowSelectMultiple();
            }

            @Override
            public void setObject(Boolean bool) {
                grid.setAllowSelectMultiple(bool);
            }
        }).add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(GridPanel.this);
            }
        }));
        add(selectionLabel = new Label("currentSelection", new Model<Integer>() {
            @Override
            public Integer getObject() {
                return grid.getSelectedItems().size();
            }
        }));
        selectionLabel.setOutputMarkupId(true);
        add(new Link<Void>("clone") {

            @Override
            public void onClick() {
                pluginLocator.getPlugin().clone(getSelectedItems());
            }

            @Override
            public boolean isVisible() {
                if (grid.getSelectedItems().isEmpty()) {
                    return false;
                }
                for (T entity : getSelectedItems()) {
                    if (!pluginLocator.getPlugin().canCloneEntity(entity, Context.ADMINISTRATION)) {
                        return false;
                    }
                }
                return true;
            }

        });

        add(new Link<Void>("delete") {

            @Override
            public void onClick() {
                pluginLocator.getPlugin().delete(getSelectedItems());
                grid.resetSelectedItems();
                send(getPage(), Broadcast.BREADTH, new SelectionChangedEvent<T>(new EntityModel<>(null)));
            }

            @Override
            public boolean isVisible() {
                if (grid.getSelectedItems().isEmpty()) {
                    return false;
                }
                for (T entity : getSelectedItems()) {
                    if (!pluginLocator.getPlugin().canDeleteEntity(entity, Context.ADMINISTRATION)) {
                        return false;
                    }
                }
                return true;
            }

        });

    }

    private List<T> getSelectedItems() {
        List<T> list = new ArrayList<T>();
        Collection<IModel<T>> selected = grid.getSelectedItems();
        for (IModel<T> model : selected) {
            model.detach();
            list.add(model.getObject());
        }
        return list;
    }

    private class IdColumn extends AbstractColumn<GridDataSource<T, ID, F>, T, Object> {
        public IdColumn() {
            super("id", new ResourceModel("jpa-plugin.id"), "id");
            setInitialSize(50);
        }

        @Override
        public Component newCell(WebMarkupContainer parent, String componentId, IModel<T> rowModel) {
            return new IdPanel(componentId, rowModel);
        }
    };

    @Override
    public void onEvent(IEvent<?> event) {
        super.onEvent(event);
        Object payload = event.getPayload();
        if (payload instanceof FilterChanged) {
            @SuppressWarnings("unchecked")
            FilterChanged<T, ID, F> e = (FilterChanged<T, ID, F>) payload;
            dataSource.getFilters().clear();
            dataSource.getFilters().addAll(e.getEntries());
        }
    }

    private class IdPanel extends BrixGenericPanel<T> {

        public IdPanel(String id, final IModel<T> model) {
            super(id, model);
            Link<?> link;
            add(link = new Link<Void>("select") {
                @Override
                public void onClick() {
                    grid.selectItem(model, true);
                    send(getPage(), Broadcast.BREADTH, new SelectionChangedEvent<T>(model));
                }
            });
            link.add(new Label("label", new PropertyModel<String>(model, "id")));
        }
    }

    public static class SelectionChangedEvent<T> implements Serializable {
        private final IModel<T> selected;

        public SelectionChangedEvent(IModel<T> selected) {
            this.selected = selected;
        }

        public IModel<T> getSelected() {
            return selected;
        }

    }

}
