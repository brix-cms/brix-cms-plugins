package org.brixcms.plugin.jpa.web.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.model.IModel;
import org.brixcms.plugin.jpa.JpaPluginLocator;
import org.brixcms.plugin.jpa.Persistable;
import org.brixcms.plugin.site.admin.NodeManagerTabbedPanel;
import org.brixcms.web.BrixFeedbackPanel;
import org.brixcms.web.generic.BrixGenericPanel;
import org.brixcms.web.tab.IBrixTab;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class EntityManagerEditorPanel<T extends Persistable<ID>, ID extends Serializable, F extends Serializable> extends BrixGenericPanel<T> {

    private final JpaPluginLocator<T, ID, F> pluginLocator;

    public EntityManagerEditorPanel(String id, IModel<T> model, final JpaPluginLocator<T, ID, F> pluginLocator) {
        super(id, model);
        this.pluginLocator = pluginLocator;
        add(new SessionFeedbackPanel("sessionFeedback"));
        add(new NodeManagerTabbedPanel("tabbedPanel", getTabs(getModel())));
    }

    private List<IBrixTab> getTabs(IModel<T> entityModel) {
        T entity = entityModel.getObject();

        final Collection<ManageEntityTabFactory<T>> factories;
        if (entity != null) {
            factories = pluginLocator.getPlugin().getManageEntityTabFactories();
        } else {
            factories = Collections.emptyList();
        }

        if (!factories.isEmpty()) {
            List<IBrixTab> result = new ArrayList<IBrixTab>();
            for (ManageEntityTabFactory<T> f : factories) {
                List<IBrixTab> tabs = f.getManageNodeTabs(entityModel);
                if (tabs != null) {
                    result.addAll(tabs);
                }
            }
            return result;
        } else {
            return Collections.emptyList();
        }
    }

    private static class SessionFeedbackPanel extends BrixFeedbackPanel {

        public SessionFeedbackPanel(String id) {
            super(id, new Filter());
        }

        @Override
        public boolean isVisible() {
            List<?> messages = getFeedbackMessagesModel().getObject();
            return messages != null && !messages.isEmpty();
        }

        private static class Filter implements IFeedbackMessageFilter {
            public boolean accept(FeedbackMessage message) {
                return message.getReporter() == null;
            }
        };
    };
}
