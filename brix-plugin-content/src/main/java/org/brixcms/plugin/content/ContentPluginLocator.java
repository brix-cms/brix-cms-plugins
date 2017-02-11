package org.brixcms.plugin.content;

import org.brixcms.plugin.hierarchical.HierarchicalNodePlugin;
import org.brixcms.plugin.hierarchical.HierarchicalPluginLocator;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class ContentPluginLocator implements HierarchicalPluginLocator {

    @Override
    public HierarchicalNodePlugin getPlugin() {
        return ContentPlugin.get();
    }

}
