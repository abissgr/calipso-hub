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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.javers.core.metamodel.annotation.ShallowReference;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import gr.abiss.calipso.model.base.AbstractSystemUuidPersistable;
import gr.abiss.calipso.model.interfaces.CalipsoPersistable;
import gr.abiss.calipso.model.serializers.SkipPropertySerializer;
import gr.abiss.calipso.tiers.annotation.ModelResource;
import gr.abiss.calipso.tiers.controller.AbstractModelController;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ShallowReference
@Entity
@ApiModel(description = "UserCredentials")
@ModelResource(path = "UserCredentials", controllerSuperClass = AbstractModelController.class, apiName = "UserCredentials", apiDescription = "Operations about UserCredentials")
@Table(name = "user_credentials")
@Inheritance(strategy = InheritanceType.JOINED)
public class UserCredentials extends AbstractSystemUuidPersistable implements CalipsoPersistable<String> {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(hidden = true)
	@JsonSerialize(using = SkipPropertySerializer.class)
	@Column(name = "user_password")
	private String password;

	@JsonIgnore
	@Column(name = "reset_password_token")
	private String resetPasswordToken;

	@JsonIgnore
	@Column(name = "reset_password_token_date")
	private Date resetPasswordTokenCreated;

	@Column(name = "password_changed")
	private Date lastPassWordChangeDate;

	@Column(name = "last_login")
	private Date lastLogin;

	@Column(name = "login_attempts")
	private Short loginAttempts = 0;

	@OneToOne(fetch = FetchType.LAZY)
	private User user;

	public UserCredentials() {
	}

	public UserCredentials(String password, String resetPasswordToken, Date resetPasswordTokenCreated,
			Date lastPassWordChangeDate, Date lastLogin, Short loginAttempts) {
		super();
		this.password = password;
		this.resetPasswordToken = resetPasswordToken;
		this.resetPasswordTokenCreated = resetPasswordTokenCreated;
		this.lastPassWordChangeDate = lastPassWordChangeDate;
		this.lastLogin = lastLogin;
		this.loginAttempts = loginAttempts;
	}

	@PreUpdate
	@PrePersist
	public void onBeforeSave() {
		// clear or set the token creation date  if needed
		if (this.getResetPasswordToken() == null) {
			this.setResetPasswordTokenCreated(null);
		} else if (this.getResetPasswordTokenCreated() == null) {
			this.setResetPasswordTokenCreated(new Date());
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UserCredentials [password=" + password + ", resetPasswordToken=" + resetPasswordToken
				+ ", resetPasswordTokenCreated=" + resetPasswordTokenCreated + ", lastPassWordChangeDate="
				+ lastPassWordChangeDate + ", lastLogin=" + lastLogin + ", loginAttempts=" + loginAttempts + "]";
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getResetPasswordToken() {
		return resetPasswordToken;
	}

	public void setResetPasswordToken(String resetPasswordToken) {
		this.resetPasswordToken = resetPasswordToken;
	}

	public Date getResetPasswordTokenCreated() {
		return resetPasswordTokenCreated;
	}

	public void setResetPasswordTokenCreated(Date resetPasswordTokenCreated) {
		this.resetPasswordTokenCreated = resetPasswordTokenCreated;
	}

	public Date getLastPassWordChangeDate() {
		return lastPassWordChangeDate;
	}

	public void setLastPassWordChangeDate(Date lastPassWordChangeDate) {
		this.lastPassWordChangeDate = lastPassWordChangeDate;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Short getLoginAttempts() {
		return loginAttempts;
	}

	public void setLoginAttempts(Short loginAttempts) {
		this.loginAttempts = loginAttempts;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	public static class Builder {
		private String password;
		private String resetPasswordToken;
		private Date resetPasswordTokenCreated;
		private Date lastPassWordChangeDate;
		private Date lastLogin;
		private Short loginAttempts;
		private User user;

		public Builder password(String password) {
			this.password = password;
			return this;
		}

		public Builder resetPasswordToken(String resetPasswordToken) {
			this.resetPasswordToken = resetPasswordToken;
			return this;
		}

		public Builder resetPasswordTokenCreated(Date resetPasswordTokenCreated) {
			this.resetPasswordTokenCreated = resetPasswordTokenCreated;
			return this;
		}

		public Builder lastPassWordChangeDate(Date lastPassWordChangeDate) {
			this.lastPassWordChangeDate = lastPassWordChangeDate;
			return this;
		}

		public Builder lastLogin(Date lastLogin) {
			this.lastLogin = lastLogin;
			return this;
		}

		public Builder loginAttempts(Short loginAttempts) {
			this.loginAttempts = loginAttempts;
			return this;
		}

		public Builder user(User user) {
			this.user = user;
			return this;
		}

		public UserCredentials build() {
			return new UserCredentials(this);
		}
	}

	private UserCredentials(Builder builder) {
		this.password = builder.password;
		this.resetPasswordToken = builder.resetPasswordToken;
		this.resetPasswordTokenCreated = builder.resetPasswordTokenCreated;
		this.lastPassWordChangeDate = builder.lastPassWordChangeDate;
		this.lastLogin = builder.lastLogin;
		this.loginAttempts = builder.loginAttempts;
		this.user = builder.user;
	}
}