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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Persistable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import gr.abiss.calipso.model.dto.MetadatumDTO;
import gr.abiss.calipso.tiers.service.ModelService;
import io.swagger.annotations.ApiOperation;

public abstract class AbstractModelWithMetadataController<T extends Persistable<ID>, ID extends Serializable, S extends ModelService<T, ID>>
		extends AbstractModelController<T, ID, S> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractModelWithMetadataController.class);
 

	@RequestMapping(value = "{subjectId}/metadata", method = RequestMethod.PUT)
	@ResponseBody
    @ApiOperation(value = "Add metadatum", notes = "Add or updated a resource metadatum")
	public void addMetadatum(@PathVariable ID subjectId,
			@RequestBody MetadatumDTO dto) {
		service.addMetadatum(subjectId, dto);
	}

	@RequestMapping(value = "{subjectId}/metadata/{predicate}", method = RequestMethod.DELETE)
	@ResponseBody
    @ApiOperation(value = "Remove metadatum", notes = "Remove a resource metadatum if it exists")
	public void removeMetadatum(@PathVariable ID subjectId,
			@PathVariable String predicate) {
		service.removeMetadatum(subjectId, predicate);
	}

}
