package brix.demo.web.admin;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;

import brix.web.admin.AdminPanel;

public class AdminPanelWithWebLink extends Panel {

	private static final long serialVersionUID = 1L;

	public AdminPanelWithWebLink(String id, String workspace) {
		super(id);
		add(new AdminPanel("adminPanel", workspace));
		add(new WebMarkupContainer("frontendLink").add(new SimpleAttributeModifier("href", getFrontendPath())));
	}

	private String getFrontendPath() {
		String contextPath = ((ServletWebRequest) getRequest()).getHttpServletRequest().getContextPath();
		if ("".equals(contextPath)) {
			return "/";
		}
		return contextPath;
	}
}
