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

package org.brixcms.plugin.content.resource.image.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.auth.Action.Context;
import org.brixcms.jcr.wrapper.BrixFileNode;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.jcr.wrapper.ResourceNode;
import org.brixcms.plugin.content.ContentPlugin;
import org.brixcms.plugin.content.resource.ResourceUtils;
import org.brixcms.plugin.site.ManageNodeTabFactory;
import org.brixcms.web.tab.CachingAbstractTab;
import org.brixcms.web.tab.IBrixTab;

@SuppressWarnings("serial")
public class ManageImageTabFactory implements ManageNodeTabFactory {
    
    private static List<IBrixTab> getTabs(final IModel<BrixNode> nodeModel) {
        List<IBrixTab> tabs = new ArrayList<IBrixTab>();

        tabs.add(new CachingAbstractTab(new ResourceModel("view", "View"), 100) {
            @Override
            public Panel newPanel(String panelId) {
                return new ViewImagePanel(panelId, nodeModel);
            }
        });

        return tabs;
    }

    @Override
    public List<IBrixTab> getManageNodeTabs(IModel<BrixNode> nodeModel) {
        BrixNode node = nodeModel.getObject();
        if (node instanceof ResourceNode && hasViewPermission(nodeModel)) {
            if (ResourceUtils.isImage((BrixFileNode) node)) {
                return getTabs(nodeModel);
            }
        }
        return null;
    }

    private static boolean hasViewPermission(IModel<BrixNode> model) {
        return ContentPlugin.get().canViewNode(model.getObject(), Context.ADMINISTRATION);
    }

}
