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

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.resthub.web.exception.NotImplementedClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Persistable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import gr.abiss.calipso.model.cms.BinaryFile;
import gr.abiss.calipso.tiers.service.ModelService;
import gr.abiss.calipso.utils.ConfigurationFactory;
import io.swagger.annotations.ApiOperation;

/**
 * Base class for model controllers that must not support HTTP DELETE operations.
 */
@RequestMapping(produces = { "application/json", "application/xml" })
public abstract class AbstractNoDeleteModelController<T extends Persistable<ID>, ID extends Serializable, S extends ModelService<T, ID>>
		extends
		AbstractModelController<T, ID, S> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNoDeleteModelController.class);

	@Override
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	@ApiOperation(hidden = true, value = "Delete a resource (unsupported)")
	public void delete(ID id) {
		throw new NotImplementedClientException("Method is unsupported.");
	}

	@Override
    @RequestMapping(method = RequestMethod.DELETE)
	@ApiOperation(hidden = true, value = "Delete all resources (unsupported)")
	public void delete() {
		throw new NotImplementedClientException("Method is unsupported.");
	}
	
	@Override
    @ApiOperation(hidden = true, value = "Delete an uploaded file")
    @RequestMapping(value = "{subjectId}/uploads/{propertyName}/{id}", method = RequestMethod.DELETE)
    public @ResponseBody List deleteById(@PathVariable String subjectId, @PathVariable String propertyName, @PathVariable String id) {
		throw new NotImplementedClientException("Method is unsupported.");
    }

	@RequestMapping(value = "{subjectId}/metadata/{predicate}", method = RequestMethod.DELETE)
	@ResponseBody
    @ApiOperation(hidden = true, value = "Remove metadatum")
	public void removeMetadatum(@PathVariable ID subjectId,
			@PathVariable String predicate) {
		throw new NotImplementedClientException("Method is unsupported.");
		}
}
