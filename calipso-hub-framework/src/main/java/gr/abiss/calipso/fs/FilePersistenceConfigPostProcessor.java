/**
 * calipso-hub-framework - A full stack, high level framework for lazy application hackers.
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
package gr.abiss.calipso.fs;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;

import gr.abiss.calipso.utils.ConfigurationFactory;

public class FilePersistenceConfigPostProcessor 
        implements BeanDefinitionRegistryPostProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(FilePersistenceConfigPostProcessor.class);

	@Value("${fs.FilePersistenceService}")
	private String repositoryClassName;
	private Class repositoryClass;

	

	@Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
            throws BeansException {
    	if(this.repositoryClass == null){

    		LOGGER.debug("Adding FilePersistenceService bean using: " + this.repositoryClassName);
    		if(StringUtils.isBlank(this.repositoryClassName)){
    			this.repositoryClassName = ConfigurationFactory.getConfiguration().getString(ConfigurationFactory.FS_IMPL_CLASS);
    		}
    		
    		
			try {
				this.repositoryClass = FilePersistenceConfigPostProcessor.class.forName(this.repositoryClassName);
			} catch (ClassNotFoundException e) {
				LOGGER.error("Failed to obtain repository class: " + this.repositoryClassName + ", will fallback to default");
			}
    	}
    	if(this.repositoryClass == null){
    		this.repositoryClass = DummyFilePersistenceServiceImpl.class;
    	}

        RootBeanDefinition beanDefinition = 
                new RootBeanDefinition(this.repositoryClass); //The service implementation
    	beanDefinition.setTargetType(FilePersistenceService.class); //The service interface
        //beanDefinition.setRole(BeanDefinition.ROLE_APPLICATION);
        registry.registerBeanDefinition(FilePersistenceService.BEAN_ID, beanDefinition );

		LOGGER.debug("Added FilePersistenceService bean using: " + this.repositoryClass.getName());
    }

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		// TODO Auto-generated method stub
		
	}
}