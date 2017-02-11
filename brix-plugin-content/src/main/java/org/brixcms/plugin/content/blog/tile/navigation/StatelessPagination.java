package org.brixcms.plugin.content.blog.tile.navigation;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.list.LoopItem;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.IPagingLabelProvider;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigation;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.brixcms.web.nodepage.BrixPageParameters;
import org.brixcms.web.nodepage.PageParametersAware;

@SuppressWarnings({ "serial", "rawtypes" })
public class StatelessPagination extends PagingNavigator implements PageParametersAware {

    public static final String PAGE_PARAM_NAME = "page";

    protected BrixPageParameters pageParameters;

    public StatelessPagination(String id, IPageable pageable) {
        super(id, pageable);
    }

    @Override
    protected AbstractLink newPagingNavigationLink(String id, IPageable pageable, int pageNumber) {
        return new StatelessPagingNavigationLink(id, pageable, pageNumber) {
            @Override
            protected BrixPageParameters getInitialParameters() {
                return pageParameters;
            }
        };
    }

    @Override
    protected AbstractLink newPagingNavigationIncrementLink(String id, IPageable pageable, int increment) {
        return new StatelessPagingNavigationIncrementLink(id, pageable, increment) {
            @Override
            protected BrixPageParameters getInitialParameters() {
                return pageParameters;
            }
        };
    }

    @Override
    protected PagingNavigation newNavigation(String id, IPageable pageable, IPagingLabelProvider labelProvider) {
        return new PagingNavigation(id, pageable, labelProvider) {
            @Override
            protected AbstractLink newPagingNavigationLink(String id, IPageable pageable, long pageIndex) {
                return new StatelessPagingNavigationLink(id, pageable, (int) pageIndex) {
                    @Override
                    protected BrixPageParameters getInitialParameters() {
                        return pageParameters;
                    }
                };
            }

            /** Attribute for active state */
            private final AttributeModifier activeAttribute = AttributeModifier.append("class", "active");

            @Override
            protected void populateItem(final LoopItem loopItem) {
                super.populateItem(loopItem);
                if ((getStartIndex() + loopItem.getIndex()) == pageable.getCurrentPage()) {
                    loopItem.add(activeAttribute);
                }
            }

        };
    }

    @Override
    public void initializeFromPageParameters(BrixPageParameters params) {
        pageParameters = params;
    }

    @Override
    public void contributeToPageParameters(BrixPageParameters params) {
    }

}
