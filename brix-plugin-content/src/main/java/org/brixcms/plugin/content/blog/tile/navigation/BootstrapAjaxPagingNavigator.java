
package org.brixcms.plugin.content.blog.tile.navigation;

import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.IPagingLabelProvider;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigation;

@SuppressWarnings("serial")
public class BootstrapAjaxPagingNavigator extends AjaxPagingNavigator {

    public BootstrapAjaxPagingNavigator(final String id, final IPageable pageable) {
        super(id, pageable, null);
    }

    @Override
    protected PagingNavigation newNavigation(String id, IPageable pageable, IPagingLabelProvider labelProvider) {
        return new BootstrapAjaxPagingNavigation(id, pageable, labelProvider);
    }

}
