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
package gr.abiss.calipso.controller;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import gr.abiss.calipso.fs.FilePersistenceService;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.dto.MetadatumDTO;
import gr.abiss.calipso.service.UserService;
import gr.abiss.calipso.tiers.controller.AbstractNoDeleteModelController;
import gr.abiss.calipso.tiers.controller.IFilesModelController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@Api(tags = "Users", description = "User management operations")
@RequestMapping(value = "/api/rest/users", produces = { "application/json", "application/xml" })
public class UserController extends AbstractNoDeleteModelController<User, String, UserService>  implements IFilesModelController<User, String, UserService>{

	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	FilePersistenceService filePersistenceService;

	@Inject
	@Qualifier(FilePersistenceService.BEAN_ID)
	public void setFilePersistenceService(FilePersistenceService filePersistenceService) {
		this.filePersistenceService = filePersistenceService;
	}

	@Override
	public FilePersistenceService getFilePersistenceService() {
		return this.filePersistenceService;
	}

	@RequestMapping(value = "byUserNameOrEmail/{userNameOrEmail}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get one by username or email", notes = "Get the single user with the given username or email.")
	public User getByUserNameOrEmail(@PathVariable String userNameOrEmail) {
		return this.service.findByUserNameOrEmail(userNameOrEmail);
	}
	
	@RequestMapping(value = "{subjectId}/metadata", method = RequestMethod.PUT)
	@ResponseBody
    @ApiOperation(value = "Add metadatum", notes = "Add or update a resource metadatum")
	public void addMetadatum(@PathVariable String subjectId,
			@RequestBody MetadatumDTO dto) {
		service.addMetadatum(subjectId, dto);
	}
    
}
