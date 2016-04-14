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
package gr.abiss.calipso.userDetails.service;

import gr.abiss.calipso.userDetails.integration.LocalUser;
import gr.abiss.calipso.userDetails.model.ICalipsoUserDetails;
import gr.abiss.calipso.userDetails.model.UserDetails;
import gr.abiss.calipso.userDetails.util.DuplicateEmailException;

import org.resthub.common.service.CrudService;


public interface UserDetailsService extends CrudService<ICalipsoUserDetails, String> {

	ICalipsoUserDetails resetPassword(ICalipsoUserDetails userDetails);

	void handlePasswordResetRequest(String userNameOrEmail);

//	ICalipsoUserDetails confirmPrincipal(String confirmationToken);

	ICalipsoUserDetails createForImplicitSignup(LocalUser user)
			throws DuplicateEmailException;

	ICalipsoUserDetails getPrincipal();

	LocalUser getPrincipalLocalUser();

	void updateLastLogin(ICalipsoUserDetails u);
}