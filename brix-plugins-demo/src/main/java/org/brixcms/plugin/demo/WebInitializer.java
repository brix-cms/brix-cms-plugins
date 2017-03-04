
package org.brixcms.plugin.demo;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.wicket.protocol.http.WicketFilter;
import org.apache.wicket.spring.SpringWebApplicationFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * This class is the replacement of the web.xml. It registers the wicket filter in the
 * spring aware configuration style.
 */
@Configuration
public class WebInitializer implements ServletContextInitializer {

    private static final String PARAM_APP_BEAN = "applicationBean";

    @Override
    public void onStartup(ServletContext sc) throws ServletException {
        FilterRegistration filter = sc.addFilter("wicket-filter", WicketFilter.class);
        filter.setInitParameter(PARAM_APP_BEAN, StringUtils.uncapitalize(BrixApplication.class.getSimpleName()));
        filter.setInitParameter(WicketFilter.APP_FACT_PARAM, SpringWebApplicationFactory.class.getName());
        filter.setInitParameter(WicketFilter.FILTER_MAPPING_PARAM, "/*");
        filter.addMappingForUrlPatterns(null, false, "/*");
    }

}