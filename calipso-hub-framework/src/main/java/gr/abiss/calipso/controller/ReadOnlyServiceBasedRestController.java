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
package gr.abiss.calipso.controller;


import gr.abiss.calipso.jpasearch.service.GenericService;

import java.io.Serializable;

import org.resthub.web.exception.NotImplementedClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Persistable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.wordnik.swagger.annotations.Api;

@Controller
@RequestMapping(produces = { "application/json", "application/xml" })
@Api(description = "All generic operations for entities", value = "")
public abstract class ReadOnlyServiceBasedRestController<T extends Persistable<ID>, ID extends Serializable, S extends GenericService<T, ID>>
		extends
		AbstractServiceBasedRestController<T, ID, S> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReadOnlyServiceBasedRestController.class);

	@Override
	@RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
	public T create(T resource) {
		throw new NotImplementedClientException("Method is unsupported.");
	}

	@Override
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
    @ResponseBody
	public T update(ID id, T resource) {
		throw new NotImplementedClientException("Method is unsupported.");
	}

	@Override
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void delete(ID id) {
		throw new NotImplementedClientException("Method is unsupported.");
	}
    
}
