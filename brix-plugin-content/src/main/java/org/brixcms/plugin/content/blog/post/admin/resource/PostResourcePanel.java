package org.brixcms.plugin.content.blog.post.admin.resource;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.brixcms.jcr.wrapper.BrixFileNode;
import org.brixcms.plugin.content.resource.FileResourceReference;
import org.brixcms.plugin.content.resource.ResourceUtils;
import org.brixcms.web.generic.BrixGenericPanel;

@SuppressWarnings("serial")
class PostResourcePanel extends BrixGenericPanel<BrixFileNode> {

    public PostResourcePanel(String id, IModel<BrixFileNode> model) {
        super(id, model);
        final String url = urlFor(FileResourceReference.INSTANCE, ResourceUtils.getResourceParameters(getModelObject())).toString();
        final String identifier = getModelObject().getIdentifier();
        final String name = getModelObject().getName();

        add(new Label("html", new IModel<String>() {
            @Override
            public String getObject() {
                if (ResourceUtils.isImage(getModelObject())) {
                    return createImgHtml(url, identifier, name);
                } else {
                    return createLinkHtml(url, identifier, name);
                }
            }
        }));
    }

    private String createImgHtml(String url, String id, String name) {
        StringBuilder builder = new StringBuilder();
        builder.append("<img src=\"");
        builder.append(url);
        builder.append("\" id=\"");
        builder.append(id);
        builder.append("\" alt=\"");
        builder.append(name);
        builder.append("\" class=\"img-responsive\" />");
        return builder.toString();
    }

    private String createLinkHtml(String url, String id, String name) {
        StringBuilder builder = new StringBuilder();
        builder.append("<a href=\"");
        builder.append(url);
        builder.append("\" id=\"");
        builder.append(id);
        builder.append("\">");
        builder.append(name);
        builder.append("</a>");
        return builder.toString();
    }

}
