package org.brixcms.plugin.jpa.web.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.brixcms.auth.Action.Context;
import org.brixcms.plugin.jpa.JpaPluginLocator;
import org.brixcms.plugin.jpa.Persistable;
import org.brixcms.plugin.jpa.web.admin.GridPanel.SelectionChangedEvent;
import org.brixcms.plugin.jpa.web.admin.filter.FilterPanel;
import org.brixcms.plugin.site.SimpleCallback;
import org.brixcms.web.generic.BrixGenericPanel;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class EntityManagerPanel<T extends Persistable<ID>, ID extends Serializable, F extends Serializable> extends BrixGenericPanel<T> {
    private static final String EDITOR_ID = "editor";
    private static MetaDataKey<String> EDITOR_NODE_TYPE = new MetaDataKey<String>() {
    };
    private final JpaPluginLocator<T, ID, F> pluginLocator;
    private Component lastEditor;
    private Component editor;

    public EntityManagerPanel(String id, IModel<T> model, JpaPluginLocator<T, ID, F> pluginLocator) {
        super(id, model);
        this.pluginLocator = pluginLocator;
        setOutputMarkupId(true);
        add(new FilterPanel<T, ID, F>("filterPanel", pluginLocator));
        add(new GridPanel<T, ID, F>("gridPanel", pluginLocator));
        WebMarkupContainer createContainer = new WebMarkupContainer("createContainer") {
            @Override
            public boolean isVisible() {
                return pluginLocator.getPlugin().canCreateEntity(Context.ADMINISTRATION);
            }

        };
        add(createContainer);

        final JpaEntityPluginEntriesModel<T, ID, F> createEntitiesModel = new JpaEntityPluginEntriesModel<T, ID, F>(pluginLocator);
        createContainer.add(new ListView<PluginEntry<T, ID, F>>("create", createEntitiesModel) {

            @Override
            protected void populateItem(final ListItem<PluginEntry<T, ID, F>> item) {
                Link<Void> link;
                item.add(link = new Link<Void>("link") {

                    @Override
                    public void onClick() {
                        EntityPlugin<T, ID> plugin = item.getModelObject().getPlugin();
                        final Component currentEditor = getEditor();

                        if (lastEditor == null || currentEditor.getMetaData(EDITOR_NODE_TYPE) == null) {
                            lastEditor = currentEditor;
                        }
                        SimpleCallback goBack = new SimpleCallback() {

                            @Override
                            public void execute() {
                                setupEditor(lastEditor);
                            }
                        };
                        Panel panel = plugin.newCreateEntityPanel(EDITOR_ID, goBack);
                        panel.setMetaData(EDITOR_NODE_TYPE, plugin.getPluginId());
                        setupEditor(panel);
                    }

                    @Override
                    protected void onComponentTag(ComponentTag tag) {
                        super.onComponentTag(tag);
                        EntityPlugin<T, ID> plugin = item.getModelObject().getPlugin();
                        String pluginId = getEditor().getMetaData(EDITOR_NODE_TYPE);
                        if (plugin.getPluginId().equals(pluginId)) {
                            CharSequence klass = tag.getAttribute("class");
                            if (klass == null) {
                                klass = "active";
                            } else {
                                klass = klass + " active";
                            }
                            tag.put("class", klass);
                        }
                    }
                });
                EntityPlugin<T, ID> plugin = item.getModelObject().getPlugin();
                link.add(new Label("label", plugin.newCreateCaptionModel()));
            }

        }.setReuseItems(false));

        editor = new WebMarkupContainer(EDITOR_ID);
        add(editor);
        setupDefaultEditor();
    }

    private void setupEditor(Component newEditor) {
        editor.replaceWith(newEditor);
        editor = newEditor;
    }

    private void setupDefaultEditor() {
        setupEditor(new EntityManagerEditorPanel<T, ID, F>(EDITOR_ID, getModel(), pluginLocator));
    }

    private Component getEditor() {
        return get(EDITOR_ID);
    };

    @Override
    public void onEvent(IEvent<?> event) {
        super.onEvent(event);
        if (event.getPayload() instanceof SelectionChangedEvent) {
            @SuppressWarnings("unchecked")
            SelectionChangedEvent<T> e = (SelectionChangedEvent<T>) event.getPayload();
            setModelObject(e.getSelected().getObject());
            setupDefaultEditor();
            getRequestCycle().find(AjaxRequestTarget.class).ifPresent(t -> t.add(this));
        }
    }

    private static class PluginEntry<T extends Persistable<ID>, ID extends Serializable, F extends Serializable> implements Serializable {

        private final JpaPluginLocator<T, ID, F> pluginLocator;
        private final String pluginId;

        public PluginEntry(JpaPluginLocator<T, ID, F> pluginLocator, EntityPlugin<T, ID> plugin) {
            this.pluginLocator = pluginLocator;
            this.pluginId = plugin.getPluginId();
        }

        public EntityPlugin<T, ID> getPlugin() {
            return pluginLocator.getPlugin().getEntityPluginById(pluginId);
        }

    }

    private static class JpaEntityPluginEntriesModel<T extends Persistable<ID>, ID extends Serializable, F extends Serializable>
            extends LoadableDetachableModel<List<PluginEntry<T, ID, F>>> {

        private final JpaPluginLocator<T, ID, F> pluginLocator;

        public JpaEntityPluginEntriesModel(JpaPluginLocator<T, ID, F> pluginLocator) {
            this.pluginLocator = pluginLocator;
        }

        @Override
        protected List<PluginEntry<T, ID, F>> load() {
            return convert(pluginLocator.getPlugin().getEntityPlugins());
        }

        private List<PluginEntry<T, ID, F>> convert(Collection<? extends EntityPlugin<T, ID>> editorPlugins) {
            List<PluginEntry<T, ID, F>> list = new ArrayList<PluginEntry<T, ID, F>>();
            for (EntityPlugin<T, ID> plugin : editorPlugins) {
                if (pluginLocator.getPlugin().getEntityClass().equals(plugin.getEntityClass())) {
                    list.add(new PluginEntry<T, ID, F>(pluginLocator, plugin));
                }
            }
            return list;
        }

    }

}
