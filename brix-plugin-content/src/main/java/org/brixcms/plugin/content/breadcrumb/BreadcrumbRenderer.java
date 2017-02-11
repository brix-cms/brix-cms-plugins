package org.brixcms.plugin.content.breadcrumb;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.content.breadcrumb.BreadcrumbContributor.BreadcrumbItem;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.plugin.site.page.PageNode;
import org.brixcms.plugin.site.page.PageRenderingPage;
import org.brixcms.web.generic.BrixGenericWebMarkupContainer;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class BreadcrumbRenderer extends BrixGenericWebMarkupContainer<BrixNode> {

    public BreadcrumbRenderer(String id, IModel<BrixNode> tileNodeModel) {
        super(id, tileNodeModel);
    }

    @Override
    public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
        getResponse().write(createBreadcrumbHtml(createItems()));
    }

    private List<BreadcrumbItem> createItems() {
        final List<BreadcrumbItem> breadcrumbItems = new ArrayList<>();
        PageRenderingPage page = (PageRenderingPage) getPage();
        PageNode pageNode = (PageNode) page.getPageNode();
        breadcrumbItems.add(loadRootItem());
        breadcrumbItems.add(new BreadcrumbItem(pageNode.getTitle(), SitePlugin.get().getUriPathForNode(pageNode).toString()));
        getPage().visitChildren(BreadcrumbContributor.class, new IVisitor<Component, BreadcrumbContributor>() {
            @Override
            public void component(Component component, IVisit<BreadcrumbContributor> iVisit) {
                ((BreadcrumbContributor) component).contributeToBreadcrumb(breadcrumbItems);
            }
        });
        return breadcrumbItems;
    }

    private String createBreadcrumbHtml(List<BreadcrumbItem> breadcrumbItems) {
        StringBuilder builder = new StringBuilder();
        builder.append("<ol class=\"breadcrumb\">");
        for (int i = 0; i < breadcrumbItems.size() - 1; i++) {
            builder.append(createLink(breadcrumbItems.get(i)));
        }
        builder.append(createActive(breadcrumbItems.get(breadcrumbItems.size() - 1)));
        builder.append("</ol>");
        return builder.toString();
    }

    private BreadcrumbItem loadRootItem() {
        BrixNode root = (BrixNode) getModelObject().getProperty(BreadcrumbTileEditor.HOME_NODE).getNode();
        String title = getModelObject().getProperty(BreadcrumbTileEditor.HOME_TITLE).getString();
        return new BreadcrumbItem(title, SitePlugin.get().getUriPathForNode(root).toString());
    }

    private String createLink(BreadcrumbItem item) {
        return "<li><a href=" + item.getUrl() + ">" + item.getTitle() + "</a></li>";
    }

    private String createActive(BreadcrumbItem item) {
        return "<li class=\"active\">" + item.getTitle() + "</li>";
    }

}
