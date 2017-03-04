package org.brixcms.plugin.jpa;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;
import org.brixcms.Brix;
import org.brixcms.Plugin;
import org.brixcms.auth.Action;
import org.brixcms.auth.Action.Context;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.plugin.jpa.auth.AccessJpaPluginAction;
import org.brixcms.plugin.jpa.web.admin.EntityManagerPanel;
import org.brixcms.plugin.jpa.web.admin.EntityModel;
import org.brixcms.plugin.jpa.web.admin.GridDataSource;
import org.brixcms.plugin.jpa.web.admin.EntityPlugin;
import org.brixcms.plugin.jpa.web.admin.ManageEntityTabFactory;
import org.brixcms.plugin.jpa.web.admin.filter.FilterPlugin;
import org.brixcms.registry.ExtensionPoint;
import org.brixcms.web.tab.CachingAbstractTab;
import org.brixcms.web.tab.IBrixTab;
import org.brixcms.workspace.Workspace;

import com.inmethod.grid.IGridColumn;

/**
 * The JpaPlugin was created as an abstract parent plugin to make it easy to
 * create new plugins that focus on editing jpa entities.
 * 
 * The counterpart for hierarchical nodes is HierarchicalNodePlugin
 * 
 * @author dan.simko@gmail.com
 */

public abstract class JpaPlugin<T extends Persistable<ID>, ID extends Serializable, F extends Serializable> implements Plugin {

    private final Brix brix;

    public JpaPlugin(Brix brix) {
        this.brix = brix;
    }

    protected abstract IModel<String> getTabName();

    protected abstract JpaPluginLocator<T, ID, F> getPluginLocator();

    public abstract Class<T> getEntityClass();

    public abstract void delete(List<T> entities);

    public abstract void clone(List<T> entities);

    public void addGridColumns(List<IGridColumn<GridDataSource<T, ID, F>, T, Object>> columns) {
    }

    public void registerFilterPlugin(FilterPlugin<T, ID, F> plugin) {
        Args.notNull(plugin, "plugin");
        brix.getConfig().getRegistry().register(getFilterPluginExtensionPoint(), plugin);
    }

    public void registerEntityPlugin(EntityPlugin<T, ID> plugin) {
        Args.notNull(plugin, "plugin");
        brix.getConfig().getRegistry().register(EntityPlugin.POINT, plugin);
    }

    public void registerManageEntityTabFactory(ManageEntityTabFactory<T> factory) {
        Args.notNull(factory, "factory");
        brix.getConfig().getRegistry().register(getManageEntityTabFactoryExtensionPoint(), factory);
    }

    public Collection<ManageEntityTabFactory<T>> getManageEntityTabFactories() {
        return brix.getConfig().getRegistry().lookupCollection(getManageEntityTabFactoryExtensionPoint());
    }

    public Collection<FilterPlugin<T, ID, F>> getFilterPlugins() {
        return brix.getConfig().getRegistry().lookupCollection(getFilterPluginExtensionPoint());
    }

    public FilterPlugin<T, ID, F> getFilterPluginById(String pluginId) {
        for (FilterPlugin<T, ID, F> plugin : getFilterPlugins()) {
            if (plugin.getPluginId().equals(pluginId)) {
                return plugin;
            }
        }
        return null;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Collection<EntityPlugin<T, ID>> getEntityPlugins() {
        return (Collection) brix.getConfig().getRegistry().lookupCollection(EntityPlugin.POINT);
    }

    public EntityPlugin<T, ID> getEntityPluginById(String pluginId) {
        for (EntityPlugin<T, ID> plugin : getEntityPlugins()) {
            if (plugin.getPluginId().equals(pluginId)) {
                return plugin;
            }
        }
        return null;
    }

    protected int getTabPriority() {
        return 0;
    }

    @Override
    public String getUserVisibleName(Workspace workspace, boolean isFrontend) {
        return null;
    }

    @Override
    public List<Workspace> getWorkspaces(Workspace currentWorkspace, boolean isFrontend) {
        return null;
    }

    @Override
    public void initWorkspace(Workspace workspace, JcrSession workspaceSession) {
    }

    @Override
    public boolean isPluginWorkspace(Workspace workspace) {
        return false;
    }

    @Override
    public List<IBrixTab> newTabs(IModel<Workspace> workspaceModel) {
        IBrixTab tab = new JpaEditorTab<T, ID, F>(getTabName(), getPluginLocator(), getTabPriority());
        return Collections.singletonList(tab);
    }

    public Brix getBrix() {
        return brix;
    }

    @SuppressWarnings("serial")
    static class JpaEditorTab<T extends Persistable<ID>, ID extends Serializable, F extends Serializable> extends CachingAbstractTab {
        private final JpaPluginLocator<T, ID, F> pluginLocator;

        public JpaEditorTab(IModel<String> title, JpaPluginLocator<T, ID, F> pluginLocator, int priority) {
            super(title, priority);
            this.pluginLocator = pluginLocator;
        }

        @Override
        public Panel newPanel(String panelId) {
            return new EntityManagerPanel<T, ID, F>(panelId, new EntityModel<T, ID>(null), pluginLocator);
        }

        @Override
        public boolean isVisible() {
            final Action action = new AccessJpaPluginAction<T, ID, F>(pluginLocator);
            return Brix.get().getAuthorizationStrategy().isActionAuthorized(action);
        }

    }

    public boolean canCreateEntity(Context context) {
        return true;
    }

    public boolean canCloneEntity(T entity, Context context) {
        return true;
    }

    public boolean canViewEntity(T entity, Context context) {
        return true;
    }

    public boolean canEditEntity(T entity, Context context) {
        return true;
    }

    public boolean canDeleteEntity(T entity, Context context) {
        return true;
    }

    protected ExtensionPoint<ManageEntityTabFactory<T>> getManageEntityTabFactoryExtensionPoint() {
        return manageEntityTabFactoryPoint;
    }

    protected ExtensionPoint<FilterPlugin<T, ID, F>> getFilterPluginExtensionPoint() {
        return filterPluginPoint;
    }

    private ExtensionPoint<FilterPlugin<T, ID, F>> filterPluginPoint = new ExtensionPoint<FilterPlugin<T, ID, F>>() {
        @Override
        public org.brixcms.registry.ExtensionPoint.Multiplicity getMultiplicity() {
            return Multiplicity.COLLECTION;
        }

        @Override
        public String getUuid() {
            return FilterPlugin.class.getName();
        }
    };

    private final ExtensionPoint<ManageEntityTabFactory<T>> manageEntityTabFactoryPoint = new ExtensionPoint<ManageEntityTabFactory<T>>() {
        @Override
        public org.brixcms.registry.ExtensionPoint.Multiplicity getMultiplicity() {
            return Multiplicity.COLLECTION;
        }

        @Override
        public String getUuid() {
            return ManageEntityTabFactory.class.getName();
        }
    };

}
