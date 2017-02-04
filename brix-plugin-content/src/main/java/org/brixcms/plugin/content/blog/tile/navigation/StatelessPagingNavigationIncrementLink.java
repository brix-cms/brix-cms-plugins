package org.brixcms.plugin.content.blog.tile.navigation;

import org.apache.wicket.markup.html.link.DisabledAttributeLinkBehavior;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.brixcms.web.nodepage.BrixPageParameters;
import org.brixcms.web.nodepage.PageParametersLink;

/**
 * An incremental link to a page of a PageableListView. Assuming your list view
 * navigation looks like
 * 
 * <pre>
 * 
 *   	 [first / &lt;&lt; / &lt;] 1 | 2 | 3 [&gt; / &gt;&gt; /last]
 * 
 * </pre>
 * 
 * <p>
 * and "&lt;" meaning the previous and "&lt;&lt;" goto the "current page - 5",
 * than it is this kind of incremental page links which can easily be created.
 * 
 * @param <T>
 *            type of model object
 */
public class StatelessPagingNavigationIncrementLink<T> extends PageParametersLink {
    private static final long serialVersionUID = 1L;

    /** The increment. */
    private final int increment;

    /** The PageableListView the page links are referring to. */
    protected final IPageable pageable;

    /**
     * Constructor.
     * 
     * @param id
     *            See Component
     * @param pageable
     *            The pageable component the page links are referring to
     * @param increment
     *            increment by
     */
    public StatelessPagingNavigationIncrementLink(final String id, final IPageable pageable, final int increment) {
        super(id);
        this.increment = increment;
        this.pageable = pageable;
        add(new DisabledAttributeLinkBehavior());
    }

    @Override
    protected void contributeToPageParameters(BrixPageParameters parameters) {
        super.contributeToPageParameters(parameters);
        parameters.set(StatelessPagination.PAGE_PARAM_NAME, getPageNumber());
    }

    @Override
    public boolean isEnabled() {
        return !linksTo();
    }

    /**
     * Determines the next page number for the pageable component.
     * 
     * @return the new page number
     */
    public final int getPageNumber() {
        // Determine the page number based on the current
        // PageableListView page and the increment
        int idx = (int) pageable.getCurrentPage() + increment;

        // make sure the index lies between 0 and the last page
        return Math.max(0, Math.min((int) pageable.getPageCount() - 1, idx));
    }

    /**
     * @return True if it is referring to the first page of the underlying
     *         PageableListView.
     */
    public boolean isFirst() {
        return pageable.getCurrentPage() <= 0;
    }

    /**
     * @return True if it is referring to the last page of the underlying
     *         PageableListView.
     */
    public boolean isLast() {
        return pageable.getCurrentPage() >= (pageable.getPageCount() - 1);
    }

    /**
     * Returns true if the page link links to the given page.
     * 
     * @param page
     *            ignored
     * @return True if this link links to the given page
     * @see org.apache.wicket.markup.html.link.PageLink#linksTo(org.apache.wicket.Page)
     */
    public boolean linksTo() {
        pageable.getCurrentPage();
        return ((increment < 0) && isFirst()) || ((increment > 0) && isLast());
    }
}