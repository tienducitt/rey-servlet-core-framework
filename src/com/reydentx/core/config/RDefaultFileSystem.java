/*
 * Copied from org.apache.commons.configuration.DefaultFileSystem in apache commons-configuration-1.9 package
 */
package com.reydentx.core.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileSystem;
import org.apache.log4j.Logger;


/**
 * FileSystem that uses java.io.File or HttpClient
 *
 * @since 1.7
 * @author <a
 * href="http://commons.apache.org/configuration/team-list.html">Commons
 * Configuration team</a>
 */
public class RDefaultFileSystem extends FileSystem {

	/**
	 * The Log for diagnostic messages.
	 */
	//mod by Zing
	//private Log log = LogFactory.getLog(ZDefaultFileSystem.class); //old
	private Logger log = Logger.getLogger(RDefaultFileSystem.class);

	@Override
	public InputStream getInputStream(String basePath, String fileName)
			throws ConfigurationException {
		try {
			URL url = RConfigurationUtils.locate(this, basePath, fileName);

			if (url == null) {
				throw new ConfigurationException("Cannot locate configuration source " + fileName);
			}
			return getInputStream(url);
		} catch (ConfigurationException e) {
			throw e;
		} catch (Exception e) {
			throw new ConfigurationException("Unable to load the configuration file " + fileName, e);
		}
	}

	@Override
	public InputStream getInputStream(URL url) throws ConfigurationException {
		// throw an exception if the target URL is a directory
		File file = RConfigurationUtils.fileFromURL(url);
		if (file != null && file.isDirectory()) {
			throw new ConfigurationException("Cannot load a configuration from a directory");
		}

		try {
			return url.openStream();
		} catch (Exception e) {
			throw new ConfigurationException("Unable to load the configuration from the URL " + url, e);
		}
	}

	@Override
	public OutputStream getOutputStream(URL url) throws ConfigurationException {
		return null;
	}

	@Override
	public OutputStream getOutputStream(File file) throws ConfigurationException {
		try {
			// create the file if necessary
			createPath(file);
			return new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			throw new ConfigurationException("Unable to save to file " + file, e);
		}
	}

	@Override
	public String getPath(File file, URL url, String basePath, String fileName) {
		String path = null;
		// if resource was loaded from jar file may be null
		if (file != null) {
			path = file.getAbsolutePath();
		}

		// try to see if file was loaded from a jar
		if (path == null) {
			if (url != null) {
				path = url.getPath();
			} else {
				try {
					path = getURL(basePath, fileName).getPath();
				} catch (Exception e) {
					// simply ignore it and return null
				}
			}
		}

		return path;
	}

	@Override
	public String getBasePath(String path) {
		URL url;
		try {
			url = getURL(null, path);
			return RConfigurationUtils.getBasePath(url);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String getFileName(String path) {
		URL url;
		try {
			url = getURL(null, path);
			return RConfigurationUtils.getFileName(url);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public URL getURL(String basePath, String file) throws MalformedURLException {
		File f = new File(file);
		if (f.isAbsolute()) // already absolute?
		{
			return RConfigurationUtils.toURL(f);
		}

		try {
			if (basePath == null) {
				return new URL(file);
			} else {
				URL base = new URL(basePath);
				return new URL(base, file);
			}
		} catch (MalformedURLException uex) {
			return RConfigurationUtils.toURL(RConfigurationUtils.constructFile(basePath, file));
		}
	}

	@Override
	public URL locateFromURL(String basePath, String fileName) {
		try {
			URL url;
			if (basePath == null) {
				return new URL(fileName);
				//url = new URL(name);
			} else {
				URL baseURL = new URL(basePath);
				url = new URL(baseURL, fileName);

				// check if the file exists
				InputStream in = null;
				try {
					in = url.openStream();
				} finally {
					if (in != null) {
						in.close();
					}
				}
				return url;
			}
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Create the path to the specified file.
	 *
	 * @param file the target file
	 */
	private void createPath(File file) {
		if (file != null) {
			// create the path to the file if the file doesn't exist
			if (!file.exists()) {
				File parent = file.getParentFile();
				if (parent != null && !parent.exists()) {
					parent.mkdirs();
				}
			}
		}
	}

	/**
	 * Wraps the output stream so errors can be detected in the HTTP response.
	 *
	 * @since 1.7
	 * @author <a
	 * href="http://commons.apache.org/configuration/team-list.html">Commons
	 * Configuration team</a>
	 */
}
