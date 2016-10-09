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
 * but WITHOUUser ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package gr.abiss.calipso.service;

import java.util.Map;

import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.dto.UserInvitationResultsDTO;
import gr.abiss.calipso.model.dto.UserInvitationsDTO;
import gr.abiss.calipso.tiers.service.ModelService;
import gr.abiss.calipso.userDetails.model.ICalipsoUserDetails;
import gr.abiss.calipso.userDetails.util.DuplicateEmailException;

public interface UserService extends ModelService<User, String>{


	/**
	 * Find the user matching the given username or email
	 * @param userNameOrEmail the username or email of the user
	 * @return the matching user, if any
	 */
	public User findByUserNameOrEmail(String userNameOrEmail);
	
	/**
	 * Update the password for the user matching the given credentials
	 * @param userNameOrEmail the username or email of the user
	 * @param oldPassword the user's current password
	 * @param newPassword the new password 
	 * @param newPasswordConfirm the confirmation for the new password
	 * @return the matching user, if any, with the persistent password already updated 
	 */
	public User changePassword(String userNameOrEmail, String oldPassword, String newPassword, String newPasswordConfirm);
	/**
	 * Find the user with the given ID
	 * @param userId
	 * @return
	 */
	public User findById(String userId);
	public User createForImplicitSignup(User user) throws DuplicateEmailException;

	/**
	 * Get a local application user matching the given credentials, after adding
	 * and possibly persisting the given metadata for the match, if any.
	 * 
	 * @param userNameOrEmail
	 *            the username or email
	 * @param userPassword
	 *            the user password
	 * @param metadata
	 *            the metadata to add to the the matching user. May be
	 *            <code>null</code>.
	 * @return the local user or null if no match was found for the given
	 *         credentials
	 */
	public User findByCredentials(String userNameOrEmail, String userPassword,
			Map<String, String> metadata);

	//public User confirmPrincipal(String confirmationToken);

	public void handlePasswordResetRequest(String userNameOrEmail);

	public User handlePasswordResetToken(String userNameOrEmail, String token, String newPassword);

	public User createTest(User resource);

	public void updateLastLogin(ICalipsoUserDetails u);

	public void expireResetPasswordTokens();


	public UserInvitationResultsDTO inviteUsers(UserInvitationsDTO invitations);

}