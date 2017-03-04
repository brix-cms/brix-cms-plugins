package org.brixcms.plugin.demo;

import javax.jcr.ImportUUIDBehavior;

import org.apache.wicket.Page;
import org.apache.wicket.request.cycle.RequestCycle;
import org.brixcms.Brix;
import org.brixcms.Path;
import org.brixcms.config.BrixConfig;
import org.brixcms.config.PrefixUriMapper;
import org.brixcms.config.UriMapper;
import org.brixcms.jcr.JcrSessionFactory;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.plugin.content.ContentPlugin;
import org.brixcms.plugin.demo.web.AbstractWicketApplication;
import org.brixcms.plugin.demo.web.DemoBrix;
import org.brixcms.plugin.demo.web.admin.AdminPage;
import org.brixcms.plugin.demo.web.signin.BrixSignInPage;
import org.brixcms.plugin.demo.web.signin.BrixSignOutPage;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.plugin.usermgmt.role.Role;
import org.brixcms.plugin.usermgmt.role.RoleRepository;
import org.brixcms.plugin.usermgmt.user.User;
import org.brixcms.plugin.usermgmt.user.UserRepository;
import org.brixcms.web.BrixRequestMapper;
import org.brixcms.workspace.Workspace;
import org.brixcms.workspace.WorkspaceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Application object for your web application.
 */
@SpringBootApplication
@ComponentScan({ "org.brixcms.plugin.*" })
@EntityScan("org.brixcms.plugin.*")
@EnableJpaRepositories("org.brixcms.plugin.*")
public class BrixApplication extends AbstractWicketApplication {
    private static final Logger logger = LoggerFactory.getLogger(BrixApplication.class);

    /**
     * brix instance
     */
    private Brix brix;

    public Brix getBrix() {
        return brix;
    }

    /**
     * A main() so we can easily run this app in our IDE
     */
    public static void main(String[] args) {
        SpringApplication.run(BrixApplication.class, args);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends Page> getHomePage() {
        // use special class so that the URL coding strategy knows we want to go
        // home
        // it is not possible to just return null here because some pages (e.g.
        // expired page)
        // rely on knowing the home page
        return BrixRequestMapper.HomePage.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void init() {
        super.init();

        final JcrSessionFactory sf = getJcrSessionFactory();
        final WorkspaceManager wm = getWorkspaceManager();

        getDebugSettings().setOutputMarkupContainerClassName(true);

        try {
            // create uri mapper for the cms
            // we are mounting the cms on the root, and getting the workspace
            // name from the
            // application properties
            UriMapper mapper = new PrefixUriMapper(Path.ROOT) {
                @Override
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

            // setRootRequestMapper(new HttpsMapper(new SystemMapper(this), new
            // HttpsConfig(config.getHttpPort(), config.getHttpsPort())));

            // create brix instance and attach it to this application
            brix = new DemoBrix(config, getApplicationContext());
            brix.attachTo(this);
            initializeRepository();
            initDefaultWorkspaces();

        } catch (Exception e) {
            logger.error("Exception in WicketApplication init()", e);
        } finally {
            // since we accessed session factory we also have to perform cleanup
            cleanupSessionFactory();
        }

        // mount admin page
        mountPage("/admin", AdminPage.class);
        mountPage("/signin", BrixSignInPage.class);
        mountPage("/signout", BrixSignOutPage.class);

    }

    /**
     * Allow Brix to perform repository initialization
     */
    private void initializeRepository() {
        try {
            brix.initRepository();
        } finally {
            // cleanup any sessions we might have created
            cleanupSessionFactory();
        }
    }

    private void initDefaultWorkspaces() {
        try {
            final String defaultState = getProperties().getWorkspaceDefaultState();
            final String wn = getProperties().getJcrDefaultWorkspace();
            final SitePlugin sp = SitePlugin.get(brix);

            if (!sp.siteExists(wn, defaultState)) {
                Workspace w = sp.createSite(wn, defaultState);
                JcrSession session = brix.getCurrentSession(w.getId());

                session.importXML("/", getClass().getResourceAsStream("/org/brixcms/demo/web/workspace.xml"),
                        ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING);

                brix.initWorkspace(w, session);

                session.save();
            }
            initContentPluginWorkspace(wn, defaultState);
        } catch (Exception e) {
            throw new RuntimeException("Could not initialize jackrabbit workspace with Brix", e);
        }
    }

    private void initContentPluginWorkspace(String siteWorkspaceName, String defaultState) {
        try {
            final String wn = siteWorkspaceName + ".ContenPlugin";
            final ContentPlugin cp = ContentPlugin.get(brix);

            if (!cp.contentExists(wn, defaultState)) {
                Workspace w = cp.createContent(wn, defaultState);
                JcrSession session = brix.getCurrentSession(w.getId());

                session.importXML("/", getClass().getResourceAsStream("/org/brixcms/demo/web/workspace_content.xml"),
                        ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING);

                brix.initWorkspace(w, session);

                session.save();
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not initialize jackrabbit workspace with Brix", e);
        }
    }

    @Bean
    public CommandLineRunner initUserManagement(UserRepository userRepository, RoleRepository roleRepository) {
        return (args) -> {
            userRepository.save(createUser("admin", "admin", "admin@brixcms.org", true));
            userRepository.save(createUser("editor", "editor", "editor@brixcms.org", true));
            for (int i = 1; i <= 15; i++) {
                userRepository.save(createUser("user", "user" + i, "user" + i + "@brixcms.org", false));
            }
            roleRepository.save(createRole("admin"));
            roleRepository.save(createRole("editor"));
        };
    }

    private User createUser(String username, String password, String email, boolean verified) {
        logger.info("Creating user '{}' with email '{}'.", username, email);
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setVerified(verified);
        return user;
    }

    private Role createRole(String name) {
        logger.info("Creating role '{}'", name);
        Role role = new Role();
        role.setName(name);
        return role;
    }

}
