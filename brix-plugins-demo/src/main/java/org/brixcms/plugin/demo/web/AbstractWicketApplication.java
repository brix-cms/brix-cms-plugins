
package org.brixcms.plugin.demo.web;

import javax.jcr.Repository;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.authroles.authorization.strategies.role.metadata.MetaDataRoleAuthorizationStrategy;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.brixcms.jcr.ThreadLocalSessionFactory;
import org.brixcms.plugin.demo.ApplicationProperties;
import org.brixcms.plugin.demo.web.admin.AdminPage;
import org.brixcms.plugin.demo.web.signin.BrixSignInPage;
import org.brixcms.util.JcrUtils;
import org.brixcms.workspace.WorkspaceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * Factors out noise not necessary to demonstrating how to install Brix into a
 * Wicket application. This class takes care of peripheral duties such as
 * creating the Jcr repository, setting up JcrSessionFactory, etc.
 *
 * @author igor.vaynberg 
 * @author dan.simko@gmail.com
 */
public abstract class AbstractWicketApplication extends AuthenticatedWebApplication {

    private static final Logger logger = LoggerFactory.getLogger(AbstractWicketApplication.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ApplicationProperties properties;

    /**
     * jcr repository
     */
    private Repository repository;

    /**
     * jcr session factory. sessions created by this factory are cleaned up by
     * {@link WicketRequestCycleListener}
     */
    private ThreadLocalSessionFactory sessionFactory;

    /**
     * workspace manager to be used by brix
     */
    private WorkspaceManager workspaceManager;

    /**
     * @return application instance
     */
    public static AbstractWicketApplication get() {
        return (AbstractWicketApplication) WebApplication.get();
    }

    /**
     * @return application properties
     */
    public final ApplicationProperties getProperties() {
        return properties;
    }

    /**
     * @return jcr repository
     */
    public final Repository getRepository() {
        return repository;
    }

    /**
     * @return workspace manager
     */
    public final WorkspaceManager getWorkspaceManager() {
        return workspaceManager;
    }

    /**
     * @return jcr session factory
     */
    public final ThreadLocalSessionFactory getJcrSessionFactory() {
        return sessionFactory;
    }

    @Override
    protected void init() {
        super.init();

        getComponentInstantiationListeners().add(new SpringComponentInjector(this, applicationContext));

        logger.info("Using JCR repository url: " + properties.getJcrRepositoryUrl());

        // create jcr repository
        repository = JcrUtils.createRepository(properties.getJcrRepositoryUrl());

        // create session factory that will be used to feed brix jcr sessions
        sessionFactory = new DemoThreadLocalSessionFactory(repository, properties.buildSimpleCredentials());

        try {
            // create workspace manager brix will use to access
            // workspace-related functionality
            workspaceManager = JcrUtils.createWorkspaceManager(properties.getWorkspaceManagerUrl(), sessionFactory);
        } finally {
            // since creating workspace manager may require access to session we
            // need to clean up
            cleanupSessionFactory();
        }

        getMarkupSettings().setStripWicketTags(true);
        getRequestCycleListeners().add(new WicketRequestCycleListener());
        MetaDataRoleAuthorizationStrategy.authorize(AdminPage.class, Roles.USER);
    }

    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
        return AuthenticatedSession.class;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return BrixSignInPage.class;
    }

    /**
     * cleans up any opened sessions in session factory
     */
    public final void cleanupSessionFactory() {
        sessionFactory.cleanup();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy() {
        // shutdown the repository cleanly
        if (repository instanceof RepositoryImpl) {
            logger.info("Shutting down JackRabbit repository...");
            ((RepositoryImpl) repository).shutdown();
        }
        super.onDestroy();
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
