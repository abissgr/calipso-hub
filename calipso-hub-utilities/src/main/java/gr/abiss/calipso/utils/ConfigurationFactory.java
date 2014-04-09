package gr.abiss.calipso.utils;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Provides a configuration based on calipso.defaults.properties and calipso.properties
 * @author manos
 *
 */
public class ConfigurationFactory {
	public static final String BASE_URL = "calipso.baseurl";
	public static final String INIT_DATA = "calipso.initData";
	private static CompositeConfiguration config = new CompositeConfiguration();
	
	static {
		try {
			config.addConfiguration(new PropertiesConfiguration("calipso.properties"));
			config.addConfiguration(new PropertiesConfiguration("calipso.defaults.properties"));
		} catch (ConfigurationException e) {
			throw new RuntimeException("Failed to load configuration", e);
		}
	}

	public static Configuration getConfiguration() {
		return config;
	}
}
