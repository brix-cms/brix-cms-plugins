package org.brixcms.plugin.content.resource;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.io.IOUtils;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.time.Time;
import org.brixcms.Brix;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.wrapper.BrixFileNode;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.content.resource.admin.ResourceNodePlugin;
import org.brixcms.plugin.hierarchical.nodes.TitledNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class FileResourceReference extends ResourceReference {

    public static FileResourceReference INSTANCE = new FileResourceReference();

    private FileResourceReference() {
        super(FileResourceReference.class, FileResourceReference.class.getName());
    }

    @Override
    public IResource getResource() {
        return new FileResource();
    }

    public static class FileResource extends AbstractResource {

        private final static Logger LOG = LoggerFactory.getLogger(FileResource.class);

        private BrixNode getNode(Attributes attributes) {
            String workspace = attributes.getParameters().get(ResourceNodePlugin.WS_PARAM_NAME).toOptionalString();
            String id = attributes.getParameters().get(ResourceNodePlugin.ID_PARAM_NAME).toOptionalString();
            if (!Strings.isEmpty(workspace) && !Strings.isEmpty(id)) {
                try {
                    return (BrixNode) Brix.get().getCurrentSession(workspace).getNodeByIdentifier(id);
                } catch (Exception e) {
                    LOG.info("File in workspace {} with id {} was not found due to " + e.getMessage(), workspace, id);
                }
            }
            return null;
        }

        @Override
        protected ResourceResponse newResourceResponse(Attributes attributes) {
            final ResourceResponse response = new ResourceResponse();
            BrixNode node = getNode(attributes);
            if (node != null) {
                if (BrixFileNode.isFileNode(node)) {
                    BrixFileNode fileNode = new BrixFileNode(node, node.getSession());
                    response.setContentType(fileNode.getMimeType());
                    response.setLastModified(Time.valueOf(fileNode.getCreated()));
                    response.setContentLength(fileNode.getContentLength());
                    if (ResourceUtils.isImage(fileNode)) {
                        response.setContentDisposition(ContentDisposition.INLINE);
                    } else {
                        response.setFileName(getNodeTitle(node));
                        response.setContentDisposition(ContentDisposition.ATTACHMENT);
                    }
                    final byte[] data;
                    try {
                        data = IOUtils.toByteArray(fileNode.getDataAsStream());
                    } catch (IOException e) {
                        LOG.error(e.getMessage(), e);
                        response.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        return response;
                    }
                    if (response.dataNeedsToBeWritten(attributes)) {
                        response.setWriteCallback(new WriteCallback() {
                            @Override
                            public void writeData(final Attributes attributes) {
                                attributes.getResponse().write(data);
                            }
                        });
                    }
                } else {
                    response.setError(HttpServletResponse.SC_NOT_FOUND);
                }
            } else {
                response.setError(HttpServletResponse.SC_NOT_FOUND);
            }
            return response;
        }

        private String getNodeTitle(JcrNode node) {
            if (node instanceof TitledNode) {
                return ((TitledNode) node).getTitle();
            }
            return node.getName();
        }

    }

}
