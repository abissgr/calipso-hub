/**
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
package gr.abiss.calipso.userDetails.model;

import gr.abiss.calipso.model.interfaces.Metadatum;
import gr.abiss.calipso.userDetails.integration.LocalUser;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;

public class SimpleLocalUser implements LocalUser {

	private Boolean active;
	private String confirmationToken;
	private String resetPasswordToken;
	private String userPassword;
	private String userName;
	private String email;
	private String firstName;
	private String lastName;
	private Serializable id;
	private String redirectUrl;
	private Collection<? extends GrantedAuthority> roles;
	private Map<String, ? extends Metadatum> metadata;

	@Override
	public Boolean getActive() {
		return this.active;
	}

	@Override
	public void setActive(Boolean active) {
		this.active = active;
	}

	@Override
	public void setConfirmationToken(String confirmationToken) {
		this.confirmationToken = confirmationToken;
	}

	@Override
	public void setResetPasswordToken(String resetPasswordToken) {
		this.resetPasswordToken = resetPasswordToken;
	}

	@Override
	public void setUserPassword(String newPassword) {
		this.userPassword = newPassword;
	}

	@Override
	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Override
	public String getFirstName() {
		return this.firstName;
	}

	@Override
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String getLastName() {
		return this.lastName;
	}

	@Override
	public String getEmail() {
		return this.email;
	}

	@Override
	public Serializable getId() {
		return this.id;
	}

	@Override
	public String getUserName() {
		return this.userName;
	}

	@Override
	public String getUserPassword() {
		return this.userPassword;
	}

	@Override
	public String getRedirectUrl() {
		return this.redirectUrl;
	}

	@Override
	public Collection<? extends GrantedAuthority> getRoles() {
		return this.roles;
	}

	@Override
	public Map<String, ? extends Metadatum> getMetadata() {
		return this.metadata;
	}

}
