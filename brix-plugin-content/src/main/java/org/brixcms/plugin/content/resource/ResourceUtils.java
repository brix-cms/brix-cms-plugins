package org.brixcms.plugin.content.resource;

import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.Imaging;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.wrapper.BrixFileNode;
import org.brixcms.plugin.content.resource.admin.ResourceNodePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceUtils {

    private final static Logger LOG = LoggerFactory.getLogger(ResourceUtils.class);

    public static PageParameters getResourceParameters(JcrNode jcrNode) {
        PageParameters parameters = new PageParameters();
        if (jcrNode != null) {
            parameters.add(ResourceNodePlugin.WS_PARAM_NAME, jcrNode.getSession().getWorkspace().getName());
            parameters.add(ResourceNodePlugin.ID_PARAM_NAME, jcrNode.getIdentifier());
        }
        return parameters;
    }

    public static String getResolution(BrixFileNode node) {
        if (isImage(node)) {
            try {
                ImageInfo ii = Imaging.getImageInfo(node.getDataAsStream(), node.getName());
                return ii.getWidth() + " x " + ii.getHeight() + " pixels, " + ii.getBitsPerPixel() + " bits per pixel";
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                return e.getMessage();
            }
        }
        return null;
    }

    public static boolean isImage(BrixFileNode fileNode) {
        if (fileNode != null && fileNode.getMimeType().contains("image")) {
            return true;
        }
        return false;
    }

}
