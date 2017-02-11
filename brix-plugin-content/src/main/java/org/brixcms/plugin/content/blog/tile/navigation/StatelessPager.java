package org.brixcms.plugin.content.blog.tile.navigation;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.IPagingLabelProvider;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigation;
import org.brixcms.web.util.DisabledClassAppender;

@SuppressWarnings("serial")
public class StatelessPager extends StatelessPagination {

    public StatelessPager(String id, IPageable pageable) {
        super(id, pageable);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        WebMarkupContainer prevLi = new WebMarkupContainer("prevLi");
        AbstractLink prevLink = super.newPagingNavigationIncrementLink("prev", getPageable(), -1);
        prevLi.add(prevLink);
        prevLi.add(new DisabledClassAppender(prevLink));
        WebMarkupContainer nextLi = new WebMarkupContainer("nextLi");
        AbstractLink nextLink = super.newPagingNavigationIncrementLink("next", getPageable(), 1);
        nextLi.add(nextLink);
        nextLi.add(new DisabledClassAppender(nextLink));
        add(prevLi);
        add(nextLi);
    }

    @Override
    protected AbstractLink newPagingNavigationLink(String id, IPageable pageable, int pageNumber) {
        AbstractLink link = super.newPagingNavigationLink(id, pageable, pageNumber);
        link.setVisible(false);
        return link;
    }

    @Override
    protected AbstractLink newPagingNavigationIncrementLink(String id, IPageable pageable, int increment) {
        AbstractLink link = super.newPagingNavigationIncrementLink(id, pageable, increment);
        link.setVisible(false);
        return link;
    }

    @Override
    protected PagingNavigation newNavigation(String id, IPageable pageable, IPagingLabelProvider labelProvider) {
        PagingNavigation navigation = super.newNavigation(id, pageable, labelProvider);
        navigation.setVisible(false);
        return navigation;
    }

}
