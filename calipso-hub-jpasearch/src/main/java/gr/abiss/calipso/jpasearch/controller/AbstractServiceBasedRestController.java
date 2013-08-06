/**
 * Copyright (c) 2007 - 2013 www.Abiss.gr
 *
 * This file is part of Calipso, a software platform by www.Abiss.gr.
 *
 * Calipso is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Calipso is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser Public License
 * along with Calipso. If not, see http://www.gnu.org/licenses/lgpl-3.0.txt
 */
package gr.abiss.calipso.jpasearch.controller;


import gr.abiss.calipso.jpasearch.data.ParameterMapBackedPageRequest;
import gr.abiss.calipso.jpasearch.model.FormSchema;
import gr.abiss.calipso.jpasearch.service.GenericService;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.resthub.common.exception.NotFoundException;
import org.resthub.common.service.CrudService;
import org.resthub.web.controller.ServiceBasedRestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Persistable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(produces = { "application/json", "application/xml" })
public abstract class AbstractServiceBasedRestController<T, ID extends Serializable, S extends CrudService<T, ID>>
		extends
		ServiceBasedRestController<T, ID, S> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractServiceBasedRestController.class);

	@Autowired
	private HttpServletRequest request;
	
    /**
     * {@inheritDoc}
     */
    @Override
    public Page<T> findPaginated(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "direction", required = false, defaultValue = "") String direction,
            @RequestParam(value = "properties", required = false) String properties) {
        Assert.isTrue(page > 0, "Page index must be greater than 0");
        Assert.isTrue(direction.isEmpty() || direction.equalsIgnoreCase(Sort.Direction.ASC.toString()) || direction.equalsIgnoreCase(Sort.Direction.DESC.toString()), "Direction should be ASC or DESC");
        if(direction.isEmpty()) {
            return this.service.findAll(new ParameterMapBackedPageRequest(request
    				.getParameterMap(), page - 1, size));
        } else {
            Assert.notNull(properties);
            return this.service.findAll(new ParameterMapBackedPageRequest(request
    				.getParameterMap(), page - 1, size, new Sort(Sort.Direction.fromString(direction.toUpperCase()), properties.split(","))));
        }
    }

	// TODO: refactor to OPTIONS on base path?
	@RequestMapping(value = "form-schema", produces = { "application/json" }, method = RequestMethod.GET)
	@ResponseBody
	public FormSchema getSchema(
			@RequestParam(value = "mode", required = false, defaultValue = "search") String mode) {
		Assert.isTrue(mode == null 
				|| mode.equalsIgnoreCase("SEARCH") 
				|| mode.equalsIgnoreCase("CREATE") 
				|| mode.equalsIgnoreCase("UPDATE"));
		mode = mode.toUpperCase();
		try {
			FormSchema schema = new FormSchema();
			schema.setDomainClass(
					((GenericService<Persistable<ID>, ID>) this.service)
							.getDomainClass());
			schema.setType(FormSchema.Type.valueOf(mode));
			return schema;
		} catch (Exception e) {
			throw new NotFoundException();
		}
	}
}
