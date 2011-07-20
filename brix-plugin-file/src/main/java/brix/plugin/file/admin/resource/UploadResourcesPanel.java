/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package brix.plugin.file.admin.resource;

import org.brixcms.Brix;
import org.brixcms.jcr.wrapper.BrixFileNode;
import org.brixcms.jcr.wrapper.BrixNode;
import brix.plugin.hierarchical.HierarchicalPluginLocator;
import org.brixcms.plugin.site.SimpleCallback;
import org.brixcms.plugin.site.admin.NodeManagerPanel;
import org.brixcms.web.ContainerFeedbackPanel;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.MultiFileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.io.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Binary;
import javax.jcr.RepositoryException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

/**
 * @author wickeria at gmail.com
 */
public class UploadResourcesPanel extends NodeManagerPanel {

	private static final long serialVersionUID = 1L;
	private Collection<FileUpload> uploads = new ArrayList<FileUpload>();
	private boolean overwrite = false;
	private HierarchicalPluginLocator pluginLocator;
    private Logger log = LoggerFactory.getLogger(UploadResourcesPanel.class);

	public UploadResourcesPanel(String id, IModel<BrixNode> model, final SimpleCallback goBack,
			HierarchicalPluginLocator pluginLocator) {
		super(id, model);

		this.pluginLocator = pluginLocator;

		Form<?> form = new Form<UploadResourcesPanel>("form", new CompoundPropertyModel<UploadResourcesPanel>(this));
		add(form);

		form.add(new ContainerFeedbackPanel("feedback", this));

		form.add(new SubmitLink("upload") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				processUploads();
			}
		});

		form.add(new Link<Void>("cancel") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				goBack.execute();
			}
		});

		form.add(new MultiFileUploadField("uploads"));
		form.add(new CheckBox("overwrite"));
	}

	private void processUploads() {
		final BrixNode parentNode = getModelObject();

		for (final FileUpload upload : uploads) {
			final String fileName = upload.getClientFileName();

			if (parentNode.hasNode(fileName)) {
				if (overwrite) {
					parentNode.getNode(fileName).remove();
				} else {
					class ModelObject implements Serializable {
						@SuppressWarnings("unused")
						private String fileName = upload.getClientFileName();
					}

					getSession().error(getString("fileExists", new Model<ModelObject>(new ModelObject())));
					continue;
				}
			}

			BrixNode newNode = (BrixNode) parentNode.addNode(fileName, "nt:file");

			try {
				// copy the upload into a temp file and assign that
				// output stream to the node
				File temp = File.createTempFile(Brix.NS + "-upload-" + UUID.randomUUID().toString(), null);

				Streams.copy(upload.getInputStream(), new FileOutputStream(temp));
				upload.closeStreams();

				String mime = upload.getContentType();

				BrixFileNode file = BrixFileNode.initialize(newNode, mime);
                final FileInputStream inputStream = new FileInputStream(temp);
                file.setData(new Binary() {
                    public void dispose() {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            log.error("Problem with stream", e);
                        }
                    }

                    public InputStream getStream() throws RepositoryException {
                        return inputStream;
                    }

                    public int read(byte[] b, long position) throws IOException, RepositoryException {
                        return inputStream.read(b, (int) position, b.length);
                    }

                    public long getSize() throws RepositoryException {
                        try {
                            return inputStream.available();
                        } catch (IOException e) {
                            throw new RepositoryException("Problem with stream", e);
                        }
                    }
                });
				file.getParent().save();

			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
		pluginLocator.getPlugin().selectNode(this, parentNode, true);
	}

	@Override
	protected void onDetach() {
		uploads.clear();
		super.onDetach();
	}
}
