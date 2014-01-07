/**
 * Copyright (c) 2007 - 2013 www.Abiss.gr
 *
 * This file is part of Calipso, a software platform by www.Abiss.gr.
 *
 * Calipso is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Calipso is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Calipso. If not, see http://www.gnu.org/licenses/agpl.html
 */
package gr.abiss.calipso.model;

import gr.abiss.calipso.model.entities.AbstractAuditableMetadataSubject;
import gr.abiss.calipso.model.serializers.SkipPropertySerializer;
import gr.abiss.calipso.model.metadata.UserMetadatum;
import gr.abiss.calipso.userDetails.integration.LocalUser;
import gr.abiss.calipso.utils.MD5Utils;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 */
@Entity
@Table(name = "user")
public class User extends AbstractAuditableMetadataSubject<UserMetadatum, User> implements LocalUser {

	private static final long serialVersionUID = -7942906897981646998L;

	@Column(name = "first_name", nullable = false)
	private String firstName;
	
	@Column(name = "last_name", nullable = false)
	private String lastName;
	
	@Column(name = "user_name", unique = true, nullable = false)
	private String userName;

	@JsonSerialize(using = SkipPropertySerializer.class)
	@Column(name = "user_password")
	private String userPassword;

	@JsonIgnore
	@Column(name = "confirmation_token")
	private String confirmationToken;

	@JsonIgnore
	@Column(name = "reset_password_token")
	private String resetPasswordToken;

	@Column(name = "password_changed")
	private Date lastPassWordChangeDate;
	
	@Column(name = "email", unique = true, nullable = false)
	private String email;

	@Column(name = "email_hash", nullable = false)
	private String emailHash;

	@Column(name = "avatar_url")
	private String avatarUrl;

	@Column(name = "birthday")
	private Date birthDay;

	@Column(name = "last_visit")
	private Date lastVisit;

	@Column(name = "last_post")
	private Date lastPost;

	@Column(name = "login_attempts")
	private Short loginAttempts = 0;

	@Column(name = "active")
	private Boolean active = false;

	@Column(name = "inactivation_reason")
	private String inactivationReason;

	@Column(name = "inactivation_date")
	private Date inactivationDate;

	@Column(name = "locale", nullable = false)
	private String locale = "en";

	@Column(name = "date_format")
	private String dateFormat;

	@Transient
	private String redirectUrl;

	public User() {
	}
	
	public User(String email) {
		this.email = email;
		// TODO: remove when @PrePersist etc. works
		// this.resetEmailHash();
	}

	@Override
	public Class<UserMetadatum> getMetadataDomainClass() {
		return UserMetadatum.class;
	}

	@Override
	public boolean equals(Object obj) {
		if (null == obj) {
			return false;
		}

		if (this == obj) {
			return true;
		}
		if (!(obj instanceof User)) {
			return false;
		}
		User that = (User) obj;
		return null == this.getId() ? false : this.getId().equals(that.getId());
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).appendSuper(super.toString()).append("userName", this.getUserName())
				.append("email", this.getEmail())
.append("new", this.isNew())
				.toString();
	}

	/**
	 * @see gr.abiss.calipso.userDetails.integration.LocalUser#getRedirectUrl()
	 */
	@Override
	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}


	/**
	 * Called by Hibernate <code>@PrePersist</code> and <code>@PreUpdate</code>
	 * to
	 * keep the email hash of the user up-to date
	 */
	@PrePersist
	@PreUpdate
	public void resetEmailHash() {
		if (this.getEmail() != null) {

			// make sure it's trimmed
			this.setEmail(this.getEmail().trim());
			// update the hash
			this.setEmailHash(MD5Utils.md5Hex(this.getEmail()));

		}

	}

	@Override
	public String getFirstName() {
		return firstName;
	}

	@Override
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Override
	public String getLastName() {
		return lastName;
	}

	@Override
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String getUserName() {
		return userName;
	}

	@Override
	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String getUserPassword() {
		return userPassword;
	}

	@Override
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getConfirmationToken() {
		return confirmationToken;
	}

	@Override
	public void setConfirmationToken(String confirmationToken) {
		this.confirmationToken = confirmationToken;
	}

	public String getResetPasswordToken() {
		return resetPasswordToken;
	}

	@Override
	public void setResetPasswordToken(String resetPasswordToken) {
		this.resetPasswordToken = resetPasswordToken;
	}

	public Date getLastPassWordChangeDate() {
		return lastPassWordChangeDate;
	}

	public void setLastPassWordChangeDate(Date lastPassWordChangeDate) {
		this.lastPassWordChangeDate = lastPassWordChangeDate;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
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

	public Date getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(Date birthDay) {
		this.birthDay = birthDay;
	}

	public Date getLastVisit() {
		return lastVisit;
	}

	public void setLastVisit(Date lastVisit) {
		this.lastVisit = lastVisit;
	}

	public Date getLastPost() {
		return lastPost;
	}

	public void setLastPost(Date lastPost) {
		this.lastPost = lastPost;
	}

	public Short getLoginAttempts() {
		return loginAttempts;
	}

	public void setLoginAttempts(Short loginAttempts) {
		this.loginAttempts = loginAttempts;
	}

	@Override
	public Boolean getActive() {
		return active;
	}

	@Override
	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getInactivationReason() {
		return inactivationReason;
	}

	public void setInactivationReason(String inactivationReason) {
		this.inactivationReason = inactivationReason;
	}

	public Date getInactivationDate() {
		return inactivationDate;
	}

	public void setInactivationDate(Date inactivationDate) {
		this.inactivationDate = inactivationDate;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	@Override
	public Collection<? extends GrantedAuthority> getRoles() {
		// TODO Auto-generated method stub
		return null;
	}


}