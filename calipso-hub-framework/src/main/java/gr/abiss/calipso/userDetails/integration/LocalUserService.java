/**
 *
 *
 * Copyright (c) 2007 - 2013 www.Abiss.gr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gr.abiss.calipso.userDetails.integration;

import gr.abiss.calipso.userDetails.util.DuplicateEmailException;

import java.io.Serializable;
import java.util.Map;



/**
 * 
 * @param <T> The local user implementation type
 * @param <ID> The local user implementation identifier type
 */
public interface LocalUserService<ID extends Serializable, T extends LocalUser> {

	public T findByUserNameOrEmail(String userNameOrEmail);

	public T createForImplicitSignup(LocalUser user) throws DuplicateEmailException;

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
	public T findByCredentials(String userNameOrEmail, String userPassword,
			Map<String, String> metadata);

	public T confirmPrincipal(String confirmationToken);

	public void handlePasswordResetRequest(String userNameOrEmail);

	public T handlePasswordResetToken(String userNameOrEmail, String token, String newPassword);

	public T createActive(T resource);

}
