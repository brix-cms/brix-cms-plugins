package org.brixcms.plugin.demo.web.signin;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

@SuppressWarnings("serial")
public class BrixSignInPage extends WebPage {

	public BrixSignInPage() {
		this(null);
	}

	public BrixSignInPage(final PageParameters parameters) {
		add(new BrixSignInPanel("signInPanel"));
	}
}
