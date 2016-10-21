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
package com.restdude.auth.userAccount.controller;

import com.restdude.app.users.model.User;
import com.restdude.app.users.service.UserService;
import com.restdude.auth.userAccount.model.UserAccountRegistration;
import gr.abiss.calipso.utils.ConfigurationFactory;
import gr.abiss.calipso.web.spring.BadRequestException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;

@Controller
@Api(tags = "Auth", description = "User account operations")
@RequestMapping(value = "/api/auth/account", produces = {"application/json", "application/xml"})
public class UserAccountController {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserAccountController.class);

	private UserService service;

	@Inject
	@Qualifier("userService")
	public void setService(UserService service) {
		this.service = service;
	}

	@RequestMapping(method = RequestMethod.POST)
	@ApiOperation(value = "Register", notes = "Register a new user")
	@ResponseBody
	public User create(@RequestBody UserAccountRegistration resource) {
		LOGGER.debug("create, resource: {}", resource);
		// get config
		Configuration config = ConfigurationFactory.getConfiguration();
		boolean forceCodes = config.getBoolean(ConfigurationFactory.FORCE_CODES, false);

		// require email
		if (StringUtils.isBlank(resource.getEmail())) {
			throw new BadRequestException("Email is required");
		}
		// force registration codes?
		if (forceCodes && StringUtils.isBlank(resource.getRegistrationCode())) {
			throw new BadRequestException("Registration code is required");
		}
		// passwords match?
		if (StringUtils.isNotBlank(resource.getPassword())
				&& StringUtils.isNotBlank(resource.getPasswordConfirmation())
				&& !resource.getPassword().equals(resource.getPasswordConfirmation())) {
			throw new BadRequestException("Password and password confirmation do not match");
		}

		// create user
		User u = resource.asUser();
		LOGGER.debug("create, user: {}", u);
		return this.service.create(u);

	}


}
