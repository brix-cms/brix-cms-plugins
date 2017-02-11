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

package org.brixcms.demo.web;

import org.brixcms.Brix;
import org.brixcms.Plugin;
import org.brixcms.auth.AuthorizationStrategy;
import org.brixcms.config.BrixConfig;
import org.brixcms.plugin.content.ContentPlugin;
import org.brixcms.plugin.menu.MenuPlugin;
import org.brixcms.plugin.prototype.PrototypePlugin;
import org.brixcms.plugin.snapshot.SnapshotPlugin;
import org.brixcms.plugin.webdavurl.WebdavUrlPlugin;

/**
 * Subclass of {@link Brix} that configures demo-specific settings such as plugins, tiles, etc.
 *
 * @author igor.vaynberg
 */
public class DemoBrix extends Brix {
    /**
     * Constructor
     *
     * @param config
     */
    public DemoBrix(BrixConfig config) {
        super(config);

        // register plugins
        config.getRegistry().register(Plugin.POINT, new MenuPlugin(this));
        config.getRegistry().register(Plugin.POINT, new SnapshotPlugin(this));
        config.getRegistry().register(Plugin.POINT, new PrototypePlugin(this));
        config.getRegistry().register(Plugin.POINT, new WebdavUrlPlugin());
        config.getRegistry().register(Plugin.POINT, new ContentPlugin(this));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuthorizationStrategy newAuthorizationStrategy() {
        // register our simple demo auth strategy
        return new DemoAuthorizationStrategy();
    }
}
