
package org.brixcms.plugin.demo.web;

import org.brixcms.plugin.demo.BrixApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * This class is needed for deployment on an application server. It is the counterpart of
 * the main method in WicketWebApplication.
 */
public class WarInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(BrixApplication.class);
    }

}