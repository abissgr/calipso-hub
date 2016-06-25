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
 * You should have received a copy of the GNU Affero General Public License
 * along with Calipso. If not, see http://www.gnu.org/licenses/agpl.html
 */
package gr.abiss.calipso.service;

import java.util.Map;

import gr.abiss.calipso.model.User;
import gr.abiss.calipso.tiers.service.GenericService;
import gr.abiss.calipso.tiers.service.ModelService;
import gr.abiss.calipso.userDetails.integration.LocalUserService;

public interface UserService extends ModelService<User, String>, LocalUserService<String, User> {

	/**
	 * Get the user matching the given credentials
	 * @param userNameOrEmail the username or email of the user
	 * @param  password the user's current password
	 * @param  metadata the metadata to add to the user
	 * @return the matching user, if any
	 */
	User findByCredentials(String userNameOrEmail, String password, Map metadata);

}