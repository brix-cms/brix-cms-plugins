package org.brixcms.plugin.content.breadcrumb;

import java.io.Serializable;
import java.util.List;

/**
 * Interface implemented by components that want to contribute to a breadcrumb. 
 *
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public interface BreadcrumbContributor extends Serializable {

    void contributeToBreadcrumb(List<BreadcrumbItem> items);

    public static class BreadcrumbItem implements Serializable {
        
        private final String title;
        private final String url;

        public BreadcrumbItem(String title, String url) {
            this.title = title;
            this.url = url;
        }

        public String getTitle() {
            return title;
        }

        public String getUrl() {
            return url;
        }

    }
}
