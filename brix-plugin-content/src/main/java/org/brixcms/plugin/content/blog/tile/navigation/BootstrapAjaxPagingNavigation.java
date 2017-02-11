
package org.brixcms.plugin.content.blog.tile.navigation;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigation;
import org.apache.wicket.markup.html.list.LoopItem;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.IPagingLabelProvider;

@SuppressWarnings("serial")
public class BootstrapAjaxPagingNavigation extends AjaxPagingNavigation {

    /** Attribute for active state */
    private final AttributeModifier activeAttribute = AttributeModifier.append("class", "active");

    public BootstrapAjaxPagingNavigation(String id, IPageable pageable, IPagingLabelProvider labelProvider) {
        super(id, pageable, labelProvider);
    }

    @Override
    protected void populateItem(LoopItem loopItem) {
        super.populateItem(loopItem);
        if ((getStartIndex() + loopItem.getIndex()) == pageable.getCurrentPage()) {
            loopItem.add(activeAttribute);
        }
    }

}
