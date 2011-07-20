package brix.plugins.springsecurity;

import org.brixcms.Brix;
import org.brixcms.Plugin;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.registry.ExtensionPointRegistry;
import org.brixcms.web.tab.AbstractWorkspaceTab;
import org.brixcms.web.tab.IBrixTab;
import org.brixcms.workspace.Workspace;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Arrays;
import java.util.List;
import java.util.Collections;


/**
 * The parent class of the Spring Security User Management plugin.
 * 
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date Mar 10, 2009 4:18:11 PM
 */
public class UserPlugin implements Plugin {
// ------------------------------ FIELDS ------------------------------

    public static final String AUTH_GROUP_KEY = "AuthGroups";
    private static String ID = UserPlugin.class.getName();

    private final Brix brix;

// -------------------------- STATIC METHODS --------------------------

    public static UserPlugin get() {
        return get(Brix.get());
    }

    public static UserPlugin get(Brix brix) {
        return (UserPlugin) brix.getPlugin(ID);
    }

    public static BrixNode findPermissionsNode(BrixNode node) {
        while (node != null && !node.hasProperty(AUTH_GROUP_KEY)) {
            node = node.getDepth() > 0 ? (BrixNode) node.getParent() : null;
        }
        return node;
    }

// --------------------------- CONSTRUCTORS ---------------------------

    public UserPlugin(Brix brix) {
        this.brix = brix;
        brix.getConfig().getRegistry().lookupCollection(Plugin.POINT, new ExtensionPointRegistry.Callback<Plugin>() {
            public Status processExtension(Plugin extension) {
                if (extension.getClass().equals(SitePlugin.class)) {
                    SitePlugin sp = (SitePlugin) extension;
                    sp.registerManageNodeTabFactory(new UserNodeTabFactory());
                    return Status.STOP;
                }
                return Status.CONTINUE;
            }
        });
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Plugin ---------------------

    public String getId() {
        return ID;
    }

    public List<IBrixTab> newTabs(IModel<Workspace> workspaceModel) {
        // todo: Make this a proper user manager?
//        IBrixTab tabs[] = new IBrixTab[]{new Tab(new Model<String>("Users"), workspaceModel)};
//        return Arrays.asList(tabs);
        return Collections.emptyList();
    }

    public void initWorkspace(Workspace workspace, JcrSession workspaceSession) {
    }

    public List<Workspace> getWorkspaces(Workspace currentWorkspace, boolean isFrontend) {
        return null;
    }

    public boolean isPluginWorkspace(Workspace workspace) {
        return false;
    }

    public String getUserVisibleName(Workspace workspace, boolean isFrontend) {
        return "Users";
    }

// -------------------------- INNER CLASSES --------------------------

    static class Tab extends AbstractWorkspaceTab {
        public Tab(IModel<String> title, IModel<Workspace> workspaceModel) {
            super(title, workspaceModel, 20);
        }

        @Override
        public Panel newPanel(String panelId, IModel<Workspace> workspaceModel) {
            return new UserNodeTabFactory.UserPanel(panelId, workspaceModel);
        }
    }
}
