package brix.plugins.springsecurity;

import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.ManageNodeTabFactory;
import brix.web.tab.CachingAbstractTab;
import brix.web.tab.IBrixTab;
import brix.workspace.Workspace;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;


/**
 * TabFactory implementation for the user node
 * 
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date Mar 11, 2009 2:14:49 PM
 */
public class UserNodeTabFactory implements ManageNodeTabFactory {
// -------------------------- STATIC METHODS --------------------------

    private static CachingAbstractTab getTab(final IModel<BrixNode> nodeModel) {
        return new CachingAbstractTab(new Model<String>("Users"), -1) {
            @Override
            public Panel newPanel(String panelId) {
                return new ViewUsersTab(panelId, nodeModel);
            }
        };
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface ManageNodeTabFactory ---------------------

    public List<IBrixTab> getManageNodeTabs(final IModel<BrixNode> nodeModel) {
        List<IBrixTab> result = new ArrayList<IBrixTab>();
        result.add(getTab(nodeModel));

        return result;
    }

// -------------------------- INNER CLASSES --------------------------

    /**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
     * @version $Id$
     * @date Mar 10, 2009 4:21:27 PM
     */
    public static class UserPanel extends Panel {
        public UserPanel(String panelId, IModel<Workspace> workspaceModel) {
            super(panelId, workspaceModel);
        }
    }
}
