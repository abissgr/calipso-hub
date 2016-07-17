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
package gr.abiss.calipso.model;

import java.io.Serializable;

import javax.persistence.Column;

import org.apache.commons.lang3.builder.ToStringBuilder;


public class UserDTO implements Serializable {
	
	private String id;
	
	private String firstName;
	
	private String lastName;
	
	private String username;
	
	@Column(name = "email", unique = true, nullable = false)
	private String email;

	@Column(name = "email_hash", nullable = false)
	private String emailHash;


	public UserDTO() {
	}

	
	public UserDTO(String id, String firstName, String lastName,
			String username, String email, String emailHash) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
		this.email = email;
		this.emailHash = emailHash;
	}


	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.appendSuper(super.toString())
			.append("id", this.getUsername())
			.append("firstName", this.getUsername())
			.append("lastName", this.getUsername())
			.append("username", this.getUsername())
			.append("email", this.getUsername())
			.append("emailHash", this.getEmail())
			.toString();
	}
	
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getLastName() {
		return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getEmailHash() {
		return emailHash;
	}


	public void setEmailHash(String emailHash) {
		this.emailHash = emailHash;
	}
	
	

}