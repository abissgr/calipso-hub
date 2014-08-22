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

import gr.abiss.calipso.model.interfaces.Metadatum;
import gr.abiss.calipso.model.serializers.SkipPropertySerializer;
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
public class UserDetails implements  ICalipsoUserDetails{
	
	private static final long serialVersionUID = 5206010308112791343L;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(UserDetails.class);



	private String id;
	
	@JsonProperty(value = "username")
	private String username;
	
	private Long notificationCount;

	@JsonSerialize(using = SkipPropertySerializer.class)
	@JsonProperty(value = "password")
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

	public static ICalipsoUserDetails fromUser(LocalUser user) {
		UserDetails details = null;
		if (user != null) {
			details = new UserDetails();
			BeanUtils.copyProperties(user, details);
			if(user.getId() != null){
				details.setId(user.getId().toString());
			}
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
			.append("authorities", authorities)
			.toString();
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#setId(java.lang.String)
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#getFirstName()
	 */
	@Override
	public String getFirstName() {
		return firstName;
	}


	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#setNotificationCount()
	 */
	@Override
	public Long getNotificationCount() {
		return notificationCount;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#setNotificationCount(Long)
	 */
	@Override
	public void setNotificationCount(Long notificationCount) {
		this.notificationCount = notificationCount;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#setFirstName(java.lang.String)
	 */
	@Override
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#getLastName()
	 */
	@Override
	public String getLastName() {
		return lastName;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#setLastName(java.lang.String)
	 */
	@Override
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#getLastPassWordChangeDate()
	 */
	@Override
	public Date getLastPassWordChangeDate() {
		return lastPassWordChangeDate;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#setLastPassWordChangeDate(java.util.Date)
	 */
	@Override
	public void setLastPassWordChangeDate(Date lastPassWordChangeDate) {
		this.lastPassWordChangeDate = lastPassWordChangeDate;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#getEmail()
	 */
	@Override
	public String getEmail() {
		return email;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#setEmail(java.lang.String)
	 */
	@Override
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#getEmailHash()
	 */
	@Override
	public String getEmailHash() {
		return emailHash;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#setEmailHash(java.lang.String)
	 */
	@Override
	public void setEmailHash(String emailHash) {
		this.emailHash = emailHash;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#getAvatarUrl()
	 */
	@Override
	public String getAvatarUrl() {
		return avatarUrl;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#setAvatarUrl(java.lang.String)
	 */
	@Override
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#getBirthDay()
	 */
	@Override
	public Date getBirthDay() {
		return birthDay;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#setBirthDay(java.util.Date)
	 */
	@Override
	public void setBirthDay(Date birthDay) {
		this.birthDay = birthDay;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#getLastVisit()
	 */
	@Override
	public Date getLastVisit() {
		return lastVisit;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#setLastVisit(java.util.Date)
	 */
	@Override
	public void setLastVisit(Date lastVisit) {
		this.lastVisit = lastVisit;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#getLastPost()
	 */
	@Override
	public Date getLastPost() {
		return lastPost;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#setLastPost(java.util.Date)
	 */
	@Override
	public void setLastPost(Date lastPost) {
		this.lastPost = lastPost;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#getLoginAttempts()
	 */
	@Override
	public Short getLoginAttempts() {
		return loginAttempts;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#setLoginAttempts(java.lang.Short)
	 */
	@Override
	public void setLoginAttempts(Short loginAttempts) {
		this.loginAttempts = loginAttempts;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#getActive()
	 */
	@Override
	public Boolean getActive() {
		return active;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#setActive(java.lang.Boolean)
	 */
	@Override
	public void setActive(Boolean active) {
		this.active = active;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#getInactivationReason()
	 */
	@Override
	public String getInactivationReason() {
		return inactivationReason;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#setInactivationReason(java.lang.String)
	 */
	@Override
	public void setInactivationReason(String inactivationReason) {
		this.inactivationReason = inactivationReason;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#getInactivationDate()
	 */
	@Override
	public Date getInactivationDate() {
		return inactivationDate;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#setInactivationDate(java.util.Date)
	 */
	@Override
	public void setInactivationDate(Date inactivationDate) {
		this.inactivationDate = inactivationDate;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#getLocale()
	 */
	@Override
	public String getLocale() {
		return locale;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#setLocale(java.lang.String)
	 */
	@Override
	public void setLocale(String locale) {
		this.locale = locale;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#getDateFormat()
	 */
	@Override
	public String getDateFormat() {
		return dateFormat;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#setDateFormat(java.lang.String)
	 */
	@Override
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#isAdmin()
	 */
	@Override
	public boolean isAdmin() {
		return isAdmin;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#setAdmin(boolean)
	 */
	@Override
	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#isModerator()
	 */
	@Override
	public boolean isModerator() {
		return isModerator;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#setModerator(boolean)
	 */
	@Override
	public void setModerator(boolean isModerator) {
		this.isModerator = isModerator;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#setRedirectUrl(java.lang.String)
	 */
	@Override
	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#getRedirectUrl()
	 */
	@Override
	public String getRedirectUrl() {
		return this.redirectUrl;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#setConfirmationToken(java.lang.String)
	 */
	@Override
	public void setConfirmationToken(String confirmationToken) {
		this.confirmationToken = confirmationToken;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#setResetPasswordToken(java.lang.String)
	 */
	@Override
	public void setResetPasswordToken(String resetPasswordToken) {
		this.resetPasswordToken = resetPasswordToken;

	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#getMetadata()
	 */
	@Override
	public Map<String, String> getMetadata() {
		return this.metadata;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#setMetadata(java.util.Map)
	 */
	@Override
	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#addMetadatum(java.lang.String, java.lang.String)
	 */
	@Override
	public void addMetadatum(String predicate, String object) {
		if (this.metadata == null) {
			this.metadata = new HashMap<String, String>();
		}
		LOGGER.info("addMetadatum predicate: " + predicate + ", object: "
				+ object);
		this.metadata.put(predicate, object);
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#getConfirmationToken()
	 */
	@Override
	public String getConfirmationToken() {
		return confirmationToken;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#getResetPasswordToken()
	 */
	@Override
	public String getResetPasswordToken() {
		return resetPasswordToken;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#setUsername(java.lang.String)
	 */
	@Override
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#setPassword(java.lang.String)
	 */
	@Override
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#setAuthorities(java.util.Collection)
	 */
	@Override
	public void setAuthorities(
			Collection<? extends GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

	// SocialUserDetails

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#getAuthorities()
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#getPassword()
	 */
	@Override
	public String getPassword() {
		return this.password;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#getUsername()
	 */
	@Override
	public String getUsername() {
		return this.username;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#isAccountNonExpired()
	 */
	@Override
	public boolean isAccountNonExpired() {
		return this.active;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#isAccountNonLocked()
	 */
	@Override
	public boolean isAccountNonLocked() {
		return this.active;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#isCredentialsNonExpired()
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		return this.active;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return this.active;
	}

	/**
	 * @see gr.abiss.calipso.userDetails.model.ICalipsoUserDetails#getUserId()
	 */
	@Override
	public String getUserId() {
		return this.id;
	}
}
