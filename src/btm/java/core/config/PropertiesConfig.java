package btm.java.core.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import btm.java.core.constant.FileDir;

public class PropertiesConfig {
	private static Logger LOG = LogManager.getLogger(PropertiesConfig.class);
	private static LinkedHashMap<String, String> configProperties = new LinkedHashMap<String, String>();

	public static Properties configFile;
	public static boolean isWebEnable = false;
	public static String webServerConfigPath = "";

	public static String getWebRootPath() {
		return webServerConfigPath;
	}

	public static String getRootPath() {
		if (isWebEnable) {
			return getWebRootPath();
		} else {
			URL url = ClassLoader.getSystemResource(".");
			File me = new File(url.getFile());
			return me.getAbsolutePath();
		}
	}

	public static String getConfigPath() {
		return getRootPath();
	}

	public static File getConfigFile() {
		return new File(getConfigPath() + File.separator + FileDir.CONFIG_FILE);
	}

	public static void loadConfigProperties() {
		LOG.info(">>>>>> +[loadConfigProperties] START");
		InputStream is = null;
		configFile = new java.util.Properties();

		try {
			is = new FileInputStream(getConfigFile());
			Properties propFile = new Properties();
			propFile.load(is);
			Enumeration<?> keysEnum = propFile.keys();
			String name = "";
			String value = "";
			while (keysEnum.hasMoreElements()) {
				name = (String) keysEnum.nextElement();
				value = propFile.getProperty(name);
				if (value != null) {
					value = value.trim();
					configProperties.put(name, value);
				}
			}
			if (configProperties.size() > 0) {
				// Repair HUB Schema
				// readHubSchema();
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					LOG.error(e);
				}
			}
		}
		LOG.info("<<<<<< +[loadConfigProperties] END");
	}

	public static boolean hasProperties() {
		return !configProperties.isEmpty();
	}

	public static String get(String key) {
		String value = null;
		try {
			if (key != null && key.length() > 0 && configProperties.containsKey(key)) {
				value = configProperties.get(key);
			} else {
				LOG.warn(key);
			}
		} catch (Exception e) {
			LOG.error(e);
		}
		return value;
	}

	public static String get(String key, String vDefault) {
		String value = null;
		try {
			if (key != null && key.length() > 0 && configProperties.containsKey(key)) {
				value = configProperties.get(key);
			} else {
				LOG.warn(key);
			}
		} catch (Exception e) {
			LOG.error(e);
		}
		return value;
	}
}
