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
package gr.abiss.calipso.repository;

import java.util.Date;

import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.dto.UserDTO;
import gr.abiss.calipso.tiers.repository.ModelRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends ModelRepository<User, String> {

//	@Query("select u from User u where u.confirmationToken = ?1")
//	public User findByConfirmationToken(String token);

	@Query("select u from User u where (UPPER(u.email) = UPPER(?1) or UPPER(u.username) = UPPER(?1)) and u.password = ?2 and u.active = true")
	public User findByCredentials(String usernameOrEmail, String password);

	// @Query("select u from User u LEFT JOIN FETCH u.roles where UPPER(u.email) = UPPER(?1) or UPPER(u.username) = UPPER(?1)) ")
	@Query("select u from User u where UPPER(u.email) = UPPER(?1) or UPPER(u.username) = UPPER(?1)) ")
	public User findByUsernameOrEmail(String usernameOrEmail);
	
	// @Query("select u from User u LEFT JOIN FETCH u.roles where UPPER(u.email) = UPPER(?1) or UPPER(u.username) = UPPER(?1)) ")
	@Query("select u from User u where u.id = ?1 or UPPER(u.email) = UPPER(?1) or UPPER(u.username) = UPPER(?1)) ")
	public User findByIdOrUsernameOrEmail(String idOrUsernameOrEmail);

	// @Query("select u from User u LEFT JOIN FETCH u.roles where UPPER(u.email) = UPPER(?1) or UPPER(u.username) = UPPER(?1)) ")
	@Query("select new gr.abiss.calipso.model.dto.UserDTO(u.id, u.firstName, u.lastName, u.username, u.email, u.emailHash) from User u where u.id = ?1 or UPPER(u.email) = UPPER(?1) or UPPER(u.username) = UPPER(?1)) ")
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
