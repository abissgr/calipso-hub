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
 * You should have received a copy of the GNU General Public License
 * along with Calipso. If not, see http://www.gnu.org/licenses/agpl.html
 */
package gr.abiss.calipso.service;

import gr.abiss.calipso.model.User;

import org.resthub.common.service.CrudService;

/**
 * This class describes a service interface that could
 * be useful for RPC clients.
 * This contract module can be distributed to RPC clients, since it's got no hard dependency.
 */
public interface UserService extends CrudService<User, String> {

	User findByCredentials(String userNameOrEmail, String password);

}