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


import gr.abiss.calipso.jpasearch.data.ParameterMapBackedPageRequest;
import gr.abiss.calipso.jpasearch.data.RestrictionBackedPageRequest;
import gr.abiss.calipso.jpasearch.model.FormSchema;
import gr.abiss.calipso.jpasearch.model.structuredquery.Restriction;
import gr.abiss.calipso.jpasearch.service.GenericService;
import gr.abiss.calipso.model.dto.MetadatumDTO;
import gr.abiss.calipso.model.entities.AbstractPersistable;
import gr.abiss.calipso.model.entities.FormSchemaAware;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.resthub.common.exception.NotFoundException;
import org.resthub.common.view.ResponseView;
import org.resthub.web.controller.ServiceBasedRestController;
import org.resthub.web.exception.NotImplementedClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Persistable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;

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
