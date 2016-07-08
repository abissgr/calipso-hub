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
package gr.abiss.calipso.userDetails.model;

import gr.abiss.calipso.model.interfaces.Metadatum;
import gr.abiss.calipso.userDetails.integration.LocalUser;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.GrantedAuthority;

public class SimpleLocalUser implements LocalUser {

	private Boolean active;
//	private String confirmationToken;
	private String resetPasswordToken;
	private String password;
	private String username;
	private String email;
	private String firstName;
	private String lastName;
	private Serializable id;
	private String redirectUrl;
	private List<? extends GrantedAuthority> roles;
	private Map<String, ? extends Metadatum> metadata;

	@Override
	public String getFullName() {
		StringBuffer s = new StringBuffer("");
		if(StringUtils.isNotBlank(this.getFirstName())){
			s.append(this.getFirstName());
			if(StringUtils.isNotBlank(this.getLastName())){
				s.append(' ');
			}
		}
		if(StringUtils.isNotBlank(this.getLastName())){
			s.append(this.getLastName());
		}
		return s.toString();

	}
	
	@Override
	public Boolean getActive() {
		return this.active;
	}

	@Override
	public void setActive(Boolean active) {
		this.active = active;
	}
//
//	@Override
//	public void setConfirmationToken(String confirmationToken) {
//		this.confirmationToken = confirmationToken;
//	}

	@Override
	public void setResetPasswordToken(String resetPasswordToken) {
		this.resetPasswordToken = resetPasswordToken;
	}

	@Override
	public void setPassword(String newPassword) {
		this.password = newPassword;
	}

	@Override
	public void setUsername(String username) {
		this.username = username;
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
	public void setId(String id) {
		this.id = id;
	}

	public void setRoles(List<? extends GrantedAuthority> roles) {
		this.roles = roles;
	}

	public void setMetadata(Map<String, ? extends Metadatum> metadata) {
		this.metadata = metadata;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getRedirectUrl() {
		return this.redirectUrl;
	}

	@Override
	public List<? extends GrantedAuthority> getRoles() {
		return this.roles;
	}

	@Override
	public Map<String, ? extends Metadatum> getMetadata() {
		return this.metadata;
	}

}
