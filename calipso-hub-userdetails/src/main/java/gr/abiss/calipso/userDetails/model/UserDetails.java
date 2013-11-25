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

import gr.abiss.calipso.ddd.core.model.interfaces.MetadataSubject;
import gr.abiss.calipso.ddd.core.model.interfaces.Metadatum;
import gr.abiss.calipso.ddd.core.model.serializers.SkipPropertySerializer;
import gr.abiss.calipso.userDetails.integration.LocalUser;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;


@XmlRootElement(name = "loggedInUserDetails")
public class UserDetails implements SocialUserDetails, LocalUser,
		MetadataSubject<Metadatum> {
	
	private static final long serialVersionUID = 5206010308112791343L;

	// TODO: move to JS client Model
	public static void initRoles(UserDetails userDetails, Collection<? extends GrantedAuthority> authorities) {
		if (!CollectionUtils.isEmpty(authorities)) {
			for (GrantedAuthority authority : authorities) {
				if (authority.getAuthority().equals("ROLE_ADMIN")) {
					userDetails.isAdmin = true;
				} else if (authority.getAuthority().equals("ROLE_MODERATOR")) {
					userDetails.isModerator = true;
				}
			}
		}
	}
	private String id;
	private String firstName;
	private String lastName;
	private String userName;
	private String redirectUrl = null;

	@JsonSerialize(using = SkipPropertySerializer.class)
	private String userPassword;
	private Date lastPassWordChangeDate;
	private String email;
	private String emailHash;
	private String avatarUrl;
	private Date birthDay;
	private Date lastVisit;
	private Date lastPost;
	private Short loginAttempts = 0;
	private Boolean active = true;
	private String inactivationReason;
	private Date inactivationDate;
	private String locale = "en";
	private String dateFormat;
	private boolean isAdmin = false;
	private boolean isModerator = false;
	private Collection<? extends GrantedAuthority> authorities;
	private String confirmationToken;
	private String resetPasswordToken;
	private Map<String, Metadatum> metadata;

	public static UserDetails fromUser(LocalUser user) {
		Assert.notNull(user);
		UserDetails details = new UserDetails(user.getId(), user.getUserName(),
				user.getUserPassword(), user.getEmail(), user.getActive(),
				user.getRoles());
		// details.setMetadata(user.getMetadata());
		return details;
	}

	public UserDetails(Serializable id, String userName, String userPassword, String email, Boolean active,
			Collection<? extends GrantedAuthority> roles) {
		Assert.notNull(id);
		Assert.notNull(userName);
		Assert.notNull(userPassword);
		Assert.notNull(email);
		Assert.notNull(active);
		Assert.notNull(roles);

		this.id = id.toString();
		this.userName = userName;
		this.userPassword = userPassword;
		this.email = email;
		this.active = active;
		this.authorities = roles;
		UserDetails.initRoles(this, roles);
	}

	public UserDetails() {
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", this.id).append("userName", userName).append("userPassword", userPassword)
				.append("email", email).toString();
	}

	@Override
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	@Override
	public String getRedirectUrl() {
		return this.redirectUrl;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public String getPassword() {
		return this.getUserPassword();
	}

	@Override
	public String getUsername() {
		return this.getUserName();
	}

	@Override
	public boolean isAccountNonExpired() {
		return this.getActive();
	}

	@Override
	public boolean isAccountNonLocked() {
		return this.getActive();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return this.getActive();
	}

	@Override
	public boolean isEnabled() {
		return this.getActive();
	}

	@Override
	public String getUserId() {
		return this.getId();
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
	public Collection<? extends GrantedAuthority> getRoles() {
		return this.getAuthorities();
	}

	@Override
	public Map<String, Metadatum> getMetadata() {
		// TODO Auto-generated method stub
		return this.metadata;
	}

	@Override
	public void setMetadata(Map<String, Metadatum> metadata) {
		this.metadata = metadata;
	}

	@Override
	public Metadatum addMetadatum(Metadatum metadatum) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Metadatum addMetadatum(String predicate, String object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class getMetadataDomainClass() {
		// TODO Auto-generated method stub
		return null;
	}

}
