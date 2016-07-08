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
package gr.abiss.calipso.tiers.processor;

import gr.abiss.calipso.controller.geography.CountryController;
import gr.abiss.calipso.model.geography.Country;
import gr.abiss.calipso.service.geography.CountryService;
import gr.abiss.calipso.tiers.annotation.ModelResource;
import gr.abiss.calipso.tiers.controller.AbstractModelController;
import gr.abiss.calipso.tiers.controller.ModelController;
import gr.abiss.calipso.tiers.repository.ModelRepository;
import gr.abiss.calipso.tiers.repository.ModelRepositoryFactoryBean;
import gr.abiss.calipso.tiers.service.AbstractModelServiceImpl;
import gr.abiss.calipso.tiers.service.ModelService;
import gr.abiss.calipso.tiers.util.CreateClassCommand;
import gr.abiss.calipso.tiers.util.EntityUtil;
import gr.abiss.calipso.tiers.util.JavassistUtil;
import gr.abiss.calipso.tiers.util.ModelContext;
import gr.abiss.calipso.utils.ClassUtils;
import io.swagger.annotations.Api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.NotFoundException;

import javax.inject.Named;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Generates <code>Repository</code>, <code>Service</code> and
 * <code>Controller</code> tiers for entity beans are annotated with
 * {@link javax.persistence.ModelResource} or
 * {@link gr.abiss.calipso.tiers.annotation.ModelRelatedResource}.
 */
public class ModelDrivenBeansGenerator implements BeanDefinitionRegistryPostProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ModelDrivenBeansGenerator.class);

	private Map<Class<?>, ModelContext> entityModelContextsMap = new HashMap<Class<?>, ModelContext>();

	public ModelDrivenBeansGenerator() {

	}

	/**
	 * Modify the application context's internal bean definition registry after
	 * its standard initialization. All regular bean definitions will have been
	 * loaded, but no beans will have been instantiated yet. This allows for
	 * adding further bean definitions before the next post-processing phase
	 * kicks in.
	 * 
	 * @param registry
	 *            the bean definition registry used by the application context
	 * @throws org.springframework.beans.BeansException
	 *             in case of errors
	 */
	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

		try {
			findModels("**.calipso.model");
			findExistingBeans(registry);
			createBeans(registry);
			LOGGER.info("Completed generation");
		} catch (Exception e) {
			throw new FatalBeanException("Failed generating ApiResources", e);
		}
	

	}

	public void createBeans(BeanDefinitionRegistry registry) throws NotFoundException, CannotCompileException {

		for (Class<?> model : this.entityModelContextsMap.keySet()) {
			// TODO: add related
			// Ensure we have the necessary parent stuff...
			// if (wrapper.isNested()) {
			// writeBeans(registry, wrapper.getParentClass());
			// }
			createBeans(registry, model, this.entityModelContextsMap.get(model));
		}

	}

	private void createBeans(BeanDefinitionRegistry registry, Class<?> model, ModelContext modelContext)
			throws NotFoundException, CannotCompileException {
		Assert.notNull(modelContext, "No model context was found for model type " + model.getName());
		createRepository(registry, modelContext);
		createService(registry, modelContext);
		createController(registry, modelContext);

	}

	/**
	 * Creates a controller for the given resource model. Consider the following
	 * entity annotation:
	 * 
	 * <pre>
	 * {@code
	 * &#64;ModelResource(path = "countries", apiName = "Countries", apiDescription = "Operations about countries") one
	 * }
	 * </pre>
	 * 
	 * created for the Country class:
	 * 
	 * <pre>
	 * {
	 * 	&#64;code
	 * 	&#64;Controller
	 * 	&#64;Api(tags = "Countries", description = "Operations about countries")
	 * 	&#64;RequestMapping(value = "/api/rest/countries", produces = { "application/json",
	 * 			"application/xml" }, consumes = { "application/json", "application/xml" })
	 * 	public class CountryController extends AbstractModelController<Country, String, CountryService> {
	 * 		private static final Logger LOGGER = LoggerFactory.getLogger(CountryController.class);
	 * 	}
	 * }
	 * </pre>
	 * 
	 * @param registry
	 * @param modelContext
	 */
	protected void createController(BeanDefinitionRegistry registry, ModelContext modelContext) {
		if (modelContext.getControllerDefinition() == null) {
			String newBeanNameSuffix = "Controller";
			String newBeanClassName = modelContext.getGeneratedClassNamePrefix() + newBeanNameSuffix;
			String newBeanRegistryName = StringUtils.uncapitalize(newBeanClassName);
			String newBeanPackage = modelContext.getBeansBasePackage() + ".controller";

			// grab the generic types
			List<Class<?>> genericTypes = modelContext.getGenericTypes();
			genericTypes.add(modelContext.getServiceInterfaceType());
			CreateClassCommand createControllerCmd = new CreateClassCommand(newBeanPackage + newBeanClassName,
					modelContext.getControllerSuperClass());
			createControllerCmd.setGenericTypes(genericTypes);
			LOGGER.info("Creating class " + newBeanClassName + 
					", super: " + modelContext.getControllerSuperClass().getName() + 
					", genericTypes: " + genericTypes);
			
			// add @Controller stereotype annotation
			Map<String, Object> controllerMembers = new HashMap<String, Object>();
			controllerMembers.put("value", newBeanRegistryName);
			createControllerCmd.addTypeAnnotation(RestController.class, controllerMembers);

			// set swagger Api annotation
			Map<String, Object> apiMembers = modelContext.getApiAnnotationMembers();
			if (MapUtils.isNotEmpty(apiMembers)) {
				createControllerCmd.addTypeAnnotation(Api.class, apiMembers);
			}

			// set request mapping annotation
			Map<String, Object> members = new HashMap<String, Object>();
			String[] path = {"/api/rest" + modelContext.getPath()};
			members.put("path", path);
			String[] types = { "application/json", "application/xml" };
			members.put("produces", types);
			//members.put("consumes", types);
			createControllerCmd.addTypeAnnotation(RequestMapping.class, members);
			LOGGER.info("Adding request mapping " + members);
			

			// create and register controller class
			Class<?> controllerClass = JavassistUtil.createClass(createControllerCmd);

			// add service dependency
			String serviceDependency = StringUtils.uncapitalise(modelContext.getGeneratedClassNamePrefix()) + "Service";
			AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(controllerClass)
					.addDependsOn(serviceDependency).setAutowireMode(Autowire.BY_NAME.value()).getBeanDefinition();

			LOGGER.info("Registering bean " + newBeanRegistryName);
			registry.registerBeanDefinition(newBeanRegistryName, beanDefinition);

		}
	}

	protected void createService(BeanDefinitionRegistry registry, ModelContext modelContext)
			throws NotFoundException, CannotCompileException {
		if (modelContext.getServiceDefinition() == null) {

			String newBeanClassName = modelContext.getGeneratedClassNamePrefix() + "Service";
			String newBeanRegistryName = StringUtils.uncapitalize(newBeanClassName);

			String newBeanPackage = modelContext.getBeansBasePackage() + ".service";
			// grab the generic types
			List<Class<?>> genericTypes = modelContext.getGenericTypes();

			// extend the base service interface
			Class<?> newServiceInterface = JavassistUtil.createInterface(newBeanPackage + newBeanClassName,
					ModelService.class, genericTypes);
			ArrayList<Class<?>> interfaces = new ArrayList<Class<?>>(1);
			interfaces.add(newServiceInterface);

			// create a service implementation bean
			CreateClassCommand createServiceCmd = new CreateClassCommand(
					newBeanPackage + "impl." + newBeanClassName + "Impl", AbstractModelServiceImpl.class);
			createServiceCmd.setInterfaces(interfaces);
			createServiceCmd.setGenericTypes(genericTypes);
			createServiceCmd.addGenericType(modelContext.getRepositoryType());
			HashMap<String, Object> named = new HashMap<String, Object>();
			named.put("value", newBeanRegistryName);
			createServiceCmd.addTypeAnnotation(Named.class, named);

			// create and register a service implementation bean
			Class<?> serviceClass = JavassistUtil.createClass(createServiceCmd);
			AbstractBeanDefinition def = BeanDefinitionBuilder.rootBeanDefinition(serviceClass).getBeanDefinition();
			registry.registerBeanDefinition(newBeanRegistryName, def);

			// note in context as a dependency to a controller
			modelContext.setServiceDefinition(def);
			modelContext.setServiceInterfaceType(newServiceInterface);
			modelContext.setServiceImplType(serviceClass);

		} else {
			Class<?> serviceType = ClassUtils.getClass(modelContext.getServiceDefinition().getBeanClassName());
			// grab the service interface
			if (!serviceType.isInterface()) {
				Class<?>[] serviceInterfaces = serviceType.getInterfaces();
				if (ArrayUtils.isNotEmpty(serviceInterfaces)) {
					for (Class<?> interfaze : serviceInterfaces) {
						if (ModelService.class.isAssignableFrom(interfaze)) {
							modelContext.setServiceInterfaceType(interfaze);
							break;
						}
					}
				}
			}
			Assert.notNull(modelContext.getRepositoryType(),
					"Found a service bean definition for " + modelContext.getGeneratedClassNamePrefix()
							+ "  but failed to figure out the service interface type.");
		}
	}

	protected void createRepository(BeanDefinitionRegistry registry, ModelContext modelContext)
			throws NotFoundException, CannotCompileException {
		if (modelContext.getRepositoryDefinition() == null) {
			Class<?> repoSUperInterface = ModelRepository.class;

			String newBeanPackage = modelContext.getBeansBasePackage() + ".repository";

			// grab the generic types
			List<Class<?>> genericTypes = modelContext.getGenericTypes();

			// create the new interface
			Class<?> newRepoInterface = JavassistUtil.createInterface(
					newBeanPackage + modelContext.getGeneratedClassNamePrefix() + "Repository", repoSUperInterface,
					genericTypes);

			// register using the uncapitalised className as the key
			AbstractBeanDefinition def = BeanDefinitionBuilder.rootBeanDefinition(ModelRepositoryFactoryBean.class)
					.addPropertyValue("repositoryInterface", newRepoInterface).getBeanDefinition();
			registry.registerBeanDefinition(StringUtils.uncapitalize(newRepoInterface.getSimpleName()), def);

			// note the repo in context
			modelContext.setRepositoryDefinition(def);
			modelContext.setRepositoryType(newRepoInterface);

		} else {
			// mote the repository interface as a possible dependency to a
			// service
			Class<?> beanClass = ClassUtils.getClass(modelContext.getRepositoryDefinition().getBeanClassName());
			// get the actual interface in case of a factory
			if (ModelRepositoryFactoryBean.class.isAssignableFrom(beanClass)) {
				for (PropertyValue propertyValue : modelContext.getRepositoryDefinition().getPropertyValues()
						.getPropertyValueList()) {
					if (propertyValue.getName().equals("repositoryInterface")) {
						Object obj = propertyValue.getValue();
						modelContext.setRepositoryType(String.class.isAssignableFrom(obj.getClass())
								? ClassUtils.getClass(obj.toString()) : (Class<?>) obj);
					}
				}
			}
			Assert.notNull(modelContext.getRepositoryType(),
					"Found a repository (factory) bean definition for " + modelContext.getGeneratedClassNamePrefix()
							+ "  but was unable to figure out the repository type.");
		}
	}

	/**
	 * Iterate over registered beans to find any manually-created components
	 * (Controllers, Services, Repositories) we can skipp from generating.
	 * 
	 * @param registry
	 */
	protected void findExistingBeans(BeanDefinitionRegistry registry) {
		for (String name : registry.getBeanDefinitionNames()) {

			BeanDefinition d = registry.getBeanDefinition(name);

			if (d instanceof AbstractBeanDefinition) {
				AbstractBeanDefinition def = (AbstractBeanDefinition) d;
				// if controller
				if (isOfType(def, ModelController.class)) {
					Class<?> entity = GenericTypeResolver.resolveTypeArguments(
							ClassUtils.getClass(def.getBeanClassName()), ModelController.class)[0];

					ModelContext modelContext = entityModelContextsMap.get(entity);
					if (modelContext != null) {
						modelContext.setControllerDefinition(def);
					}
				}
				// if service
				if (isOfType(def, AbstractModelServiceImpl.class)) {
					Class<?> entity = GenericTypeResolver
							.resolveTypeArguments(ClassUtils.getClass(def.getBeanClassName()), ModelService.class)[0];
					ModelContext modelContext = entityModelContextsMap.get(entity);
					if (modelContext != null) {
						modelContext.setServiceDefinition(def);
					}
				}
				// if repository
				else if (isOfType(def, JpaRepositoryFactoryBean.class) || isOfType(def, JpaRepository.class)) {
					String repoName = (String) def.getPropertyValues().get("repositoryInterface");

					Class<?> repoInterface = ClassUtils.getClass(repoName);
					if (JpaRepository.class.isAssignableFrom(repoInterface)) {
						Class<?> entity = GenericTypeResolver.resolveTypeArguments(repoInterface,
								JpaRepository.class)[0];
						ModelContext modelContext = entityModelContextsMap.get(entity);
						if (modelContext != null) {
							modelContext.setRepositoryDefinition(def);
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
			Class<?> beanClass = ClassUtils.getClass(beanDef.getBeanClassName());
			return targetType.isAssignableFrom(beanClass);
		}
		return false;
	}

	// @Override
	protected void findModels(String basePackage) throws Exception {
		Set<BeanDefinition> entityBeanDefs = EntityUtil.findAnnotatedClasses(basePackage);
		for (BeanDefinition beanDef : entityBeanDefs) {
			Class<?> entity = ClassUtils.getClass(beanDef.getBeanClassName());
			entityModelContextsMap.put(entity, ModelContext.from(entity));
		}
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		// TODO Auto-generated method stub
	}
}
