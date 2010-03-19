package brix.plugin.hierarchical;

import javax.jcr.NamespaceRegistry;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brix.Brix;
import brix.BrixRepositoryInitializer;
import brix.jcr.RepositoryInitializer;

public class HierarchicalRepoInitializer implements RepositoryInitializer
{
    private static final Logger logger = LoggerFactory.getLogger(BrixRepositoryInitializer.class);

    public void initializeRepository(Brix brix, Session session) throws RepositoryException {
		final Workspace w = session.getWorkspace();
		NamespaceRegistry nr = w.getNamespaceRegistry();

		try {
			logger.info("Registering HierarchicalNodePlugin JCR Namespace: {}", HierarchicalNodePlugin.NAMESPACE);
			nr.registerNamespace(HierarchicalNodePlugin.NAMESPACE, "http://brix-cms-plugins.googlecode.com");
		} catch (Exception ignore) {
			 //log.warn("Error registering brix namespace, may already be registered", ignore);
		}
	}

}
