package org.brixcms.plugin.demo;

import java.io.File;

import javax.jcr.Credentials;
import javax.jcr.SimpleCredentials;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Application-wide configuration settings for Brix Demo Application
 *
 * @author igor.vaynberg 
 * @author dan.simko@gmail.com
 */
@Component
@PropertySource("classpath:application.properties")
public class ApplicationProperties {

	@Value("${jcr.login}")
	private String jcrLogin;
	@Value("${httpPort}")
	private Integer httpPort;
	@Value("${httpsPort}")
	private Integer httpsPort;
	@Value("${jcr.defaultWorkspace}")
	private String jcrDefaultWorkspace;
	@Value("${jcr.password}")
	private String jcrPassword;
	@Value("${jcr.repositoryUrl}")
	private String jcrRepositoryUrl;
	@Value("${jcr.defaultWorkspaceState}")
	private String jcrDefaultWorkspaceState;
	@Value("${workspaceManagerUrl}")
	private String workspaceManagerUrl;

	/**
	 * @return jcr {@link Credentials} built from username and password
	 */
	public Credentials buildSimpleCredentials() {
		return new SimpleCredentials(getJcrLogin(), getJcrPassword().toCharArray());
	}

	/**
	 * @return jcr login name
	 */
	public String getJcrLogin() {
		return jcrLogin;
	}

	/**
	 * @return http port the server is using
	 */
	public int getHttpPort() {
		return httpPort;
	}

	/**
	 * @return https port the server is using
	 */
	public int getHttpsPort() {
		return httpsPort;
	}

	/**
	 * @return jcr default workspace
	 */
	public String getJcrDefaultWorkspace() {
		return jcrDefaultWorkspace;
	}

	/**
	 * @return jcr login password
	 */
	public String getJcrPassword() {
		return jcrPassword;
	}

	/**
	 * @return jcr repository url
	 */
	public String getJcrRepositoryUrl() {
		if (jcrRepositoryUrl == null || jcrRepositoryUrl.trim().length() == 0) {
			// if no url was specified generate a unique temporary one
			jcrRepositoryUrl = "file://" + getDefaultRepositoryFileName();
		}
		return jcrRepositoryUrl;
	}

	/**
	 * Generates a temporary file name inside tmp directory
	 *
	 * @return
	 */
	public String getDefaultRepositoryFileName() {
		String fileName = System.getProperty("java.io.tmpdir");
		if (!fileName.endsWith(File.separator)) {
			fileName += File.separator;
		}
		fileName += ".repository";
		return fileName;
	}

	/**
	 * @return default workspace state
	 */
	public String getWorkspaceDefaultState() {
		return jcrDefaultWorkspaceState;
	}

	/**
	 * @return workspace manager url
	 */
	public String getWorkspaceManagerUrl() {
		return workspaceManagerUrl;
	}
}
