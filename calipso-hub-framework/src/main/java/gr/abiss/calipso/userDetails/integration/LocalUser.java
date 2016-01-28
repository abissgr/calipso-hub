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

import gr.abiss.calipso.model.interfaces.Metadatum;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;

public interface LocalUser {

	Boolean getActive();

	void setId(String id);
	
	void setActive(Boolean b);

	void setResetPasswordToken(String generateKey);

	void setPassword(String newPassword);

	void setUsername(String newUsername);

	void setEmail(String socialEmail);

	void setFirstName(String socialFirstName);

	String getFirstName();

	void setLastName(String socialLastName);

	String getLastName();
	
	String getFullName();

	String getEmail();

	Serializable getId();

	String getUsername();

	String getPassword();

	String getRedirectUrl();

	List<? extends GrantedAuthority> getRoles();

	Map<String, ? extends Metadatum> getMetadata();

}
