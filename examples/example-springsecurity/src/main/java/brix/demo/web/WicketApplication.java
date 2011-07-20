package brix.demo.web;

import brix.demo.model.Member;
import brix.demo.model.Role;
import brix.demo.service.UserDAO;
import brix.demo.web.admin.AdminPage;
import brix.demo.web.auth.LoginPage;
import brix.demo.web.auth.LogoutPage;
import brix.plugins.springsecurity.AuthorizationStrategyImpl;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.IAuthorizationStrategy;
import org.apache.wicket.authorization.IUnauthorizedComponentInstantiationListener;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.brixcms.Brix;
import org.brixcms.Path;
import org.brixcms.config.BrixConfig;
import org.brixcms.config.PrefixUriMapper;
import org.brixcms.config.UriMapper;
import org.brixcms.jcr.JcrSessionFactory;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.web.BrixRequestCycleProcessor;
import org.brixcms.web.nodepage.BrixNodePageUrlMapper;
import org.brixcms.workspace.Workspace;
import org.brixcms.workspace.WorkspaceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.jcr.ImportUUIDBehavior;
import java.util.ArrayList;
import java.util.List;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 *
 */
public final class WicketApplication extends AbstractWicketApplication {
// ------------------------------ FIELDS ------------------------------

    private static final Logger log = LoggerFactory.getLogger(WicketApplication.class);

    public AuthorizationStrategyImpl authorizationStrategy;

    /**
     * brix instance
     */
    private Brix brix;
    private UserDAO userDAO;

// --------------------- GETTER / SETTER METHODS ---------------------

    public Brix getBrix() {
        return brix;
    }

    public void setAuthorizationStrategy(AuthorizationStrategyImpl authorizationStrategy) {
        this.authorizationStrategy = authorizationStrategy;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

// -------------------------- OTHER METHODS --------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends Page> getHomePage() {
        // use special class so that the URL coding strategy knows we want to go home
        // it is not possible to just return null here because some pages (e.g. expired page)
        // rely on knowing the home page
        return BrixNodePageUrlMapper.HomePage.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void init() {
        super.init();

        getComponentInstantiationListeners().add(new SpringComponentInjector(this));

        final JcrSessionFactory sf = getJcrSessionFactory();
        final WorkspaceManager wm = getWorkspaceManager();

        try {
            // create uri mapper for the cms
            // we are mounting the cms on the root, and getting the workspace name from the
            // application properties
            UriMapper mapper = new PrefixUriMapper(Path.ROOT) {
                public Workspace getWorkspaceForRequest(RequestCycle requestCycle, Brix brix) {
                    final String name = getProperties().getJcrDefaultWorkspace();
                    SitePlugin sitePlugin = SitePlugin.get(brix);
                    return sitePlugin.getSiteWorkspace(name, getProperties().getWorkspaceDefaultState());
                }
            };

            // create brix configuration
            BrixConfig config = new BrixConfig(sf, wm, mapper);
            config.setHttpPort(getProperties().getHttpPort());
            config.setHttpsPort(getProperties().getHttpsPort());

            // create brix instance and attach it to this application
            brix = new DemoBrix(config, authorizationStrategy);
            brix.attachTo(this);
            initializeRepository();
            initDefaultWorkspace();
            createInitialUsers();
        }
        catch (Throwable e) {
            log.error("Exception in WicketApplication init()", e);
        }
        finally {
            // since we accessed session factory we also have to perform cleanup
            cleanupSessionFactory();
        }

        // mount admin page
        mount(new QueryStringHybridUrlCodingStrategy("/admin", AdminPage.class));
        mount(new QueryStringHybridUrlCodingStrategy("/logout", LogoutPage.class));

        getSecuritySettings().setAuthorizationStrategy(new IAuthorizationStrategy() {
            public boolean isActionAuthorized(Component component, Action action) {
                return true;
            }

            public <T extends Component> boolean isInstantiationAuthorized(Class<T> componentClass) {
                boolean result = true;
                if (componentClass.equals(AdminPage.class)) {
                    List<ConfigAttribute> attrs = new ArrayList<ConfigAttribute>();
                    attrs.add(new AuthorizationStrategyImpl.ConfigAttributeImpl("ROLE_EDITOR"));
                    attrs.add(new AuthorizationStrategyImpl.ConfigAttributeImpl("ROLE_SUPERUSER"));
                    result = authorizationStrategy.userHasAuthority(componentClass, attrs);
                }
                return result;
            }
        });

        getSecuritySettings().setUnauthorizedComponentInstantiationListener(new IUnauthorizedComponentInstantiationListener() {
            public void onUnauthorizedInstantiation(Component component) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication == null || !authentication.isAuthenticated()) {
                    throw new RestartResponseAtInterceptPageException(LoginPage.class);
                } else {
                    throw new UnauthorizedInstantiationException(component.getClass());                    
                }
            }
        });

        // FIXME matej: do we need this?
        // mountBookmarkablePage("/NotFound", ResourceNotFoundPage.class);
        // mountBookmarkablePage("/Forbiden", ForbiddenPage.class);
    }

    /**
     * Allow Brix to perform repository initialization
     */
    private void initializeRepository() {
        try {
            brix.initRepository();
        }
        finally {
            // cleanup any sessions we might have created
            cleanupSessionFactory();
        }
    }

    private void initDefaultWorkspace() {
        try {
            final String defaultState = getProperties().getWorkspaceDefaultState();
            final String wn = getProperties().getJcrDefaultWorkspace();
            final SitePlugin sp = SitePlugin.get(brix);


            if (!sp.siteExists(wn, defaultState)) {
                Workspace w = sp.createSite(wn, defaultState);
                JcrSession session = brix.getCurrentSession(w.getId());

                session.importXML("/", getClass().getResourceAsStream("workspace.xml"), ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING);

                brix.initWorkspace(w, session);

                session.save();
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Could not initialize jackrabbit workspace with Brix", e);
        }
    }

    private void createInitialUsers() {
        Role superuser = new Role("ROLE_SUPERUSER");
        Role editor = new Role("ROLE_EDITOR");
        userDAO.saveOrUpdate(superuser);
        userDAO.saveOrUpdate(editor);
        Member member = new Member("sa", "sa", superuser, editor);
        userDAO.saveOrUpdate(member);
        member = new Member("editor", "editor", editor);
        userDAO.saveOrUpdate(member);
        member = new Member("user", "user");
        userDAO.saveOrUpdate(member);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IRequestCycleProcessor newRequestCycleProcessor() {
        /*
         * install brix request cycle processor
         * 
         * this will allow brix to take over part of wicket's url space and handle requests
         */
        return new BrixRequestCycleProcessor(brix);
    }
}
