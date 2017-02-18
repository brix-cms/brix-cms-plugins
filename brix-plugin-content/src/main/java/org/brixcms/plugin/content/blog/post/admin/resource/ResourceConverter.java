package org.brixcms.plugin.content.blog.post.admin.resource;

import java.util.Locale;

import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.string.Strings;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.plugin.content.blog.post.PostNode;
import org.brixcms.plugin.content.resource.FileResourceReference;
import org.brixcms.plugin.content.resource.ResourceUtils;
import org.brixcms.web.generic.BrixGenericPanel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class ResourceConverter implements IConverter<String> {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceConverter.class);

    private static final String MEDIA = "[src]";
    private static final String LINKS = "a[href]";

    private final BrixGenericPanel<PostNode> panel;

    public ResourceConverter(BrixGenericPanel<PostNode> panel) {
        this.panel = panel;
    }

    @Override
    public String convertToObject(String content, Locale locale) throws ConversionException {
        return content;
    }

    @Override
    public String convertToString(String content, Locale locale) {
        if (!Strings.isEmpty(content)) {
            Document doc = Jsoup.parseBodyFragment(content);

            for (Element img : doc.select(MEDIA)) {
                processElement(img, "src");
            }
            for (Element link : doc.select(LINKS)) {
                processElement(link, "href");
            }
            // TODO doesn't work for images
            // return Jsoup.clean(doc.body().html(),
            // Whitelist.basicWithImages());
            return doc.body().html();
        }
        return content;

    }

    private void processElement(Element img, String attr) {
        String nodeId = img.attr("id");
        if (nodeId != null) {
            JcrNode jcrNode = null;
            try {
                jcrNode = panel.getModelObject().getSession().getNodeByIdentifier(nodeId);
            } catch (Exception e) {
                LOG.warn("Resource from post {} not found " + e.getMessage(), panel.getModelObject());
            }
            if (jcrNode != null) {
                String fileURL = panel.urlFor(FileResourceReference.INSTANCE, ResourceUtils.getResourceParameters(jcrNode)).toString();
                img.attr(attr, fileURL);
            }
        }
    }

}
