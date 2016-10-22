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
package com.restdude.app.users.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.restdude.app.users.model.UserRegistrationCode;
import com.restdude.app.users.model.UserRegistrationCodeBatch;
import com.restdude.app.users.service.UserRegistrationCodeBatchService;
import gr.abiss.calipso.model.base.AbstractSystemUuidPersistable;
import gr.abiss.calipso.tiers.controller.AbstractNoDeleteModelController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@Api(tags = "RegistrationCodeBatches", description = "Codes management (admin, operator)")
@RequestMapping(value = "/api/rest/registrationCodeBatches", produces = {"application/json", "application/xml"})
public class UserRegistrationCodeBatchController extends AbstractNoDeleteModelController<UserRegistrationCodeBatch, String, UserRegistrationCodeBatchService> {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserRegistrationCodeBatchController.class);

	@RequestMapping(value = "{id}/csv", method = RequestMethod.GET, produces = "text/csv")
	@ResponseBody
	@ApiOperation(value = "Export batch to a spreadsheet (CSV) report")
	@JsonView(AbstractSystemUuidPersistable.ItemView.class)
	public List<UserRegistrationCode> exportToCsv(@ApiParam(name = "id", required = true, value = "string") @PathVariable String id) {
		// TODO
		return null;
	}

}
