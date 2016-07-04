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
package gr.abiss.calipso.tiers.annotation;

import java.io.Serializable;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import gr.abiss.calipso.tiers.controller.AbstractNoDeleteModelController;
import gr.abiss.calipso.tiers.controller.ModelController;


//Make the annotation available at runtime:
@Retention(RetentionPolicy.RUNTIME)
//Allow to use only on types:
@Target(ElementType.TYPE)
@Documented
/**
 * <p>Marks a Model as candidate for tiers code generation (Controller, Service, Repository)</p>
 * 
 *  <p>For example:</p>
 *  
 *  <pre class="code">
 * &#064;ModelResource(
 * 		path = "countries", 
 * 		apiName = "Countries", 
 * 		apiDescription = "Operations about countries", 
 * 		controllerSuperClass = AbstractModelController.class
 * 	)
 * </pre>
 * 
 *  <p>Will generate the following controller:</p>
 *  
 *  <pre class="code">
 * 
 * import org.slf4j.Logger;
 * import org.slf4j.LoggerFactory;
 * import org.springframework.stereotype.Controller;
 * import org.springframework.web.bind.annotation.RequestMapping;
 * 
 * import gr.abiss.calipso.model.geography.Country;
 * import gr.abiss.calipso.service.geography.CountryService;
 * import gr.abiss.calipso.tiers.controller.AbstractModelController;
 * import io.swagger.annotations.Api;
 * 
 * &#064;Controller
 * &#064;Api(tags = "Countries", description = "Operations about countries")
 * &#064;RequestMapping(
 *  value = "/api/rest/countries", 
 * 	produces = { "application/json", "application/xml" }
 * )
 * public class CountryController extends AbstractModelController<Country, String, CountryService> {
 * 
 * 		private static final Logger LOGGER = LoggerFactory.getLogger(CountryController.class);
 * 
 * }
 * </pre>
 * 
 * 
 * 
 * 
 */
public @interface ModelResource {

    Class<? extends Serializable> idClass() default String.class;
    /**
     * The superclass for the generated controller. must implement 
     * {@link gr.abiss.calipso.tiers.controller.ModelController}. The default 
     * is {@link gr.abiss.calipso.tiers.controller.AbstractNoDeleteModelController}.
     */
    @SuppressWarnings("rawtypes")
	Class<? extends ModelController> controllerSuperClass() default AbstractNoDeleteModelController.class;

    /**
     * The request mapping path for the generated controller
     */
    String path();
    /**
     * 
     * The API (grouping) name for the generated controller. Used for swagger documentation.
     */
    String apiName() default "";
    /**
     * 
     * The API description for the generated controller. Used for swagger documentation.
     */
    String apiDescription() default "";
    
    
}
