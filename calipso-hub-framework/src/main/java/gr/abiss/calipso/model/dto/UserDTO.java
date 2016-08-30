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
package gr.abiss.calipso.model.dto;

import java.io.Serializable;

import javax.persistence.Column;

import org.apache.commons.lang3.builder.ToStringBuilder;

import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.User.Builder;
import io.swagger.annotations.ApiModel;

@ApiModel(value = "UserDTO", description = "UserDTO is a lightweight DTO version of User")
public class UserDTO implements Serializable {

	public static UserDTO fromUser(User user){
		return new UserDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail(), user.getEmailHash(), user.getAvatarUrl());
	}
	
	private String id;

	private String firstName;

	private String lastName;

	private String username;

	private String email;

	private String emailHash;
	
	private String avatarUrl;

	public UserDTO() {
	}

	public UserDTO(String id, String firstName, String lastName, String username, String email, String emailHash, String avatarUrl) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
		this.email = email;
		this.emailHash = emailHash;
		this.avatarUrl = avatarUrl;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).appendSuper(super.toString()).append("id", this.getUsername())
				.append("firstName", this.getUsername()).append("lastName", this.getUsername())
				.append("username", this.getUsername()).append("email", this.getUsername())
				.append("emailHash", this.getEmail()).append("avatarUrl", this.getAvatarUrl()).toString();
	}

	public User toUser() {
		return new User.Builder().id(this.id).firstName(this.firstName).lastName(this.lastName).username(this.username)
				.email(this.email).emailHash(this.emailHash).avatarUrl(this.avatarUrl).build();
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

	
	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}


	public static class Builder {
		private String id;
		private String firstName;
		private String lastName;
		private String username;
		private String email;
		private String emailHash;
		private String avatarUrl;

		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder firstName(String firstName) {
			this.firstName = firstName;
			return this;
		}

		public Builder lastName(String lastName) {
			this.lastName = lastName;
			return this;
		}

		public Builder username(String username) {
			this.username = username;
			return this;
		}

		public Builder email(String email) {
			this.email = email;
			return this;
		}

		public Builder emailHash(String emailHash) {
			this.emailHash = emailHash;
			return this;
		}
		
		public Builder avatarUrl(String avatarUrl) {
			this.avatarUrl = avatarUrl;
			return this;
		}
		
		public UserDTO build() {
			return new UserDTO(this);
		}
	}

	private UserDTO(Builder builder) {
		this.id = builder.id;
		this.firstName = builder.firstName;
		this.lastName = builder.lastName;
		this.username = builder.username;
		this.email = builder.email;
		this.emailHash = builder.emailHash;
		this.avatarUrl = builder.avatarUrl;
	}
}