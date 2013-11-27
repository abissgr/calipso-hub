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
package gr.abiss.calipso.userDetails.model;

import gr.abiss.calipso.ddd.core.model.interfaces.Metadatum;
import gr.abiss.calipso.ddd.core.model.serializers.SkipPropertySerializer;
import gr.abiss.calipso.userDetails.integration.LocalUser;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


@XmlRootElement(name = "loggedInUserDetails")
public class UserDetails implements SocialUserDetails/*
													 * , LocalUser ,
													 * MetadataSubject
													 * <Metadatum>
													 */{
	
	private static final long serialVersionUID = 5206010308112791343L;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(UserDetails.class);



	private String id;
	@JsonProperty(value = "userName")
	private String username;

	@JsonSerialize(using = SkipPropertySerializer.class)
	@JsonProperty(value = "userPassword")
	private String password;
	private Date lastPassWordChangeDate;

	private String email;
	private String emailHash;

	private String firstName;
	private String lastName;

	private String avatarUrl;
	private String locale = "en";
	private String dateFormat;

	private Date birthDay;
	private Date lastVisit;
	private Date lastPost;
	private Short loginAttempts = 0;

	private String redirectUrl = null;

	@JsonSerialize(using = SkipPropertySerializer.class)
	private Boolean active = true;

	@JsonSerialize(using = SkipPropertySerializer.class)
	private String inactivationReason;

	private Date inactivationDate;
	private boolean isAdmin = false;
	private boolean isModerator = false;

	@JsonProperty(value = "roles")
	private Collection<? extends GrantedAuthority> authorities;
	private Map<String, String> metadata;
	
	@JsonSerialize(using = SkipPropertySerializer.class)
	private String confirmationToken;
	@JsonSerialize(using = SkipPropertySerializer.class)
	private String resetPasswordToken;

	public static UserDetails fromUser(LocalUser user) {
		UserDetails details = null;
		if (user != null) {
			details = new UserDetails();
			BeanUtils.copyProperties(user, details);
			details.setId(user.getId().toString());
			details.setUsername(user.getUserName());
			details.setPassword(user.getUserPassword());
			details.setEmail(user.getEmail());
			details.setFirstName(user.getFirstName());
			details.setLastName(user.getLastName());
			details.setActive(user.getActive());
			details.setAuthorities(user.getRoles());

			// init metadata
			if (!CollectionUtils.isEmpty(user.getMetadata())) {
				for (Metadatum metadatum : user.getMetadata().values()) {
					details.addMetadatum(metadatum.getPredicate(),
							metadatum.getObject());
				}
			}
			// init global roles
			if (!CollectionUtils.isEmpty(user.getRoles())) {
				for (GrantedAuthority authority : user.getRoles()) {
					if (authority.getAuthority().equals("ROLE_ADMIN")) {
						details.isAdmin = true;
					} else if (authority.getAuthority()
							.equals("ROLE_MODERATOR")) {
						details.isModerator = true;
					}
				}
			}
		}
		return details;
	}

	/**
	 * Default constructor
	 */
	public UserDetails() {

	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("id", id)
			.append("username", username)
			.append("email", email)
			.append("metadata", metadata)
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

	public Date getLastPassWordChangeDate() {
		return lastPassWordChangeDate;
	}

	public void setLastPassWordChangeDate(Date lastPassWordChangeDate) {
		this.lastPassWordChangeDate = lastPassWordChangeDate;
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

	public Boolean getActive() {
		return active;
	}

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

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public boolean isModerator() {
		return isModerator;
	}

	public void setModerator(boolean isModerator) {
		this.isModerator = isModerator;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public String getRedirectUrl() {
		return this.redirectUrl;
	}

	public void setConfirmationToken(String confirmationToken) {
		this.confirmationToken = confirmationToken;
	}

	public void setResetPasswordToken(String resetPasswordToken) {
		this.resetPasswordToken = resetPasswordToken;

	}

	public Map<String, String> getMetadata() {
		return this.metadata;
	}

	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}

	public void addMetadatum(String predicate, String object) {
		if (this.metadata == null) {
			this.metadata = new HashMap<String, String>();
		}
		LOGGER.info("addMetadatum predicate: " + predicate + ", object: "
				+ object);
		this.metadata.put(predicate, object);
	}

	public String getConfirmationToken() {
		return confirmationToken;
	}

	public String getResetPasswordToken() {
		return resetPasswordToken;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setAuthorities(
			Collection<? extends GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

	// SocialUserDetails

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return this.active;
	}

	@Override
	public boolean isAccountNonLocked() {
		return this.active;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return this.active;
	}

	@Override
	public boolean isEnabled() {
		return this.active;
	}

	@Override
	public String getUserId() {
		return this.id;
	}
}
