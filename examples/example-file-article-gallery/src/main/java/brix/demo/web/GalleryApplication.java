package brix.demo.web;

import org.apache.wicket.Application;
import org.apache.wicket.markup.html.JavascriptPackageResource;

import brix.plugin.file.web.FileResource;

import com.jquery.JQueryResourceReference;

public class GalleryApplication extends WicketApplication {

	public GalleryApplication() {
		super();
		addRenderHeadListener(JavascriptPackageResource.getHeaderContribution(new JQueryResourceReference()));
	}

	@Override
	protected void init() {
		super.init();
		getSharedResources().putClassAlias(Application.class, "a");
		getSharedResources().add("file", new FileResource());
	}

}
