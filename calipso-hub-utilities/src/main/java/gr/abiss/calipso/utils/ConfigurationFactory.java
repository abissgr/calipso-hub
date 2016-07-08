/**
 * calipso-hub-utilities - A full stack, high level framework for lazy application hackers.
 * Copyright © 2005 Manos Batsis (manosbatsis gmail)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package gr.abiss.calipso.utils;

import java.net.URL;

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
				if(LOGGER.isDebugEnabled()){
					LOGGER.warn("Loaded configuration from " + propFile);
				}
			} catch (ConfigurationException e) {
				if(LOGGER.isDebugEnabled()){
					LOGGER.warn("Failed to load configuration from " + propFile, e);
				}else{
					LOGGER.warn("Failed to load configuration from " + propFile);
				}
			}
		}
		
	}

	public static Configuration getConfiguration() {
		return config;
	}
}
