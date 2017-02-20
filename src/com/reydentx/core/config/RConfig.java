/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 */
package com.reydentx.core.config;

import com.reydentx.core.common.CompositeKey;
import com.reydentx.core.common.RUtil;
import com.reydentx.core.common.SystemParam;
import com.reydentx.exception.CircularReferenceException;
import com.reydentx.exception.InvalidParamException;
import com.reydentx.exception.NotExistException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.cliffc.high_scale_lib.NonBlockingHashMap;


public class RConfig {

	private final ConcurrentMap<String, String> _configMap = new NonBlockingHashMap<String, String>();
	private String _mainConfigPath;
	private static final String _ClazzKeySep = "@";
	private static final String _InstNameKeySep = ".";
	private static final ConcurrentMap<CompositeKey.Three, String> _fqKeysMap = new NonBlockingHashMap<>();

	public static final RConfig Instance = new RConfig();

	
	private RConfig() {
		_init();
	}

	private void _init() {
		//~~~~~~~~~~ init configs from files ~~~~~~~~~~
		
		String appProf = SystemParam.getRunningMode();
		String configDir = "conf";
		String prePath;
		if (configDir.endsWith("/")) {
			prePath = configDir + appProf + ".";
		} else {
			prePath = configDir + "/" + appProf + ".";
		}
		String configFiles = "config.ini";
		if (configFiles != null && !configFiles.isEmpty()) {
			StringTokenizer strTok = new StringTokenizer(configFiles, ",");
			while (strTok.hasMoreTokens()) {
				String fileName = strTok.nextToken();
				String path = prePath + fileName;
				try {
					ConfigToMap(_configMap, path, LoadFromFile(path), false);
				} catch (Exception ex) {
					System.err.println(ex);
				}
				if (_mainConfigPath == null) {
					_mainConfigPath = path;
				}
			}
		} else {
			System.out.println("No configuration file is specified");
		}
		//load default configuration
		try {
			Enumeration<URL> urls = ClassLoader.getSystemResources("conf/" + appProf + ".config.ini");
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				ConfigToMap(_configMap, url.toString(), LoadFromURL(url), false);
			}
		} catch (Exception ex) {
			System.err.println(ex);
		}
	}

	////////////////////////////////////////////////////////////////////////////
	///static util functions
	///

	private static void ConfigToMap(ConcurrentMap<String, String> map, String configFile, Configuration config, boolean overwrDup) {
		if (config == null) {
			return;
		}
		System.out.println("Read configuration file " + configFile);
		Iterator<String> keyIt = config.getKeys();
		while (keyIt.hasNext()) {
			String key = keyIt.next();
			if (key == null || key.isEmpty()) {
				continue;
			}
			try {
				String value = config.getString(key);
				if (value == null) {
					continue;
				}
				if (overwrDup) {
					String oldVal = map.put(key, value);
					if (oldVal != null) {
						System.out.println("Configuration key \"" + key + "\" has old value \"" + oldVal + "\" has been overwritten by new value \"" + value + "\"");
					}
				} else {
					String oldVal = map.putIfAbsent(key, value);
					if (oldVal != null) {
						System.out.println("Configuration key \"" + key + "\" has value \"" + oldVal + "\" NOT be overwrited by new value \"" + value + "\"");
					}
				}
			} catch (Exception ex) {
				System.err.println(ex);
			}
		}
	}

	private static void MapToConfig(Configuration config, ConcurrentMap<String, String> map) {
		TreeSet<String> keySet = new TreeSet<String>();
		keySet.addAll(map.keySet()); //sort theo key
		Iterator<String> keyIt = keySet.iterator();
		while (keyIt.hasNext()) {
			String key = keyIt.next();
			if (key == null || key.isEmpty()) {
				continue;
			}
			try {
				String value = map.get(key);
				if (value == null) {
					continue;
				}
				config.setProperty(key, value);
			} catch (Exception ex) {
				System.err.println(ex);
			}
		}
	}

	private static Configuration LoadFromFile(String path) throws ConfigurationException {
		if (path.endsWith(".ini") || path.endsWith(".properties")) {
			//return new ZINIConfiguration(path);
			RINIConfiguration ret = new RINIConfiguration();
			ret.setDelimiterParsingDisabled(true);
			ret.setFileName(path);
			ret.load();
			return ret;
		} else if (path.endsWith(".xml")) {
			return new XMLConfiguration(path);
		} else {
			throw new ConfigurationException("Unknown configuration file extension");
		}
	}

	private static Configuration LoadFromURL(URL url) throws ConfigurationException {
		if (url == null) {
			return null;
		}
		String path = url.toString();
		if (path.endsWith(".ini") || path.endsWith(".properties")) {
			return new RINIConfiguration(url);
		} else if (path.endsWith(".xml")) {
			return new XMLConfiguration(url);
		} else {
			throw new ConfigurationException("Unknown configuration file extension");
		}
	}

	private static void WriteToFile(String path, ConcurrentMap<String, String> map) throws IOException, ConfigurationException {
		FileConfiguration config;
		if (path.endsWith(".ini") || path.endsWith(".properties")) {
			config = new RINIConfiguration();
		} else if (path.endsWith(".xml")) {
			config = new XMLConfiguration();
		} else {
			throw new ConfigurationException("Unknown configuration file extension");
		}

		MapToConfig(config, map);
		config.save(new FileWriter(path));
	}

	public static String ClazzKeyToKey(Class clazz, String instName, String key) {
		if (key == null) {
			return null;
		}
		assert (clazz != null && instName != null);
		String strClazz = clazz.getSimpleName();
		instName = instName.trim();
		key = key.trim();
		////////////////////////////////////////////////////////////////////////
		CompositeKey.Three compositeKey = new CompositeKey.Three(clazz, instName, key);
		String fqKey = _fqKeysMap.get(compositeKey);
		if (fqKey == null) {
			StringBuilder sb = new StringBuilder(512);
			sb.append(strClazz).append(_ClazzKeySep);
			if (instName.isEmpty() || key.isEmpty()) {
				sb.append(instName).append(key);
			} else {
				sb.append(instName).append(_InstNameKeySep).append(key);
			}
			fqKey = sb.toString();
			//put a new key instead of the key from pool
			//, sothat the map could not compare object referrence
			_fqKeysMap.put(new CompositeKey.Three(clazz, instName, key), fqKey);
		}
		return fqKey;
	}

	////////////////////////////////////////////////////////////////////////////
	///util functions
	///

	public void save() {
		_save();
	}

	public void print(String linePrefix, String separat) {
		TreeSet<String> keySet = new TreeSet<String>();
		keySet.addAll(_configMap.keySet()); //sort theo key
		Iterator<String> keyIt = keySet.iterator();
		while (keyIt.hasNext()) {
			String key = keyIt.next();
			if (key == null || key.isEmpty()) {
				System.out.println("Key is null or empty.");
				continue;
			}
			System.out.print(linePrefix + key + separat);
			try {
				String value = _configMap.get(key);
				if (value == null) {
					System.out.println("<null>");
					continue;
				}
				System.out.println("\"" + value + "\"");
			} catch (Exception ex) {
				System.out.println("Error: " + ex.getMessage());
			}
		}
	}

	public void print() {
		print("+ ", " = ");
	}

	public List<String> getSubKeys(String parentKey) {
		if (parentKey == null || parentKey.isEmpty()) {
			return null;
		}
		List<String> ret = new ArrayList<String>();
		TreeSet<String> keySet = new TreeSet<String>();
		keySet.addAll(_configMap.keySet()); //sort theo key
		Iterator<String> keyIt = keySet.iterator();
		while (keyIt.hasNext()) {
			String key = keyIt.next();
			if (key == null || key.isEmpty() || !key.startsWith(parentKey)) {
				continue;
			}
			ret.add(key);
		}
		return ret;
	}

	public List<String> getSubKeys(Class clazz, String instName) {
		return getSubKeys(ClazzKeyToKey(clazz, instName, ""));
	}

	public List<String> getSubKeys(Class clazz, String instName, String key) {
		return getSubKeys(ClazzKeyToKey(clazz, instName, key));
	}

	////////////////////////////////////////////////////////////////////////////
	///private common methods
	///
	private void _save() {
		if (_mainConfigPath == null) {
			System.out.println("No configuration file is specified");
			return;
		}
		try {
			WriteToFile(_mainConfigPath, _configMap);
			System.out.println("Configuration is saved to file \"" + _mainConfigPath + "\"");
		} catch (Exception ex) {
			System.err.println(ex);
		}
	}

	private void _onChangeProperty(String key, String oldVal, String newVal) {
		assert (key != null && newVal != null);
	}

	private boolean _setPropertyWithEv(String key, String value, boolean addToSmAgent) {
		assert (key != null && value != null);
		boolean changed = false;
		String oldVal = _configMap.put(key, value);
		if (oldVal == null) {
			changed = true;
		} else if (!value.equals(oldVal)) {
			changed = true;
		}

		if (changed) {
			_onChangeProperty(key, oldVal, value);
		}
		return changed;
	}

	private boolean _multiSetPropertiesWithEv(Map<String, String> tvmap, boolean addToSmAgent) {
		assert (tvmap != null);
		boolean changed = false;
		Set<String> keys_ = tvmap.keySet();
		for (String key_ : keys_) {
			if (key_ == null) {
				continue;
			}
			String key = key_.trim();
			if (key.isEmpty()) {
				continue;
			}
			//
			String value_ = tvmap.get(key_);
			if (value_ == null) {
				continue;
			}
			String value = value_.trim();
			if (value.isEmpty()) {
				continue;
			}
			changed |= _setPropertyWithEv(key, value, addToSmAgent);
		}
		return changed;
	}

	private String _getPropertyInternal(String key, boolean addToSmAgent) throws NotExistException {
		assert (key != null);
		String origKey = key;
		String value = _configMap.get(key);
		//
		while (value != null && value.startsWith("@")) {
			key = value.substring(1).trim();
			if (key.equals(origKey)) {
				throw new CircularReferenceException(origKey);
			}
			value = _configMap.get(key);
		}
		//
		
		return value;
	}

	public String _getProperty(String key) throws NotExistException, InvalidParamException {
		if (key == null) {
			throw new InvalidParamException("Key is null");
		}
		key = key.trim();
		if (key.isEmpty()) {
			throw new InvalidParamException("Key is empty");
		}
		return _getPropertyInternal(key, true);
	}

	////////////////////////////////////////////////////////////////////////////
	///public common methods
	///
	public void setProperty(String key, String value, boolean save) {
		if (key == null || value == null) {
			return;
		}
		key = key.trim();
		value = value.trim();
		if (key.isEmpty() || value.isEmpty()) {
			return;
		}
		boolean changed = _setPropertyWithEv(key, value, true);
		if (changed && save) {
			this.save();
		}
	}

	public void setProperty(String key, String value) {
		setProperty(key, value, true);
	}

	public void setProperty(Class clazz, String instName, String key, String value) {
		setProperty(ClazzKeyToKey(clazz, instName, key), value, true);
	}

	public void setProperty(Class clazz, String instName, String key, String value, boolean save) {
		setProperty(ClazzKeyToKey(clazz, instName, key), value, save);
	}

	////////////////////////////////////////////////////////////////////////////
	///specdatatype read with throwing exceptions
	///
	public boolean getBoolean(String key) throws NotExistException, InvalidParamException {
		return RUtil.parseBooleanWEx(_getProperty(key));
	}

	public byte getByte(String key) throws NotExistException, InvalidParamException {
		return Byte.parseByte(_getProperty(key));
	}

	public double getDouble(String key) throws NotExistException, InvalidParamException {
		return Double.parseDouble(_getProperty(key));
	}

	public float getFloat(String key) throws NotExistException, InvalidParamException {
		return Float.parseFloat(_getProperty(key));
	}

	public int getInt(String key) throws NotExistException, InvalidParamException {
		return Integer.parseInt(_getProperty(key));
	}

	public long getLong(String key) throws NotExistException, InvalidParamException {
		return Long.parseLong(_getProperty(key));
	}

	public short getShort(String key) throws NotExistException, InvalidParamException {
		return Short.parseShort(_getProperty(key));
	}

	public String getString(String key) throws NotExistException, InvalidParamException {
		return _getProperty(key);
	}

	public <T extends Enum<T>> T getEnum(Class<T> enumType, String key) throws NotExistException, InvalidParamException {
		if (enumType == null) {
			throw new InvalidParamException("Enum type is null!");
		}
		String enumName = _getProperty(key);
		return Enum.valueOf(enumType, enumName);
	}

	////////////////////////////////////////////////////////////////////////////
	///specdatatype read without throwing exception (use defaultVal instead)
	///
	public boolean getBoolean(String key, boolean defaultVal) {
		try {
			return getBoolean(key);
		} catch (Exception ex) {
			return defaultVal;
		}
	}

	public byte getByte(String key, byte defaultVal) {
		try {
			return getByte(key);
		} catch (Exception ex) {
			return defaultVal;
		}
	}

	public double getDouble(String key, double defaultVal) {
		try {
			return getDouble(key);
		} catch (Exception ex) {
			return defaultVal;
		}
	}

	public float getFloat(String key, float defaultVal) {
		try {
			return getFloat(key);
		} catch (Exception ex) {
			return defaultVal;
		}
	}

	public int getInt(String key, int defaultVal) {
		try {
			return getInt(key);
		} catch (Exception ex) {
			return defaultVal;
		}
	}

	public long getLong(String key, long defaultVal) {
		try {
			return getLong(key);
		} catch (Exception ex) {
			return defaultVal;
		}
	}

	public short getShort(String key, short defaultVal) {
		try {
			return getShort(key);
		} catch (Exception ex) {
			return defaultVal;
		}
	}

	public String getString(String key, String defaultVal) {
		try {
			return getString(key);
		} catch (Exception ex) {
			return defaultVal;
		}
	}

	public <T extends Enum<T>> T getEnum(Class<T> enumType, String key, T defaultVal) {
		try {
			return getEnum(enumType, key);
		} catch (Exception ex) {
			return defaultVal;
		}
	}

	////////////////////////////////////////////////////////////////////////////
	///fromclass and specdatatype read with throwing exceptions
	///
	public boolean getBoolean(Class clazz, String instName, String key) throws NotExistException, InvalidParamException {
		return getBoolean(ClazzKeyToKey(clazz, instName, key));
	}

	public byte getByte(Class clazz, String instName, String key) throws NotExistException, InvalidParamException {
		return getByte(ClazzKeyToKey(clazz, instName, key));
	}

	public double getDouble(Class clazz, String instName, String key) throws NotExistException, InvalidParamException {
		return getDouble(ClazzKeyToKey(clazz, instName, key));
	}

	public float getFloat(Class clazz, String instName, String key) throws NotExistException, InvalidParamException {
		return getFloat(ClazzKeyToKey(clazz, instName, key));
	}

	public int getInt(Class clazz, String instName, String key) throws NotExistException, InvalidParamException {
		return getInt(ClazzKeyToKey(clazz, instName, key));
	}

	public long getLong(Class clazz, String instName, String key) throws NotExistException, InvalidParamException {
		return getLong(ClazzKeyToKey(clazz, instName, key));
	}

	public short getShort(Class clazz, String instName, String key) throws NotExistException, InvalidParamException {
		return getShort(ClazzKeyToKey(clazz, instName, key));
	}

	public String getString(Class clazz, String instName, String key) throws NotExistException, InvalidParamException {
		return getString(ClazzKeyToKey(clazz, instName, key));
	}

	public <T extends Enum<T>> T getEnum(Class<T> enumType, Class clazz, String instName, String key) throws NotExistException, InvalidParamException {
		return getEnum(enumType, ClazzKeyToKey(clazz, instName, key));
	}

	////////////////////////////////////////////////////////////////////////////
	///fromclass and specdatatype read without throwing exception (use defaultVal instead)
	///
	public boolean getBoolean(Class clazz, String instName, String key, boolean defaultVal) {
		try {
			return getBoolean(clazz, instName, key);
		} catch (Exception ex) {
			return defaultVal;
		}
	}

	public byte getByte(Class clazz, String instName, String key, byte defaultVal) {
		try {
			return getByte(clazz, instName, key);
		} catch (Exception ex) {
			return defaultVal;
		}
	}

	public double getDouble(Class clazz, String instName, String key, double defaultVal) {
		try {
			return getDouble(clazz, instName, key);
		} catch (Exception ex) {
			return defaultVal;
		}
	}

	public float getFloat(Class clazz, String instName, String key, float defaultVal) {
		try {
			return getFloat(clazz, instName, key);
		} catch (Exception ex) {
			return defaultVal;
		}
	}

	public int getInt(Class clazz, String instName, String key, int defaultVal) {
		try {
			return getInt(clazz, instName, key);
		} catch (Exception ex) {
			return defaultVal;
		}
	}

	public long getLong(Class clazz, String instName, String key, long defaultVal) {
		try {
			return getLong(clazz, instName, key);
		} catch (Exception ex) {
			return defaultVal;
		}
	}

	public short getShort(Class clazz, String instName, String key, short defaultVal) {
		try {
			return getShort(clazz, instName, key);
		} catch (Exception ex) {
			return defaultVal;
		}
	}

	public String getString(Class clazz, String instName, String key, String defaultVal) {
		try {
			return getString(clazz, instName, key);
		} catch (Exception ex) {
			return defaultVal;
		}
	}

	public <T extends Enum<T>> T getEnum(Class<T> enumType, Class clazz, String instName, String key, T defaultVal) {
		try {
			return getEnum(enumType, clazz, instName, key);
		} catch (Exception ex) {
			return defaultVal;
		}
	}
}
