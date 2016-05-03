/**
 * Copyright (c) 2007 - 2013 www.Abiss.gr
 *
 * This file is part of Calipso, a software platform by www.Abiss.gr.
 *
 * Calipso is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Calipso is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Calipso. If not, see http://www.gnu.org/licenses/agpl.html
 */
package gr.abiss.calipso.tiers.processor;

import gr.abiss.calipso.controller.AbstractServiceBasedRestController;
import gr.abiss.calipso.controller.ModelController;
import gr.abiss.calipso.jpasearch.repository.BaseRepository;
import gr.abiss.calipso.jpasearch.repository.RepositoryFactoryBean;
import gr.abiss.calipso.service.GenericEntityService;
import gr.abiss.calipso.service.impl.GenericEntityServiceImpl;
import gr.abiss.calipso.tiers.util.CreateClassCommand;
import gr.abiss.calipso.tiers.util.EntityUtil;
import gr.abiss.calipso.tiers.util.JavassistUtil;
import gr.abiss.calipso.tiers.util.ModelResourceDetails;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Named;

import javassist.CannotCompileException;
import javassist.NotFoundException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.resthub.web.controller.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.PriorityOrdered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.CrudRepository;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * The processor generates <code>Repository</code>, <code>Service</code> and
 * <code>Controller</code> tiers for your entity beans.
 * 
 * @author manos
 *
 */
public class EntityPostProcessor implements BeanDefinitionRegistryPostProcessor{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EntityPostProcessor.class);

	private final String basePackage;
	private final String beansBasePackage;
	private final String controllerBasePackage;
	private final String serviceBasePackage;
	private final String repositoryBasePackage;

	private Map<Class<?>, BeanDefinitionDetails> entityBeanDefinitionsMap = new HashMap<Class<?>, BeanDefinitionDetails>();

//	private ApplicationContext mainContext;
	
	public EntityPostProcessor() {
		this("gr.abiss.calipso.model");
	}

//	@Override
//	public void setApplicationContext(ApplicationContext mainContext) {
//	      this.mainContext = mainContext;
//	}

	public EntityPostProcessor(String basePackage) {
		super();
		this.basePackage = basePackage;
		this.beansBasePackage = basePackage.endsWith(".model") ? basePackage
				.substring(0, basePackage.indexOf(".model")) : basePackage;
		this.serviceBasePackage = beansBasePackage + ".service";
		this.repositoryBasePackage = beansBasePackage + ".repository";
		this.controllerBasePackage = beansBasePackage + ".controller";
	}
	
	/**
	 * Modify the application context's internal bean definition registry after its
	 * standard initialization. All regular bean definitions will have been loaded,
	 * but no beans will have been instantiated yet. This allows for adding further
	 * bean definitions before the next post-processing phase kicks in.
	 * @param registry the bean definition registry used by the application context
	 * @throws org.springframework.beans.BeansException in case of errors
	 */
	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
//		BeanDefinitionRegistry registry = ((BeanDefinitionRegistry) context);
		try {
			afterPropertiesSet();
			readEntities(registry);
			writeBeans(registry);
			LOGGER.info("Completed generation");
		} catch (Exception e) {
			throw new FatalBeanException("Failed generating ApiResources", e);
		}

	}

	public void writeBeans(BeanDefinitionRegistry registry) throws NotFoundException, CannotCompileException {

		for (Class<?> entity : this.entityBeanDefinitionsMap.keySet()) {
			ModelResourceDetails wrapper = ModelResourceDetails.from(entity);
			
			// Ensure we have the necessary parent stuff...
//			if (wrapper.isNested()) {
//				writeBeans(registry, wrapper.getParentClass());
//			}

			writeBeans(registry, entity);

		}

	}

	private void writeBeans(BeanDefinitionRegistry registry, Class<?> entity) throws NotFoundException,
			CannotCompileException {

		ModelResourceDetails wrapper = ModelResourceDetails.from(entity);
		BeanDefinitionDetails details = entityBeanDefinitionsMap.get(entity);

		createRepository(registry, entity, wrapper, details);
		createService(registry, entity, wrapper, details);
		createController(registry, entity, wrapper, details);

	}

	protected void createController(BeanDefinitionRegistry registry, Class<?> entity, ModelResourceDetails wrapper,
			BeanDefinitionDetails details) {
		if (details.controller == null) {
			String newBeanNameSuffix = "Controller";
			String newBeanClassName = entity.getSimpleName().replace("Model", "").replace("Entity", "") + newBeanNameSuffix;
			String newBeanRegistryName = StringUtils.uncapitalize(newBeanClassName);
			String newBeanPackage = this.controllerBasePackage + '.';
			
			// grab the generic types
			ArrayList<Class<?>> genericTypes = getGenericTypes(wrapper);
			genericTypes.add(details.serviceInterface);
			
			CreateClassCommand createControllerCmd = new CreateClassCommand(newBeanPackage + newBeanClassName,
					AbstractServiceBasedRestController.class);
			createControllerCmd.setGenericTypes(genericTypes);
			
			// add @Controller stereotype
			createControllerCmd.addTypeAnnotation(Controller.class, null);
			
			// set request mapping
			Map<String, Object> members = new HashMap<String, Object>();
			String[] path = {"/api/rest" + wrapper.getPath()};
			members.put("value", path);
			String[] produces = {"application/json", "application/xml"};
			members.put("produces", produces);
			createControllerCmd.addTypeAnnotation(RequestMapping.class, members);
			
			// create and register controller class
			Class<?> controllerClass = JavassistUtil.createClass(createControllerCmd);
			LOGGER.info("Created Controller: " + controllerClass.getName());
			if(ArrayUtils.isNotEmpty(controllerClass.getAnnotations())){
				for(Annotation annot : controllerClass.getAnnotations()){
					LOGGER.info("Controller Annotation: " + annot);
				}
			}
			//	BeanDefinition beanDefinition = new RootBeanDefinition(TestController.class, Autowire.BY_TYPE.value(), true);
			String serviceDependency = StringUtils.uncapitalize(entity.getSimpleName()) + "Service"; 
//			LOGGER.info("Adding controller dependency: " +serviceDependency);
			AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(controllerClass)
					.addDependsOn(serviceDependency)
					.setAutowireMode(Autowire.BY_TYPE.value())
					.getBeanDefinition();
			registry.registerBeanDefinition(newBeanRegistryName, beanDefinition);

		}
	}

	protected void createRepository(BeanDefinitionRegistry registry, Class<?> entity, ModelResourceDetails wrapper,
			BeanDefinitionDetails details) throws NotFoundException, CannotCompileException {
		if (details.repositoryDefinition == null) {
			Class<?> repoSUperInterface = BaseRepository.class;
			String newBeanNameSuffix = "Repository";
			String newBeanClassName = entity.getSimpleName().replace("Model", "").replace("Entity", "") + newBeanNameSuffix;
			String newBeanRegistryName = StringUtils.uncapitalize(newBeanClassName);
			String newBeanPackage = this.repositoryBasePackage + '.';

			// grab the generic types
			ArrayList<Class<?>> genericTypes = getGenericTypes(wrapper);
			
			// create and register the new interface
			Class<?> newRepoInterface = JavassistUtil.createInterface(newBeanPackage + newBeanClassName,
					repoSUperInterface, genericTypes);
			AbstractBeanDefinition def = BeanDefinitionBuilder.rootBeanDefinition(RepositoryFactoryBean.class)
					.addPropertyValue("repositoryInterface", newRepoInterface).getBeanDefinition();
			registry.registerBeanDefinition(newBeanRegistryName, def);

			// note the repo
			details.repositoryDefinition = def;
			details.repositoryClass = newRepoInterface;
			details.repositoryBeanName = newBeanRegistryName;
			
			LOGGER.info("Created Repository: " + newRepoInterface.getName());
			if(ArrayUtils.isNotEmpty(newRepoInterface.getAnnotations())){
				for(Annotation annot : newRepoInterface.getAnnotations()){
					LOGGER.info("Controller Annotation: " + annot);
				}
			}
			LOGGER.info("Generated registry entry '" + newBeanRegistryName + "' for model " + entity.getSimpleName()
					+ " . Generic signature: " + GenericTypeResolver.resolveTypeArguments(newRepoInterface, repoSUperInterface));
		} else {
			LOGGER.info("Found repository for {}", entity.getSimpleName());
		}
	}

	protected void createService(BeanDefinitionRegistry registry, Class<?> entity, ModelResourceDetails wrapper,
			BeanDefinitionDetails details) throws NotFoundException, CannotCompileException {
		if (details.serviceDefinition == null) {

			String newBeanNameSuffix = "Service";
			String newBeanClassName = entity.getSimpleName().replace("Model", "").replace("Entity", "") + newBeanNameSuffix;
			String newBeanRegistryName = StringUtils.uncapitalize(newBeanClassName);
			String newBeanPackage = this.serviceBasePackage + '.';
			// grab the generic types
			ArrayList<Class<?>> genericTypes = getGenericTypes(wrapper);

			// extend the base service interface
			Class<?> newServiceInterface = JavassistUtil.createInterface(newBeanPackage + newBeanClassName,
					GenericEntityService.class, genericTypes);
			ArrayList<Class<?>> interfaces = new ArrayList<Class<?>>(1);
			interfaces.add(newServiceInterface);

			// create a service implementation bean
			CreateClassCommand createServiceCmd = new CreateClassCommand(newBeanPackage + "impl."
					+ newBeanClassName + "Impl", GenericEntityServiceImpl.class);
			createServiceCmd.setInterfaces(interfaces);
			createServiceCmd.setGenericTypes(genericTypes);
			createServiceCmd.addGenericType(details.repositoryClass);
			HashMap<String, Object> named = new HashMap<String, Object>();
			named.put("value", newBeanRegistryName);
			createServiceCmd.addTypeAnnotation(Named.class, named);
			

			// create and register a service implementation bean
			Class<?> serviceClass = JavassistUtil.createClass(createServiceCmd);
			AbstractBeanDefinition def = BeanDefinitionBuilder.rootBeanDefinition(serviceClass).getBeanDefinition();
			registry.registerBeanDefinition(newBeanRegistryName, def);
			

			LOGGER.info("Created Service: " + serviceClass.getName());
			if(ArrayUtils.isNotEmpty(serviceClass.getAnnotations())){
				for(Annotation annot : serviceClass.getAnnotations()){
					LOGGER.info("Controller Annotation: " + annot);
				}
			}
			details.serviceDefinition = def;
			details.serviceInterface = newServiceInterface;
			details.serviceClass = serviceClass;
			LOGGER.info("generateBeansForEntity: serviceClass {}", serviceClass.getName() + ":  " + serviceClass);

		} else {
			details.serviceClass = this.getClass(details.serviceDefinition.getBeanClassName());
			// grab the service interface
			if(!details.serviceClass.isInterface()){
				Class<?>[] serviceInterfaces = details.serviceClass.getInterfaces();
				if(ArrayUtils.isNotEmpty(serviceInterfaces)){
					for(Class<?> interfaze : serviceInterfaces){
						if(GenericEntityService.class.isAssignableFrom(interfaze)){
							details.serviceInterface = interfaze;
							break;
						}
					}
				}
			}
			LOGGER.info("Found service for {}: {}", entity.getSimpleName(), details.serviceClass.getSimpleName());
		}
	}

	protected ArrayList<Class<?>> getGenericTypes(ModelResourceDetails wrapper) {
		ArrayList<Class<?>> genericTypes = new ArrayList<Class<?>>(2);
		genericTypes.add(wrapper.getDomainClass());
		genericTypes.add(wrapper.getIdClass());
		return genericTypes;
	}

	/**
	 * Iterate over registered beans to find any manually-created componenets we
	 * can skipp from generating.
	 * 
	 * @param registry
	 */
	public void readEntities(BeanDefinitionRegistry registry) {
		for (String name : registry.getBeanDefinitionNames()) {

			BeanDefinition d = registry.getBeanDefinition(name);

			if (d instanceof AbstractBeanDefinition) {
				AbstractBeanDefinition def = (AbstractBeanDefinition) d;

				if (isOfType(def, ModelController.class)) {
					Class<?> entity = GenericTypeResolver.resolveTypeArguments(this.getClass(def.getBeanClassName()),
							ModelController.class)[0];

					BeanDefinitionDetails details = entityBeanDefinitionsMap.get(entity);
					if (details != null) {
						details.controller = def;
					}
				}

				if (isOfType(def, GenericEntityServiceImpl.class)) {
					// Class<?> entity = getEntityType(def,
					// GenericEntityServiceImpl.class);
					Class<?> entity = GenericTypeResolver.resolveTypeArguments(
							this.getClass(def.getBeanClassName()),
							GenericEntityService.class)[0];
					BeanDefinitionDetails details = entityBeanDefinitionsMap.get(entity);
					if (details != null) {
						details.serviceDefinition = def;
					}
				}
				// if repository
				else if (isOfType(def, JpaRepositoryFactoryBean.class)) {
					String repoName = (String) def.getPropertyValues().get("repositoryInterface");

					Class<?> repoInterface = this.getClass(repoName);
					if (JpaRepository.class.isAssignableFrom(repoInterface)) {
						Class<?> entity = GenericTypeResolver.resolveTypeArguments(repoInterface, JpaRepository.class)[0];
						BeanDefinitionDetails details = entityBeanDefinitionsMap.get(entity);
						if (details != null) {
							details.repositoryDefinition = def;
						}
					}
				}

			}

		}
	}

	/**
	 * Checks if the given BeanDefinition extends/impleents the given target
	 * type
	 * 
	 * @param beanDef
	 * @param targetType
	 * @return
	 */
	protected boolean isOfType(BeanDefinition beanDef, Class<?> targetType) {
		if (beanDef.getBeanClassName() != null) {
			Class<?> beanClass = this.getClass(beanDef.getBeanClassName());
			return targetType.isAssignableFrom(beanClass);
		}
		return false;
	}

	/**
	 * Simple wrapper to RuntimeException
	 * 
	 * @param className
	 * @return
	 */
	protected Class<?> getClass(String className) {
		Class<?> clazz = null;
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return clazz;
	}

	//@Override
	public void afterPropertiesSet() throws Exception {
		LOGGER.info("afterPropertiesSet");
		Set<BeanDefinition> entityBeanDefs = EntityUtil.findAnnotatedClasses(basePackage);
		for (BeanDefinition beanDef : entityBeanDefs) {
			LOGGER.info("afterPropertiesSet beanDef: " + beanDef);
			Class<?> entity = this.getClass(beanDef.getBeanClassName());
			entityBeanDefinitionsMap.put(entity, new BeanDefinitionDetails());
		}
	}

	private class BeanDefinitionDetails {
		AbstractBeanDefinition repositoryDefinition, serviceDefinition, controller, assembler;
		Class<?> repositoryClass, serviceClass, serviceInterface;
		String repositoryBeanName;
	}

	private String getLowerCasePrefix(String s) {
		char c[] = s.toCharArray();
		c[0] += 32;
		return new String(c);
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		// TODO Auto-generated method stub
	}
}
