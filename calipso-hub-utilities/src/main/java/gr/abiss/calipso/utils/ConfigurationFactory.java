package gr.abiss.calipso.utils;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a configuration based on calipso.defaults.properties and calipso.properties
 * @author manos
 *
 */
public class ConfigurationFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationFactory.class);
	
	public static final String APP_NAME = "calipso.appName";
	public static final String APP_VERSION = "calipso.appVersion";
	public static final String BASE_URL = "calipso.baseurl";
	public static final String SCRIPT_MAIN = "scriptMain";
	
	public static final String INIT_DATA = "calipso.initData";
	public static final String FILES_DIR = "calipso.files.dir";
	private static CompositeConfiguration config = new CompositeConfiguration();
	
	static {
		String[] propertyFiles = {"calipso.properties", "calipso.defaults.properties"};
		for(String propFile : propertyFiles){
			try {
				config.addConfiguration(new PropertiesConfiguration(propFile));
			} catch (ConfigurationException e) {
				LOGGER.warn("Failed to load configuration " + propFile);
			}
		}
		
	}

	public static Configuration getConfiguration() {
		return config;
	}
}
