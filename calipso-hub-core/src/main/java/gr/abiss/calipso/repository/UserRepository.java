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
package gr.abiss.calipso.repository;

import gr.abiss.calipso.jpasearch.repository.BaseRepository;
import gr.abiss.calipso.model.User;

import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends BaseRepository<User, String> {

	@Query("select u from User u where u.confirmationToken = ?1")
	public User findByConfirmationToken(String token);

	@Query("select u from User u where (UPPER(u.email) = UPPER(?1) or UPPER(u.userName) = UPPER(?1)) and u.userPassword = ?2 and u.active = true")
	public User findByCredentials(String userNameOrEmail, String password);

	// @Query("select u from User u LEFT JOIN FETCH u.roles where UPPER(u.email) = UPPER(?1) or UPPER(u.userName) = UPPER(?1)) ")
	@Query("select u from User u where UPPER(u.email) = UPPER(?1) or UPPER(u.userName) = UPPER(?1)) ")
	public User findByUserNameOrEmail(String userNameOrEmail);
}
