package brix.demo.web;

import org.apache.wicket.Response;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;

import javax.jcr.Session;

/**
 * Subclass of {@link WebRequestCycle} that cleans any open Jcr {@link Session}s at the end of request
 *
 * @author igor.vaynberg
 */
public class WicketRequestCycle extends WebRequestCycle {
// --------------------------- CONSTRUCTORS ---------------------------

    /**
     * Constructor
     *
     * @param application
     * @param request
     * @param response
     */
    public WicketRequestCycle(WebApplication application, WebRequest request, Response response) {
        super(application, request, response);
    }

// -------------------------- OTHER METHODS --------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onEndRequest() {
        // clean up sessions
        AbstractWicketApplication.get().cleanupSessionFactory();
    }
}
