/**
 *
 *
 * Copyright (c) 2007 - 2013 www.Abiss.gr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
