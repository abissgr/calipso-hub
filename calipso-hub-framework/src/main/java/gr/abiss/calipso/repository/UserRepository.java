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
package gr.abiss.calipso.repository;

import java.util.Date;

import gr.abiss.calipso.jpasearch.repository.BaseRepository;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.UserDTO;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends BaseRepository<User, String> {

//	@Query("select u from User u where u.confirmationToken = ?1")
//	public User findByConfirmationToken(String token);

	@Query("select u from User u where (UPPER(u.email) = UPPER(?1) or UPPER(u.username) = UPPER(?1)) and u.password = ?2 and u.active = true")
	public User findByCredentials(String usernameOrEmail, String password);

	// @Query("select u from User u LEFT JOIN FETCH u.roles where UPPER(u.email) = UPPER(?1) or UPPER(u.username) = UPPER(?1)) ")
	@Query("select u from User u where UPPER(u.email) = UPPER(?1) or UPPER(u.username) = UPPER(?1)) ")
	public User findByUsernameOrEmail(String usernameOrEmail);

	// @Query("select u from User u LEFT JOIN FETCH u.roles where UPPER(u.email) = UPPER(?1) or UPPER(u.username) = UPPER(?1)) ")
	@Query("select new gr.abiss.calipso.model.UserDTO(u.id, u.firstName, u.lastName, u.username, u.email, u.emailHash) from User u where u.id = ?1 or UPPER(u.email) = UPPER(?1) or UPPER(u.username) = UPPER(?1)) ")
	public UserDTO findAsLink(String usernameOrEmailOrId);
	// @Query("select u from User u LEFT JOIN FETCH u.roles where UPPER(u.email) = UPPER(?1) or UPPER(u.username) = UPPER(?1)) ")

	@Modifying
	@Query("UPDATE User AS u SET u.lastLogin = CURRENT_TIMESTAMP() WHERE u.id = ?1")
	public void updateLastLogin(String userId);
	
	@Modifying
	@Query("UPDATE User AS u SET u.resetPasswordTokenCreated = NULL, u.resetPasswordToken = NULL "
			+ "WHERE u.resetPasswordTokenCreated IS NOT NULL and u.resetPasswordTokenCreated  < ?1")
	public void expireResetPasswordTokens(Date yesterday);
}
