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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Formula;
import org.javers.core.metamodel.annotation.ShallowReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import gr.abiss.calipso.friends.model.Friendship;
import gr.abiss.calipso.fs.FilePersistence;
import gr.abiss.calipso.fs.FilePersistencePreview;
import gr.abiss.calipso.model.base.AbstractSystemUuidPersistable;
import gr.abiss.calipso.model.dto.UserDTO;
import gr.abiss.calipso.model.entities.AbstractMetadataSubject;
import gr.abiss.calipso.model.geography.Country;
import gr.abiss.calipso.model.interfaces.CalipsoPersistable;
import gr.abiss.calipso.model.interfaces.MetadataSubject;
import gr.abiss.calipso.model.metadata.UserMetadatum;
import gr.abiss.calipso.model.serializers.SkipPropertySerializer;
import gr.abiss.calipso.uischema.annotation.FormSchemaEntry;
import gr.abiss.calipso.uischema.annotation.FormSchemas;
import gr.abiss.calipso.userDetails.integration.LocalUser;
import gr.abiss.calipso.utils.Constants;
import gr.abiss.calipso.utils.MD5Utils;
import gr.abiss.calipso.websocket.message.IMessageResource;
import gr.abiss.calipso.websocket.model.StompSession;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 */

@ShallowReference
@Entity
@ApiModel(description = "Human users")
@Table(name = "user")
@Inheritance(strategy = InheritanceType.JOINED)
public class User extends AbstractMetadataSubject<UserMetadatum> implements LocalUser, CalipsoPersistable<String> {

	private static final Logger LOGGER = LoggerFactory.getLogger(User.class);
	private static final long serialVersionUID = -7942906897981646998L;
	public static final String PRE_AUTHORIZE_SEARCH = "isAuthenticated()";
	public static final String PRE_AUTHORIZE_CREATE = "permitAll";
	public static final String PRE_AUTHORIZE_UPDATE = "isAuthenticated()";
	public static final String PRE_AUTHORIZE_PATCH = "isAuthenticated()";
	public static final String PRE_AUTHORIZE_VIEW = "isAuthenticated()";
	public static final String PRE_AUTHORIZE_DELETE = "isAuthenticated()";
	

	public static final String PRE_AUTHORIZE_DELETE_BY_ID = "denyAll";
	public static final String PRE_AUTHORIZE_DELETE_ALL = "denyAll";
	public static final String PRE_AUTHORIZE_DELETE_WITH_CASCADE = "denyAll";
	public static final String PRE_AUTHORIZE_FIND_BY_IDS = "denyAll";
	public static final String PRE_AUTHORIZE_FIND_ALL = "denyAll";
	public static final String PRE_AUTHORIZE_COUNT = "denyAll";

	@ApiModelProperty(hidden = true)
	@Formula("concat(first_name, ' ', last_name, ' (', user_name, ')' )")
	private String searchName;

	@Formula("concat(first_name, ' ', last_name )")
	private String name;

	@Column(name = "first_name", nullable = true)
	private String firstName;

	@Column(name = "last_name", nullable = true)
	private String lastName;

	@Column(name = "user_name", unique = true, nullable = false)
	private String username;

	@ApiModelProperty(hidden = true)
	@JsonSerialize(using = SkipPropertySerializer.class)
	@Column(name = "user_password")
	private String password;

	@Transient
	List<String> changedAttributes = null;

	@Transient
	@JsonIgnore
	Locale localeObject = null;

	@JsonIgnore
	@Column(name = "reset_password_token")
	private String resetPasswordToken;

	@JsonIgnore
	@Column(name = "reset_password_token_date")
	private Date resetPasswordTokenCreated;

	@Column(name = "password_changed")
	private Date lastPassWordChangeDate;

	@Column(name = "email", unique = true, nullable = false)
	private String email;

	@Column(name = "email_hash", nullable = false)
	private String emailHash;

	@FilePersistence(maxWidth = 130, maxHeight = 130)
	@FilePersistencePreview(maxWidth = 100, maxHeight = 100)
	@FilePersistencePreview(maxWidth = 50, maxHeight = 50)
	@Column(name = "avatar_url")
	private String avatarUrl;

	@FilePersistence(maxWidth = 1920, maxHeight = 1080)
	@FilePersistencePreview(maxWidth = 1280, maxHeight = 720)
	@FilePersistencePreview(maxWidth = 720, maxHeight = 480)
	@Column(name = "banner_url")
	private String bannerUrl;

	@Column(nullable = true)
	private String telephone;

	@Column(nullable = true)
	private String cellphone;

	@Column(nullable = true)
	private String address;

	@Column(nullable = true)
	private String postCode;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "country_id", referencedColumnName = "id", nullable = true)
	private Country country;

	@Column(name = "birthday")
	@FormSchemas({ @FormSchemaEntry(json = FormSchemaEntry.TYPE_DATE) })
	private Date birthDay;

	@Column(name = "last_visit")
	private Date lastVisit;

	@Column(name = "last_login")
	private Date lastLogin;

	@Column(name = "login_attempts")
	private Short loginAttempts = 0;

	@Column(name = "active")
	private Boolean active = true;

	@Column(name = "inactivation_reason")
	private String inactivationReason;

	@Column(name = "inactivation_date")
	private Date inactivationDate;

	@Column(name = "locale", nullable = false)
	private String locale = "en";

	@Transient
	private String redirectUrl;

	@Formula(" (select count(*) from stomp_session s where s.user = id) ")
	private Integer stompSessionCount;

	
	//	@OneToOne(optional = true, fetch=FetchType.LAZY)
	//	@MapsId
	//	private LocalRegionMailingAddress mailingAddress;

	//@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_roles", joinColumns = { @JoinColumn(name = "role_id") }, inverseJoinColumns = {
			@JoinColumn(name = "user_id") })
	private List<Role> roles = new ArrayList<Role>(0);
	
	@JsonIgnore
	@OneToMany(mappedBy = "requestSender")
	private List<Friendship> friendships;
	
	@JsonIgnore
	@OneToMany(mappedBy = "user")
	private List<StompSession> stompSessions;

	public User() {
	}

	public User(UserDTO dto) {
		this.setId(dto.getId());
		this.setFirstName(dto.getFirstName());
		this.setLastName(dto.getLastName());
		this.username = dto.getUsername();
		this.email = dto.getEmail();
		this.emailHash = dto.getEmailHash();
	}

	public User(String id) {
		this.setId(id);
	}

	public Locale getLocaleObject() {
		if (this.localeObject == null) {
			this.localeObject = new Locale(this.getLocale() != null ? this.getLocale() : "en");
		}
		return localeObject;
	}

	public boolean hasRole(String roleName) {
		Assert.notNull(roleName, "Role name cannot be null");
		boolean hasIt = false;
		if (CollectionUtils.isNotEmpty(this.roles)) {
			for (Role role : roles) {
				if (roleName.equalsIgnoreCase(role.getName())) {
					hasIt = true;
					break;
				}
			}
		}
		return hasIt;
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
		return new ToStringBuilder(this).appendSuper(super.toString())
				.append("username", this.getUsername())
				.append("firstName", this.getFirstName())
				.append("lastName", this.getLastName())
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

	public Integer getStompSessionCount() {
		return stompSessionCount;
	}

	public void setStompSessionCount(Integer stompSessionCount) {
		this.stompSessionCount = stompSessionCount;
	}

	/**
	 * Called by Hibernate  <code>@PreUpdate</code>
	 * to
	 * keep the email hash of the user up-to date
	 */
	@PreUpdate
	public void onBeforeUpdate() {
		this.onBeforeSave();
	}

	/**
	 * Called by Hibernate <code>@PrePersist</code> >
	 * to
	 * keep the email hash of the user up-to date
	 */
	@PrePersist
	public void onBeforeCreate() {
		this.onBeforeSave();
	}
	
	protected void onBeforeSave() {
		if(!StringUtils.isNotBlank(this.getUsername())){
			this.setUsername(this.getEmail());
		}
		if(!StringUtils.isNotBlank(this.getLocale())){
			this.setLocale("en");
		}
		// create the email hash, 
		// use email as username if latter is empty
		if (this.getEmail() != null) {

			// make sure it's trimmed
			this.setEmail(this.getEmail().trim());
			// update the hash
			this.setEmailHash(MD5Utils.md5Hex(this.getEmail()));

			// if usernames are not exposed the email has been used to set its value
			// in that case it must follow the email value change
			if (this.getUsername() == null || this.getUsername().contains("@")) {
				this.setUsername(this.getEmail());
			}
		}

		// clear or set the token creation date  if needed
		if (this.getResetPasswordToken() == null) {
			this.setResetPasswordTokenCreated(null);
		} else if (this.getResetPasswordTokenCreated() == null) {
			this.setResetPasswordTokenCreated(new Date());
		}
		
		// fallback to gravatar 
		if(StringUtils.isBlank(this.getAvatarUrl())){
			try {

				this.setAvatarUrl(new StringBuffer(Constants.GRAVATAR_BASE_IMG_URL)
						.append(this.getEmailHash())
						.append("?d=")
						.append(URLEncoder.encode(Constants.DEFAULT_AVATAR_URL, CharEncoding.UTF_8))
						.toString());
			} catch (UnsupportedEncodingException e) {
				LOGGER.error("Failed encoding avatar url");
			}
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

	@JsonGetter("fullName")
	@Override
	public String getFullName() {
		StringBuffer s = new StringBuffer("");
		if (StringUtils.isNotBlank(this.getFirstName())) {
			s.append(this.getFirstName());
			if (StringUtils.isNotBlank(this.getLastName())) {
				s.append(' ');
			}
		}
		if (StringUtils.isNotBlank(this.getLastName())) {
			s.append(this.getLastName());
		}
		return s.toString();

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

	public String getResetPasswordToken() {
		return resetPasswordToken;
	}

	@Override
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

	public String getBannerUrl() {
		return bannerUrl;
	}

	public void setBannerUrl(String bannerUrl) {
		this.bannerUrl = bannerUrl;
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

	@Override
	public List<? extends GrantedAuthority> getRoles() {
		return this.roles;
	}

	public List<String> getChangedAttributes() {
		return changedAttributes;
	}

	public void setChangedAttributes(List<String> changedAttributes) {
		this.changedAttributes = changedAttributes;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public List<Friendship> getFriendships() {
		return friendships;
	}

	public void setFriendships(List<Friendship> friendships) {
		this.friendships = friendships;
	}

	public List<StompSession> getStompSessions() {
		return stompSessions;
	}

	public void setStompSessions(List<StompSession> stompSessions) {
		this.stompSessions = stompSessions;
	}

	public static class Builder {
		private String id;
		private String name;
		private String firstName;
		private String lastName;
		private String username;
		private String password;
		private List<String> changedAttributes;
		private Locale localeObject;
		private String resetPasswordToken;
		private Date resetPasswordTokenCreated;
		private Date lastPassWordChangeDate;
		private String email;
		private String emailHash;
		private String avatarUrl;
		private String bannerUrl;
		private String telephone;
		private String cellphone;
		private String address;
		private String postCode;
		private Country country;
		private Date birthDay;
		private Date lastVisit;
		private Date lastLogin;
		private Short loginAttempts;
		private Boolean active;
		private String inactivationReason;
		private Date inactivationDate;
		private String locale;
		private String redirectUrl;
		private List<Role> roles;
		private List<Friendship> friendships;

		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
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

		public Builder password(String password) {
			this.password = password;
			return this;
		}

		public Builder changedAttributes(List<String> changedAttributes) {
			this.changedAttributes = changedAttributes;
			return this;
		}

		public Builder localeObject(Locale localeObject) {
			this.localeObject = localeObject;
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
		
		public Builder bannerUrl(String bannerUrl) {
			this.bannerUrl = bannerUrl;
			return this;
		}

		public Builder telephone(String telephone) {
			this.telephone = telephone;
			return this;
		}

		public Builder cellphone(String cellphone) {
			this.cellphone = cellphone;
			return this;
		}

		public Builder address(String address) {
			this.address = address;
			return this;
		}

		public Builder postCode(String postCode) {
			this.postCode = postCode;
			return this;
		}

		public Builder country(Country country) {
			this.country = country;
			return this;
		}

		public Builder birthDay(Date birthDay) {
			this.birthDay = birthDay;
			return this;
		}

		public Builder lastVisit(Date lastVisit) {
			this.lastVisit = lastVisit;
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

		public Builder active(Boolean active) {
			this.active = active;
			return this;
		}

		public Builder inactivationReason(String inactivationReason) {
			this.inactivationReason = inactivationReason;
			return this;
		}

		public Builder inactivationDate(Date inactivationDate) {
			this.inactivationDate = inactivationDate;
			return this;
		}

		public Builder locale(String locale) {
			this.locale = locale;
			return this;
		}

		public Builder redirectUrl(String redirectUrl) {
			this.redirectUrl = redirectUrl;
			return this;
		}

		public Builder roles(List<Role> roles) {
			this.roles = roles;
			return this;
		}

		public Builder friendships(List<Friendship> friendships) {
			this.friendships = friendships;
			return this;
		}

		public User build() {
			return new User(this);
		}
	}

	private User(Builder builder) {
		this.setId(builder.id);
		this.name = builder.name;
		this.firstName = builder.firstName;
		this.lastName = builder.lastName;
		this.username = builder.username;
		this.password = builder.password;
		this.changedAttributes = builder.changedAttributes;
		this.localeObject = builder.localeObject;
		this.resetPasswordToken = builder.resetPasswordToken;
		this.resetPasswordTokenCreated = builder.resetPasswordTokenCreated;
		this.lastPassWordChangeDate = builder.lastPassWordChangeDate;
		this.email = builder.email;
		this.emailHash = builder.emailHash;
		this.avatarUrl = builder.avatarUrl;
		this.bannerUrl = builder.bannerUrl;
		this.telephone = builder.telephone;
		this.cellphone = builder.cellphone;
		this.address = builder.address;
		this.postCode = builder.postCode;
		this.country = builder.country;
		this.birthDay = builder.birthDay;
		this.lastVisit = builder.lastVisit;
		this.lastLogin = builder.lastLogin;
		this.loginAttempts = builder.loginAttempts;
		this.active = builder.active;
		this.inactivationReason = builder.inactivationReason;
		this.inactivationDate = builder.inactivationDate;
		this.locale = builder.locale;
		this.redirectUrl = builder.redirectUrl;
		this.roles = builder.roles;
		this.friendships = builder.friendships;
	}
}