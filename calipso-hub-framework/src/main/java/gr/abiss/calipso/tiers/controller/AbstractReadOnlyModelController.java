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
package gr.abiss.calipso.tiers.controller;

import java.io.Serializable;

import org.resthub.web.exception.NotImplementedClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Persistable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import gr.abiss.calipso.tiers.service.ModelService;
import io.swagger.annotations.ApiOperation;

/**
 * Base class for read-only model controllers, i.e. with no support for HTTP PUT or DELETE.
 */
public abstract class AbstractReadOnlyModelController<T extends Persistable<ID>, ID extends Serializable, S extends ModelService<T, ID>>
		extends AbstractNoDeleteModelController<T, ID, S> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractReadOnlyModelController.class);

	@Override
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@ApiOperation(hidden = true, value = "Create a resource (unsupported)")
	public T create(T resource) {
		throw new NotImplementedClientException("Method is unsupported.");
	}

	@Override
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	@ResponseBody
	@ApiOperation(hidden = true, value = "Update a resource (unsupported)")
	public T update(ID id, T resource) {
		throw new NotImplementedClientException("Method is unsupported.");
	}

}
