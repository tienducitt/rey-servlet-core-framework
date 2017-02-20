/*
 * Copied from org.apache.commons.configuration.AbstractFileConfiguration in apache commons-configuration-1.9 package
 */
package com.reydentx.core.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.FileSystem;
import org.apache.commons.configuration.FileSystemBased;
import org.apache.commons.configuration.Lock;

import org.apache.commons.configuration.reloading.InvariantReloadingStrategy;
import org.apache.commons.configuration.reloading.ReloadingStrategy;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;

/**
 * <p>Partial implementation of the {@code FileConfiguration} interface.
 * Developers of file based configuration may want to extend this class, the two
 * methods left to implement are {@link FileConfiguration#load(Reader)} and
 * {@link FileConfiguration#save(Writer)}.</p> <p>This base class already
 * implements a couple of ways to specify the location of the file this
 * configuration is based on. The following possibilities exist: </p><ul><li>URLs:
 * With the method {@code setURL()} a full URL to the configuration source can
 * be specified. This is the most flexible way. Note that the {@code save()}
 * methods support only <em>file:</em> URLs.</li> <li>Files: The
 * {@code setFile()} method allows to specify the configuration source as a
 * file. This can be either a relative or an absolute file. In the former case
 * the file is resolved based on the current directory.</li> <li>As file paths
 * in string form: With the {@code setPath()} method a full path to a
 * configuration file can be provided as a string.</li> <li>Separated as base
 * path and file name: This is the native form in which the location is stored.
 * The base path is a string defining either a local directory or a URL. It can
 * be set using the {@code setBasePath()} method. The file name, non
 * surprisingly, defines the name of the configuration file.</li></ul>
 * <p>The configuration source to be loaded can be specified using one of the
 * methods described above. Then the parameterless {@code load()} method can be
 * called. Alternatively, one of the {@code load()} methods can be used which is
 * passed the source directly. These methods typically do not change the
 * internally stored file; however, if the configuration is not yet associated
 * with a configuration source, the first call to one of the {@code load()}
 * methods sets the base path and the source URL. This fact has to be taken into
 * account when calling {@code load()} multiple times with different file
 * paths.</p> <p>Note that the {@code load()} methods do not wipe out the
 * configuration's content before the new configuration file is loaded. Thus it
 * is very easy to construct a union configuration by simply loading multiple
 * configuration files, e.g.</p> null <br><pre>
 * config.load(configFile1);
 * config.load(configFile2);
 * </pre> <p>After executing this code fragment, the resulting configuration
 * will contain both the properties of configFile1 and configFile2. On the other
 * hand, if the current configuration file is to be reloaded, {@code clear()}
 * should be called first. Otherwise the properties are doubled. This behavior
 * is analogous to the behavior of the {@code load(InputStream)} method in
 * {@code java.util.Properties}.</p>
 *
 * @author Emmanuel Bourg
 * @version $Id: ZAbstractFileConfiguration.java 1234118 2012-01-20 20:36:04Z
 * oheger $
 * @since 1.0-rc2
 */
public abstract class RAbstractFileConfiguration
		extends BaseConfiguration
		implements FileConfiguration, FileSystemBased {

	/**
	 * Constant for the configuration reload event.
	 */
	public static final int EVENT_RELOAD = 20;
	/**
	 * Constant fro the configuration changed event.
	 */
	public static final int EVENT_CONFIG_CHANGED = 21;
	/**
	 * The root of the file scheme
	 */
	private static final String FILE_SCHEME = "file:";
	/**
	 * Stores the file name.
	 */
	protected String fileName;
	/**
	 * Stores the base path.
	 */
	protected String basePath;
	/**
	 * The auto save flag.
	 */
	protected boolean autoSave;
	/**
	 * Holds a reference to the reloading strategy.
	 */
	protected ReloadingStrategy strategy;
	/**
	 * A lock object for protecting reload operations.
	 */
	protected Object reloadLock = new Lock("ZAbstractFileConfiguration");
	/**
	 * Stores the encoding of the configuration file.
	 */
	private String encoding;
	/**
	 * Stores the URL from which the configuration file was loaded.
	 */
	private URL sourceURL;
	/**
	 * A counter that prohibits reloading.
	 */
	private int noReload;
	/**
	 * The FileSystem being used for this Configuration
	 */
	//mod by Zing
	//private FileSystem fileSystem = FileSystem.getDefaultFileSystem(); //old
	private FileSystem fileSystem = new RDefaultFileSystem(); //change to

	/**
	 * Default constructor
	 *
	 * @since 1.1
	 */
	public RAbstractFileConfiguration() {
		initReloadingStrategy();
		//mod by Zing
		//setLogger(LogFactory.getLog(getClass())); //orig
		addErrorLogListener();
	}

	/**
	 * Creates and loads the configuration from the specified file. The passed
	 * in string must be a valid file name, either absolute or relativ.
	 *
	 * @param fileName The name of the file to load.
	 *
	 * @throws ConfigurationException Error while loading the file
	 * @since 1.1
	 */
	public RAbstractFileConfiguration(String fileName) throws ConfigurationException {
		this();

		// store the file name
		setFileName(fileName);

		// load the file
		load();
	}

	/**
	 * Creates and loads the configuration from the specified file.
	 *
	 * @param file The file to load.
	 * @throws ConfigurationException Error while loading the file
	 * @since 1.1
	 */
	public RAbstractFileConfiguration(File file) throws ConfigurationException {
		this();

		// set the file and update the url, the base path and the file name
		setFile(file);

		// load the file
		if (file.exists()) {
			load();
		}
	}

	/**
	 * Creates and loads the configuration from the specified URL.
	 *
	 * @param url The location of the file to load.
	 * @throws ConfigurationException Error while loading the file
	 * @since 1.1
	 */
	public RAbstractFileConfiguration(URL url) throws ConfigurationException {
		this();

		// set the URL and update the base path and the file name
		setURL(url);

		// load the file
		load();
	}

	public void setFileSystem(FileSystem fileSystem) {
		if (fileSystem == null) {
			throw new NullPointerException("A valid FileSystem must be specified");
		}
		this.fileSystem = fileSystem;
	}

	public void resetFileSystem() {
		this.fileSystem = FileSystem.getDefaultFileSystem();
	}

	public FileSystem getFileSystem() {
		return this.fileSystem;
	}

	public Object getReloadLock() {
		return reloadLock;
	}

	/**
	 * Load the configuration from the underlying location.
	 *
	 * @throws ConfigurationException if loading of the configuration fails
	 */
	public void load() throws ConfigurationException {
		if (sourceURL != null) {
			load(sourceURL);
		} else {
			load(getFileName());
		}
	}

	/**
	 * Locate the specified file and load the configuration. If the
	 * configuration is already associated with a source, the current source is
	 * not changed. Otherwise (i.e. this is the first load operation), the
	 * source URL and the base path are set now based on the source to be
	 * loaded.
	 *
	 * @param fileName the name of the file to be loaded
	 * @throws ConfigurationException if an error occurs
	 */
	public void load(String fileName) throws ConfigurationException {
		try {
			URL url = RConfigurationUtils.locate(this.fileSystem, basePath, fileName);

			if (url == null) {
				throw new ConfigurationException("Cannot locate configuration source " + fileName);
			}
			load(url);
		} catch (ConfigurationException e) {
			throw e;
		} catch (Exception e) {
			throw new ConfigurationException("Unable to load the configuration file " + fileName, e);
		}
	}

	/**
	 * Load the configuration from the specified file. If the configuration is
	 * already associated with a source, the current source is not changed.
	 * Otherwise (i.e. this is the first load operation), the source URL and the
	 * base path are set now based on the source to be loaded.
	 *
	 * @param file the file to load
	 * @throws ConfigurationException if an error occurs
	 */
	public void load(File file) throws ConfigurationException {
		try {
			load(RConfigurationUtils.toURL(file));
		} catch (ConfigurationException e) {
			throw e;
		} catch (Exception e) {
			throw new ConfigurationException("Unable to load the configuration file " + file, e);
		}
	}

	/**
	 * Load the configuration from the specified URL. If the configuration is
	 * already associated with a source, the current source is not changed.
	 * Otherwise (i.e. this is the first load operation), the source URL and the
	 * base path are set now based on the source to be loaded.
	 *
	 * @param url the URL of the file to be loaded
	 * @throws ConfigurationException if an error occurs
	 */
	public void load(URL url) throws ConfigurationException {
		if (sourceURL == null) {
			if (StringUtils.isEmpty(getBasePath())) {
				// ensure that we have a valid base path
				setBasePath(url.toString());
			}
			sourceURL = url;
		}

		InputStream in = null;

		try {
			in = fileSystem.getInputStream(url);
			load(in);
		} catch (ConfigurationException e) {
			throw e;
		} catch (Exception e) {
			throw new ConfigurationException("Unable to load the configuration from the URL " + url, e);
		} finally {
			// close the input stream
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				getLogger().warn("Could not close input stream", e);
			}
		}
	}

	/**
	 * Load the configuration from the specified stream, using the encoding
	 * returned by {@link #getEncoding()}.
	 *
	 * @param in the input stream
	 *
	 * @throws ConfigurationException if an error occurs during the load
	 * operation
	 */
	public void load(InputStream in) throws ConfigurationException {
		load(in, getEncoding());
	}

	/**
	 * Load the configuration from the specified stream, using the specified
	 * encoding. If the encoding is null the default encoding is used.
	 *
	 * @param in the input stream
	 * @param encoding the encoding used. {@code null} to use the default
	 * encoding
	 *
	 * @throws ConfigurationException if an error occurs during the load
	 * operation
	 */
	public void load(InputStream in, String encoding) throws ConfigurationException {
		Reader reader = null;

		if (encoding != null) {
			try {
				reader = new InputStreamReader(in, encoding);
			} catch (UnsupportedEncodingException e) {
				throw new ConfigurationException(
						"The requested encoding is not supported, try the default encoding.", e);
			}
		}

		if (reader == null) {
			reader = new InputStreamReader(in);
		}

		load(reader);
	}

	/**
	 * Save the configuration. Before this method can be called a valid file
	 * name must have been set.
	 *
	 * @throws ConfigurationException if an error occurs or no file name has
	 * been set yet
	 */
	public void save() throws ConfigurationException {
		if (getFileName() == null) {
			throw new ConfigurationException("No file name has been set!");
		}

		if (sourceURL != null) {
			save(sourceURL);
		} else {
			save(fileName);
		}
		strategy.init();
	}

	/**
	 * Save the configuration to the specified file. This doesn't change the
	 * source of the configuration, use setFileName() if you need it.
	 *
	 * @param fileName the file name
	 *
	 * @throws ConfigurationException if an error occurs during the save
	 * operation
	 */
	public void save(String fileName) throws ConfigurationException {
		try {
			URL url = this.fileSystem.getURL(basePath, fileName);

			if (url == null) {
				throw new ConfigurationException("Cannot locate configuration source " + fileName);
			}
			save(url);
			/*File file = ZConfigurationUtils.getFile(basePath, fileName);
			 if (file == null)
			 {
			 throw new ConfigurationException("Invalid file name for save: " + fileName);
			 }
			 save(file); */
		} catch (ConfigurationException e) {
			throw e;
		} catch (Exception e) {
			throw new ConfigurationException("Unable to save the configuration to the file " + fileName, e);
		}
	}

	/**
	 * Save the configuration to the specified URL. This doesn't change the
	 * source of the configuration, use setURL() if you need it.
	 *
	 * @param url the URL
	 *
	 * @throws ConfigurationException if an error occurs during the save
	 * operation
	 */
	public void save(URL url) throws ConfigurationException {
		OutputStream out = null;
		try {
			out = fileSystem.getOutputStream(url);
			save(out);
		} catch (Exception e) {
			throw new ConfigurationException("Could not save to URL " + url, e);
		} finally {
			closeSilent(out);
		}
	}

	/**
	 * Save the configuration to the specified file. The file is created
	 * automatically if it doesn't exist. This doesn't change the source of the
	 * configuration, use {@link #setFile} if you need it.
	 *
	 * @param file the target file
	 *
	 * @throws ConfigurationException if an error occurs during the save
	 * operation
	 */
	public void save(File file) throws ConfigurationException {
		OutputStream out = null;

		try {
			out = fileSystem.getOutputStream(file);
			save(out);
		} finally {
			closeSilent(out);
		}
	}

	/**
	 * Save the configuration to the specified stream, using the encoding
	 * returned by {@link #getEncoding()}.
	 *
	 * @param out the output stream
	 *
	 * @throws ConfigurationException if an error occurs during the save
	 * operation
	 */
	public void save(OutputStream out) throws ConfigurationException {
		save(out, getEncoding());
	}

	/**
	 * Save the configuration to the specified stream, using the specified
	 * encoding. If the encoding is null the default encoding is used.
	 *
	 * @param out the output stream
	 * @param encoding the encoding to use
	 * @throws ConfigurationException if an error occurs during the save
	 * operation
	 */
	public void save(OutputStream out, String encoding) throws ConfigurationException {
		Writer writer = null;

		if (encoding != null) {
			try {
				writer = new OutputStreamWriter(out, encoding);
			} catch (UnsupportedEncodingException e) {
				throw new ConfigurationException(
						"The requested encoding is not supported, try the default encoding.", e);
			}
		}

		if (writer == null) {
			writer = new OutputStreamWriter(out);
		}

		save(writer);
	}

	/**
	 * Return the name of the file.
	 *
	 * @return the file name
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Set the name of the file. The passed in file name can contain a relative
	 * path. It must be used when referring files with relative paths from
	 * classpath. Use {@link RAbstractFileConfiguration#setPath(String)
	 * setPath()} to set a full qualified file name.
	 *
	 * @param fileName the name of the file
	 */
	public void setFileName(String fileName) {
		if (fileName != null && fileName.startsWith(FILE_SCHEME) && !fileName.startsWith("file://")) {
			fileName = "file://" + fileName.substring(FILE_SCHEME.length());
		}

		sourceURL = null;
		this.fileName = fileName;
		getLogger().debug("FileName set to " + fileName);
	}

	/**
	 * Return the base path.
	 *
	 * @return the base path
	 * @see FileConfiguration#getBasePath()
	 */
	public String getBasePath() {
		return basePath;
	}

	/**
	 * Sets the base path. The base path is typically either a path to a
	 * directory or a URL. Together with the value passed to the
	 * {@code setFileName()} method it defines the location of the configuration
	 * file to be loaded. The strategies for locating the file are quite
	 * tolerant. For instance if the file name is already an absolute path or a
	 * fully defined URL, the base path will be ignored. The base path can also
	 * be a URL, in which case the file name is interpreted in this URL's
	 * context. Because the base path is used by some of the derived classes for
	 * resolving relative file names it should contain a meaningful value. If
	 * other methods are used for determining the location of the configuration
	 * file (e.g. {@code setFile()} or {@code setURL()}), the base path is
	 * automatically set.
	 *
	 * @param basePath the base path.
	 */
	public void setBasePath(String basePath) {
		if (basePath != null && basePath.startsWith(FILE_SCHEME) && !basePath.startsWith("file://")) {
			basePath = "file://" + basePath.substring(FILE_SCHEME.length());
		}
		sourceURL = null;
		this.basePath = basePath;
		getLogger().debug("Base path set to " + basePath);
	}

	/**
	 * Return the file where the configuration is stored. If the base path is a
	 * URL with a protocol different than &quot;file&quot;, or the configuration
	 * file is within a compressed archive, the return value will not point to a
	 * valid file object.
	 *
	 * @return the file where the configuration is stored; this can be
	 * <b>null</b>
	 */
	public File getFile() {
		if (getFileName() == null && sourceURL == null) {
			return null;
		} else if (sourceURL != null) {
			return RConfigurationUtils.fileFromURL(sourceURL);
		} else {
			return RConfigurationUtils.getFile(getBasePath(), getFileName());
		}
	}

	/**
	 * Set the file where the configuration is stored. The passed in file is
	 * made absolute if it is not yet. Then the file's path component becomes
	 * the base path and its name component becomes the file name.
	 *
	 * @param file the file where the configuration is stored
	 */
	public void setFile(File file) {
		sourceURL = null;
		setFileName(file.getName());
		setBasePath((file.getParentFile() != null) ? file.getParentFile()
				.getAbsolutePath() : null);
	}

	/**
	 * Returns the full path to the file this configuration is based on. The
	 * return value is a valid File path only if this configuration is based on
	 * a file on the local disk. If the configuration was loaded from a packed
	 * archive the returned value is the string form of the URL from which the
	 * configuration was loaded.
	 *
	 * @return the full path to the configuration file
	 */
	public String getPath() {
		return fileSystem.getPath(getFile(), sourceURL, getBasePath(), getFileName());
	}

	/**
	 * Sets the location of this configuration as a full or relative path name.
	 * The passed in path should represent a valid file name on the file system.
	 * It must not be used to specify relative paths for files that exist in
	 * classpath, either plain file system or compressed archive, because this
	 * method expands any relative path to an absolute one which may end in an
	 * invalid absolute path for classpath references.
	 *
	 * @param path the full path name of the configuration file
	 */
	public void setPath(String path) {
		setFile(new File(path));
	}

	URL getSourceURL() {
		return sourceURL;
	}

	/**
	 * Return the URL where the configuration is stored.
	 *
	 * @return the configuration's location as URL
	 */
	public URL getURL() {
		return (sourceURL != null) ? sourceURL
				: RConfigurationUtils.locate(this.fileSystem, getBasePath(), getFileName());
	}

	/**
	 * Set the location of this configuration as a URL. For loading this can be
	 * an arbitrary URL with a supported protocol. If the configuration is to be
	 * saved, too, a URL with the &quot;file&quot; protocol should be provided.
	 *
	 * @param url the location of this configuration as URL
	 */
	public void setURL(URL url) {
		setBasePath(RConfigurationUtils.getBasePath(url));
		setFileName(RConfigurationUtils.getFileName(url));
		sourceURL = url;
		getLogger().debug("URL set to " + url);
	}

	public void setAutoSave(boolean autoSave) {
		this.autoSave = autoSave;
	}

	public boolean isAutoSave() {
		return autoSave;
	}

	/**
	 * Save the configuration if the automatic persistence is enabled and if a
	 * file is specified.
	 */
	protected void possiblySave() {
		if (autoSave && fileName != null) {
			try {
				save();
			} catch (ConfigurationException e) {
				throw new ConfigurationRuntimeException("Failed to auto-save", e);
			}
		}
	}

	/**
	 * Adds a new property to this configuration. This implementation checks if
	 * the auto save mode is enabled and saves the configuration if necessary.
	 *
	 * @param key the key of the new property
	 * @param value the value
	 */
	@Override
	public void addProperty(String key, Object value) {
		synchronized (reloadLock) {
			super.addProperty(key, value);
			possiblySave();
		}
	}

	/**
	 * Sets a new value for the specified property. This implementation checks
	 * if the auto save mode is enabled and saves the configuration if
	 * necessary.
	 *
	 * @param key the key of the affected property
	 * @param value the value
	 */
	@Override
	public void setProperty(String key, Object value) {
		synchronized (reloadLock) {
			super.setProperty(key, value);
			possiblySave();
		}
	}

	@Override
	public void clearProperty(String key) {
		synchronized (reloadLock) {
			super.clearProperty(key);
			possiblySave();
		}
	}

	public ReloadingStrategy getReloadingStrategy() {
		return strategy;
	}

	public void setReloadingStrategy(ReloadingStrategy strategy) {
		this.strategy = strategy;
		strategy.setConfiguration(this);
		strategy.init();
	}

	/**
	 * Performs a reload operation if necessary. This method is called on each
	 * access of this configuration. It asks the associated reloading strategy
	 * whether a reload should be performed. If this is the case, the
	 * configuration is cleared and loaded again from its source. If this
	 * operation causes an exception, the registered error listeners will be
	 * notified. The error event passed to the listeners is of type
	 * {@code EVENT_RELOAD} and contains the exception that caused the event.
	 */
	public void reload() {
		reload(false);
	}

	public boolean reload(boolean checkReload) {
		synchronized (reloadLock) {
			if (noReload == 0) {
				try {
					enterNoReload(); // avoid reentrant calls

					if (strategy.reloadingRequired()) {
						if (getLogger().isInfoEnabled()) {
							getLogger().info("Reloading configuration. URL is " + getURL());
						}
						refresh();

						// notify the strategy
						strategy.reloadingPerformed();
					}
				} catch (Exception e) {
					fireError(EVENT_RELOAD, null, null, e);
					// todo rollback the changes if the file can't be reloaded
					if (checkReload) {
						return false;
					}
				} finally {
					exitNoReload();
				}
			}
		}
		return true;
	}

	/**
	 * Reloads the associated configuration file. This method first clears the
	 * content of this configuration, then the associated configuration file is
	 * loaded again. Updates on this configuration which have not yet been saved
	 * are lost. Calling this method is like invoking {@code reload()} without
	 * checking the reloading strategy.
	 *
	 * @throws ConfigurationException if an error occurs
	 * @since 1.7
	 */
	public void refresh() throws ConfigurationException {
		fireEvent(EVENT_RELOAD, null, getURL(), true);
		setDetailEvents(false);
		boolean autoSaveBak = this.isAutoSave(); // save the current state
		this.setAutoSave(false); // deactivate autoSave to prevent information loss
		try {
			clear();
			load();
		} finally {
			this.setAutoSave(autoSaveBak); // set autoSave to previous value
			setDetailEvents(true);
		}
		fireEvent(EVENT_RELOAD, null, getURL(), false);
	}

	/**
	 * Send notification that the configuration has changed.
	 */
	public void configurationChanged() {
		fireEvent(EVENT_CONFIG_CHANGED, null, getURL(), true);
	}

	/**
	 * Enters the &quot;No reloading mode&quot;. As long as this mode is active
	 * no reloading will be performed. This is necessary for some
	 * implementations of {@code save()} in derived classes, which may cause a
	 * reload while accessing the properties to save. This may cause the whole
	 * configuration to be erased. To avoid this, this method can be called
	 * first. After a call to this method there always must be a corresponding
	 * call of {@link #exitNoReload()} later! (If necessary, {@code finally}
	 * blocks must be used to ensure this.
	 */
	protected void enterNoReload() {
		synchronized (reloadLock) {
			noReload++;
		}
	}

	/**
	 * Leaves the &quot;No reloading mode&quot;.
	 *
	 * @see #enterNoReload()
	 */
	protected void exitNoReload() {
		synchronized (reloadLock) {
			if (noReload > 0) // paranoia check
			{
				noReload--;
			}
		}
	}

	/**
	 * Sends an event to all registered listeners. This implementation ensures
	 * that no reloads are performed while the listeners are invoked. So
	 * infinite loops can be avoided that can be caused by event listeners
	 * accessing the configuration's properties when they are invoked.
	 *
	 * @param type the event type
	 * @param propName the name of the property
	 * @param propValue the value of the property
	 * @param before the before update flag
	 */
	@Override
	protected void fireEvent(int type, String propName, Object propValue, boolean before) {
		enterNoReload();
		try {
			super.fireEvent(type, propName, propValue, before);
		} finally {
			exitNoReload();
		}
	}

	@Override
	public Object getProperty(String key) {
		synchronized (reloadLock) {
			reload();
			return super.getProperty(key);
		}
	}

	@Override
	public boolean isEmpty() {
		reload();
		synchronized (reloadLock) {
			return super.isEmpty();
		}
	}

	@Override
	public boolean containsKey(String key) {
		reload();
		synchronized (reloadLock) {
			return super.containsKey(key);
		}
	}

	/**
	 * Returns an {@code Iterator} with the keys contained in this
	 * configuration. This implementation performs a reload if necessary before
	 * obtaining the keys. The {@code Iterator} returned by this method points
	 * to a snapshot taken when this method was called. Later changes at the set
	 * of keys (including those caused by a reload) won't be visible. This is
	 * because a reload can happen at any time during iteration, and it is
	 * impossible to determine how this reload affects the current iteration.
	 * When using the iterator a client has to be aware that changes of the
	 * configuration are possible at any time. For instance, if after a reload
	 * operation some keys are no longer present, the iterator will still return
	 * those keys because they were found when it was created.
	 *
	 * @return an {@code Iterator} with the keys of this configuration
	 */
	@Override
	public Iterator<String> getKeys() {
		reload();
		List<String> keyList = new LinkedList<String>();
		enterNoReload();
		try {
			for (Iterator<String> it = super.getKeys(); it.hasNext();) {
				keyList.add(it.next());
			}

			return keyList.iterator();
		} finally {
			exitNoReload();
		}
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * Creates a copy of this configuration. The new configuration object will
	 * contain the same properties as the original, but it will lose any
	 * connection to a source file (if one exists); this includes setting the
	 * source URL, base path, and file name to <b>null</b>. This is done to
	 * avoid race conditions if both the original and the copy are modified and
	 * then saved.
	 *
	 * @return the copy
	 * @since 1.3
	 */
	@Override
	public Object clone() {
		RAbstractFileConfiguration copy = (RAbstractFileConfiguration) super.clone();
		copy.setBasePath(null);
		copy.setFileName(null);
		copy.initReloadingStrategy();
		return copy;
	}

	/**
	 * Helper method for initializing the reloading strategy.
	 */
	private void initReloadingStrategy() {
		setReloadingStrategy(new InvariantReloadingStrategy());
	}

	/**
	 * A helper method for closing an output stream. Occurring exceptions will
	 * be ignored.
	 *
	 * @param out the output stream to be closed (may be <b>null</b>)
	 * @since 1.5
	 */
	protected void closeSilent(OutputStream out) {
		try {
			if (out != null) {
				out.close();
			}
		} catch (IOException e) {
			getLogger().warn("Could not close output stream", e);
		}
	}
}
