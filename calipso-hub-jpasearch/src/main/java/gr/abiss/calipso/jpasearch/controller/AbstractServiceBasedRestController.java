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
 * You should have received a copy of the GNU General Public License
 * along with Calipso. If not, see http://www.gnu.org/licenses/agpl.html
 */
package gr.abiss.calipso.jpasearch.controller;

import gr.abiss.calipso.jpasearch.data.ParameterMapBackedPageRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.resthub.common.exception.NotFoundException;
import org.resthub.common.service.CrudService;
import org.resthub.web.controller.ServiceBasedRestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(produces = { "application/json", "application/xml" })
public abstract class AbstractServiceBasedRestController<T, ID extends Serializable, S extends CrudService> extends
		ServiceBasedRestController<T, ID, S> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractServiceBasedRestController.class);

	@Autowired
	private HttpServletRequest request;


	/**
	 * Find all resources matching the given criteria and return a paginated
	 * collection<br/>
	 * REST webservice published : GET
	 * /search?page=0&size=20&sort=propertyName&direction=asc
	 * 
	 * @param page
	 *            Page number starting from 0. default to 0
	 * @param size
	 *            Number of resources by pages. default to 10
	 * @return OK http status code if the request has been correctly processed,
	 *         with the a paginated collection of all resource enclosed in the
	 *         body.
	 */

	// TODO: refactor to use base path
	@Override
	@RequestMapping(value = "search", method = RequestMethod.GET)
	@ResponseBody
	public Page<T> findPaginated(
			@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
			@RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
			@RequestParam(value = "sort", required = false, defaultValue = "id") String sort,
			@RequestParam(value = "direction", required = false, defaultValue = "ASC") String direction) {

		Assert.isTrue(page > 0, "Page index must be greater than 0");

		Order order = new Order(
				direction.equalsIgnoreCase("ASC") ? Sort.Direction.ASC
						: Sort.Direction.DESC, sort);
		List<Order> orders = new ArrayList<Order>(1);
		orders.add(order);
		return this.service.findAll(new ParameterMapBackedPageRequest(request
				.getParameterMap(), page - 1, size, new Sort(orders)));
	}

	// TODO: refactor to OPTIONS on base path
	@RequestMapping(value = "formschema", method = RequestMethod.GET)
	@ResponseBody
	Class getSchema(HttpServletRequest request, HttpServletResponse response) {
		String name = this.getClass().getSimpleName();
		try {
			return Class.forName(name.substring(0, name.indexOf("Service")));
		} catch (ClassNotFoundException e) {
			throw new NotFoundException();
		}
}
