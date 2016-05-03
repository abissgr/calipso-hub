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
package gr.abiss.calipso.tiers.util;

import org.hibernate.annotations.ManyToAny;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.core.GenericCollectionTypeResolver;
import org.springframework.data.domain.Persistable;
import org.springframework.hateoas.Identifiable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import gr.abiss.calipso.tiers.annotation.ModelRelatedResource;
import gr.abiss.calipso.tiers.annotation.ModelResource;

import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Adapter-ish context class for classes with {@link javax.persistence.ModelResource} 
 * and {@link gr.abiss.calipso.tiers.annotation.ModelRelatedResource}
 * annotations.
 */
public final class ModelContext {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ModelContext.class);

    private Class<?> modelType;
    
    private Class<?> modelIdType;
    
    private final Class<?> parentClass;
    private final String name, path, parentProperty, generatedClassNamePrefix;

    private Class<?> repositoryType;
    private Class<?> serviceInterfaceType;
    private Class<?> serviceImplType;
    private AbstractBeanDefinition repositoryDefinition, serviceDefinition, controllerDefinition;

	public List<Class<?>> getGenericTypes() {
		List<Class<?>> genericTypes = new LinkedList<Class<?>>();
		genericTypes.add(this.getModelType());
		genericTypes.add(this.getModelIdType());
		return genericTypes;
	}
	
    public ModelContext(ModelResource resource, Class<?> domainClass){
    	Assert.notNull(domainClass, "A domain class is required");
        this.name = getPath(domainClass);
        this.path = "/" + name;

        this.modelType = (Class<?>) domainClass;
        this.generatedClassNamePrefix = domainClass.getSimpleName().replace("Model", "").replace("Entity", "");
        this.parentClass = null;
        this.parentProperty = null;
        
        this.modelIdType = EntityUtil.getIdType(domainClass);
        
    }
	
    public ModelContext(ModelRelatedResource resource, Class<?> domainClass){
        this.name = getPath(domainClass);

        String parentProperty = resource.parentProperty();
        this.parentClass = (Class<?>) ReflectionUtils.findField(domainClass, parentProperty).getType();

        String parentName = getPath(parentClass);
        this.path = "/" + parentName + "/{peId}/" + this.name;

        this.modelType = (Class<?>) domainClass;
        this.modelIdType = EntityUtil.getIdType(domainClass);
        this.generatedClassNamePrefix = domainClass.getSimpleName().replace("Model", "").replace("Entity", "");

        this.parentProperty = resource.parentProperty();
    }

    public static ModelContext from(Class<?> domainClass){

        ModelResource ar = domainClass.getAnnotation(ModelResource.class);
        ModelRelatedResource anr = domainClass.getAnnotation(ModelRelatedResource.class);

        ModelContext wrapper = null;
        if( ar != null ){
            wrapper = new ModelContext(ar, domainClass);
        }else if( anr != null ){
            wrapper = new ModelContext(anr, domainClass);
        }else{
            // look for an ancestor who might be a resource
            Class<?> superClass = domainClass.getSuperclass();
            if( superClass != null && !Object.class.equals( superClass )){
                wrapper = from( domainClass.getSuperclass() );
            }
        }
        wrapper.setModelType(domainClass);
        return wrapper;
    }

    private void setModelType(Class<?> modelType) {
		this.modelType = modelType;
	}

	public static ModelContext from(Field field){
        Class<?> domainClass = field.getType();
        if( Collection.class.isAssignableFrom(domainClass) ){
            domainClass = GenericCollectionTypeResolver.getCollectionFieldType(field);
        }
        return from(domainClass);
    };

    
    
    public Class<?> getServiceInterfaceType() {
		return serviceInterfaceType;
	}

	public void setServiceInterfaceType(Class<?> serviceInterfaceType) {
		this.serviceInterfaceType = serviceInterfaceType;
	}

	public Class<?> getServiceImplType() {
		return serviceImplType;
	}

	public void setServiceImplType(Class<?> serviceImplType) {
		this.serviceImplType = serviceImplType;
	}

	public Class<?> getRepositoryType() {
		return repositoryType;
	}

	public void setRepositoryType(Class<?> repositoryType) {
		this.repositoryType = repositoryType;
	}

    public boolean isNested(){
        return parentClass != null;
    }

    public String getGeneratedClassNamePrefix() {
		return generatedClassNamePrefix;
	}

	public boolean isNestedCollection(){
        if( !isNested() ){
            return false;
        }

        ModelRelatedResource anr = modelType.getAnnotation(ModelRelatedResource.class);
        Assert.notNull(anr, "Not a nested resource");

        String parentProperty = anr.parentProperty();
        Field field = ReflectionUtils.findField(modelType, parentProperty);
        if( hasAnnotation(field, OneToOne.class, org.hibernate.mapping.OneToOne.class) ){
            return false;
        }else if( hasAnnotation(field, ManyToOne.class, org.hibernate.mapping.ManyToOne.class,
                ManyToMany.class, ManyToAny.class) ){ // TODO handle more mappings here?
            return true;
        }

        throw new IllegalStateException("No known mapping found");

    }

    private boolean hasAnnotation( Field field, Class<?>... annotations){

        for( Class<?> a : annotations ){
            if( field.isAnnotationPresent( (Class<Annotation>) a) ){
                return true;
            }
        }
        return false;
    }

    public static String getPath(Class<?> domainClass){
        ModelResource ar = domainClass.getAnnotation(ModelResource.class);
        ModelRelatedResource anr = domainClass.getAnnotation(ModelRelatedResource.class);

        String result;
        if( ar != null ){
            result = ar.path();
        }else if( anr != null){
            result = anr.path();
        }else{
            throw new IllegalStateException("Not an entity");
        }

        if( result == null || result.trim().isEmpty() ){
            result = domainClass.getSimpleName();
            result = result.toLowerCase().charAt(0) + result.substring(1) + "s";
        }

        return result;
    }

	public Class<?> getModelIdType() {
		return modelIdType;
	}

	public Class<?> getModelType() {
		return modelType;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public String getParentProperty() {
		return parentProperty;
	}

	public AbstractBeanDefinition getRepositoryDefinition() {
		return repositoryDefinition;
	}

	public void setRepositoryDefinition(AbstractBeanDefinition repositoryDefinition) {
		this.repositoryDefinition = repositoryDefinition;
	}

	public AbstractBeanDefinition getServiceDefinition() {
		return serviceDefinition;
	}

	public void setServiceDefinition(AbstractBeanDefinition serviceDefinition) {
		this.serviceDefinition = serviceDefinition;
	}

	public AbstractBeanDefinition getControllerDefinition() {
		return controllerDefinition;
	}

	public void setControllerDefinition(AbstractBeanDefinition controllerDefinition) {
		this.controllerDefinition = controllerDefinition;
	};
    
    

}
