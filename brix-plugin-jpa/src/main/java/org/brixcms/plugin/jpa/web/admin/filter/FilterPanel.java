package org.brixcms.plugin.jpa.web.admin.filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.brixcms.plugin.jpa.JpaPluginLocator;
import org.brixcms.plugin.jpa.Persistable;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class FilterPanel<T extends Persistable<ID>, ID extends Serializable, F extends Serializable> extends Panel {

    private final List<FilterPluginEntry<T, ID, F>> entries = new ArrayList<>();

    public FilterPanel(String id, JpaPluginLocator<T, ID, F> pluginLocator) {
        super(id);
        Form<Void> form = new Form<Void>("form") {
            @Override
            protected void onSubmit() {
                sendFilterChangedEvent();
            }
        };
        add(form);
        RepeatingView filters = new RepeatingView("filters");
        form.add(filters);
        form.add(new Link<Void>("clear") {
            @Override
            public void onClick() {
                for (FilterPluginEntry<T, ID, F> entry : entries) {
                    entry.setFilter(entry.getPlugin().newFilter());
                }
                sendFilterChangedEvent();
            }
        });
        for (FilterPlugin<T, ID, F> plugin : pluginLocator.getPlugin().getFilterPlugins()) {
            FilterPluginEntry<T, ID, F> entry = new FilterPluginEntry<>(pluginLocator, plugin);
            entries.add(entry);
            IModel<F> filterModel = new PropertyModel<F>(entry, "filter");
            filters.add(plugin.newFilterPanel(filters.newChildId(), filterModel));
        }
    }

    private void sendFilterChangedEvent() {
        send(getPage(), Broadcast.BREADTH, new FilterChanged<T, ID, F>(entries));
    }

    public static class FilterChanged<T extends Persistable<ID>, ID extends Serializable, F extends Serializable> implements Serializable {
        private final List<FilterPluginEntry<T, ID, F>> entries;

        public FilterChanged(List<FilterPluginEntry<T, ID, F>> entries) {
            this.entries = entries;
        }

        public List<FilterPluginEntry<T, ID, F>> getEntries() {
            return entries;
        }
    }

    public static class FilterPluginEntry<T extends Persistable<ID>, ID extends Serializable, F extends Serializable>
            implements Serializable {

        private final JpaPluginLocator<T, ID, F> pluginLocator;
        private final String pluginId;
        private F filter;

        public FilterPluginEntry(JpaPluginLocator<T, ID, F> pluginLocator, FilterPlugin<T, ID, F> plugin) {
            this.pluginLocator = pluginLocator;
            this.pluginId = plugin.getPluginId();
            this.filter = plugin.newFilter();
        }

        public FilterPlugin<T, ID, F> getPlugin() {
            return pluginLocator.getPlugin().getFilterPluginById(pluginId);
        }

        public void setFilter(F filter) {
            this.filter = filter;
        }

        public F getFilter() {
            return filter;
        }
    }

}
