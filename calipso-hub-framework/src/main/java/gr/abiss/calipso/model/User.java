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

import gr.abiss.calipso.jpasearch.annotation.FormSchemaEntry;
import gr.abiss.calipso.jpasearch.annotation.FormSchemas;
import gr.abiss.calipso.model.contactDetails.LocalRegionMailingAddress;
import gr.abiss.calipso.model.entities.AbstractAuditableMetadataSubject;
import gr.abiss.calipso.model.interfaces.ReportDataSetSubject;
import gr.abiss.calipso.model.metadata.UserMetadatum;
import gr.abiss.calipso.model.serializers.SkipPropertySerializer;
import gr.abiss.calipso.userDetails.integration.LocalUser;
import gr.abiss.calipso.utils.MD5Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Formula;
import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
//import com.wordnik.swagger.annotations.ApiModel;

/**
 */
@Entity
//@ApiModel(description = "Human users")
@Table(name = "user")
@Inheritance(strategy = InheritanceType.JOINED)
public class User extends AbstractAuditableMetadataSubject<UserMetadatum, User> implements LocalUser, ReportDataSetSubject {

	private static final long serialVersionUID = -7942906897981646998L;

	@Formula("concat(first_name, ' ', last_name, ' (', user_name, ')' )")
	private String searchName;
	
	@Formula("concat(first_name, ' ', last_name )")
	private String name;
	
	@Column(name = "first_name", nullable = false)
	private String firstName;
	
	@Column(name = "last_name", nullable = false)
	private String lastName;
	
	@Column(name = "user_name", unique = true, nullable = false)
	private String username;

	@JsonSerialize(using = SkipPropertySerializer.class)
	@Column(name = "user_password")
	private String password;

	@JsonIgnore
	@Column(name = "confirmation_token")
	private String confirmationToken;

	@JsonIgnore
	@Column(name = "reset_password_token")
	private String resetPasswordToken;

	@Column(name = "password_changed")
	@FormSchemas({
			@FormSchemaEntry(json = FormSchemaEntry.TYPE_DATE)
	})
	private Date lastPassWordChangeDate;
	
	@Column(name = "email", unique = true, nullable = false)
	private String email;

	@Column(name = "email_hash", nullable = false)
	private String emailHash;

	@Column(name = "avatar_url")
	private String avatarUrl;

	@Column(nullable = true)
	private String telephone;
	
	@Column(nullable = true)
	private String cellphone;

	@Column(name = "birthday")
	@FormSchemas({
		@FormSchemaEntry(json = FormSchemaEntry.TYPE_DATE)
	})
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

//	@OneToOne(optional = true, fetch=FetchType.LAZY)
//	@MapsId
//	private LocalRegionMailingAddress mailingAddress;
	
	//@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_roles", joinColumns = { @JoinColumn(name = "role_id") }, inverseJoinColumns = { @JoinColumn(name = "user_id") })
	private List<Role> roles = new ArrayList<Role>(0);

	public User() {
	}
	
	public User(String email) {
		this.email = email;
	}

	/**
	 * {@inheritDoc}}
	 * @see gr.abiss.calipso.model.interfaces.ReportDataSetSubject#getLabel()
	 */
	@Override
	public String getLabel(){
		return this.getName();
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
		User other = (User) obj;
		EqualsBuilder builder = new EqualsBuilder();
        builder.append(getId(), other.getId());
        builder.append(getName(), other.getName());
        return builder.isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.appendSuper(super.toString())
			.append("username", this.getUsername())
			.append("email", this.getEmail())
			.append("password", this.getPassword())
			.append("new", this.isNew())
			.append("roles", this.getRoles())
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
	
	

	public String getSearchName() {
		return searchName;
	}

	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

	/**
	 * Add a role to this principal.
	 * @param role the role to add
	 */
	public void addRole(Role role) {
		if (this.roles == null) {
			this.roles = new LinkedList<Role>();
		}
		this.roles.add(role);
	}

	/**
	 * Remove a role from this principal.
	 * @param role the role to remove
	 */
	public void removeRole(Role role) {
		if (!CollectionUtils.isEmpty(roles)) {
			this.roles.remove(role);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
	public String getUsername() {
		return username;
	}

	@Override
	public void setUsername(String userName) {
		this.username = userName;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
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

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getCellphone() {
		return cellphone;
	}

	public void setCellphone(String cellphone) {
		this.cellphone = cellphone;
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
	public List<? extends GrantedAuthority> getRoles() {
		return this.roles;
	}

//	public LocalRegionMailingAddress getMailingAddress() {
//		return mailingAddress;
//	}
//
//	public void setMailingAddress(LocalRegionMailingAddress mailingAddress) {
//		this.mailingAddress = mailingAddress;
//	}


}