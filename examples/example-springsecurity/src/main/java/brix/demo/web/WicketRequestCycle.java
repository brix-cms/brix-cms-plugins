package brix.demo.web;

import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.cycle.RequestCycleContext;

import javax.jcr.Session;

/**
 * Subclass of {@link WebRequestCycle} that cleans any open Jcr {@link Session}s at the end of request
 *
 * @author igor.vaynberg
 */
public class WicketRequestCycle extends RequestCycle {
// --------------------------- CONSTRUCTORS ---------------------------


    public WicketRequestCycle(RequestCycleContext context) {
        super(context);
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
