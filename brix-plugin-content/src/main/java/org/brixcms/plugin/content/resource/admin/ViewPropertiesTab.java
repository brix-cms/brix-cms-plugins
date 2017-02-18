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

package org.brixcms.plugin.content.resource.admin;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.lang.Bytes;
import org.brixcms.jcr.wrapper.BrixFileNode;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.content.resource.ResourceUtils;
import org.brixcms.plugin.site.resource.ResourceNodeHandler;
import org.brixcms.web.generic.BrixGenericPanel;

@SuppressWarnings("serial")
public class ViewPropertiesTab extends BrixGenericPanel<BrixNode> {
    public ViewPropertiesTab(String id, final IModel<BrixNode> nodeModel) {
        super(id, nodeModel);
        add(new Label("size", new Model<String>() {

            @Override
            public String getObject() {
                return Bytes.bytes(getFileNode().getContentLength()).toString() + " bytes";
            }
        }));
        add(new Label("resolution", new Model<String>() {

            @Override
            public String getObject() {
                return ResourceUtils.getResolution(getFileNode());
            }
        }) {

            @Override
            public boolean isVisible() {
                return ResourceUtils.isImage(getFileNode());
            }
        });
        add(new Label("identifier", new PropertyModel<String>(getModel(), "identifier")));
        add(new Label("mimeType", new Model<String>() {
            @Override
            public String getObject() {
                BrixFileNode node = (BrixFileNode) nodeModel.getObject();
                return node.getMimeType();
            }
        }));
        add(new Link<Void>("download") {
            @Override
            public void onClick() {
                getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceNodeHandler(nodeModel, true));
            }
        });
    }

    private BrixFileNode getFileNode() {
        return (BrixFileNode) getModelObject();
    }

}
