package gr.abiss.calipso.model;


import gr.abiss.calipso.jpasearch.annotation.FieldFormFieldAsJasonConfig;
import gr.abiss.calipso.model.acl.AbstractAccount;
import gr.abiss.calipso.utils.MD5Utils;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author manos
 * 
 */
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public class User extends AbstractAccount {

	private static final long serialVersionUID = -7443491878604398347L;

	@Column(name = "first_name", nullable = false)
	private String firstName;
	
	@Column(name = "last_name", nullable = false)
	private String lastName;
	

	@Column(name = "user_password")
	private String userPassword;

	@Column(name = "password_changed")
	private Date lastPassWordChangeDate;

	@JsonIgnore
	@Column(name = "confirmation_token")
	private String confirmationToken;
	
	@FieldFormFieldAsJasonConfig(
			create = "{ validators: ['required', 'email'] }", 
			update = "{ validators: ['required', 'email'] }", 
			search = "{ validators: ['email'] }" 
	)
	@Column(name = "email", unique = true, nullable = false)
	private String email;

	@Column(name = "email_hash", nullable = false)
	private String emailHash;


	@Column(name = "birthday")
	private Date birthDay;

	@Column(name = "last_visit")
	private Date lastVisit;

	@Column(name = "login_attempts")
	private Short loginAttempts = 0;

	@Column(name = "locale", nullable = false)
	private String locale = "en";

	@Column(name = "date_format")
	private String dateFormat;

	public User() {
	}
	
	public User(String email) {
		this.email = email;
	}


	@Override
	public String toString() {
		return new ToStringBuilder(this).appendSuper(super.toString()).append("userName", this.getUserName())
				.append("email", this.getEmail()).toString();
	}

	/**
	 * Called by Hibernate <code>@PrePersist</code> and <code>@PreUpdate</code>
	 * to
	 * keep the email hash of the user up-to date
	 */
	@PrePersist
	@PreUpdate
	public void resetEmailHash() {
		// make sure it's trimmed
		this.setEmail(this.getEmail().trim());
		// update the hash
		this.setEmailHash(MD5Utils.md5Hex(this.getEmail()));
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

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public Date getLastPassWordChangeDate() {
		return lastPassWordChangeDate;
	}

	public void setLastPassWordChangeDate(Date lastPassWordChangeDate) {
		this.lastPassWordChangeDate = lastPassWordChangeDate;
	}

	public String getConfirmationToken() {
		return confirmationToken;
	}

	public void setConfirmationToken(String confirmationToken) {
		this.confirmationToken = confirmationToken;
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

	public Short getLoginAttempts() {
		return loginAttempts;
	}

	public void setLoginAttempts(Short loginAttempts) {
		this.loginAttempts = loginAttempts;
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

	/**
	 * Returns the username as the business key
	 * 
	 * @see gr.abiss.calipso.model.acl.Resource#getBusinessKey()
	 */
	@Override
	public String getBusinessKey() {
		return this.getUserName();
	}

	@Override
	public String getApiBasePath() {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<? extends GrantedAuthority> getRoles() {
		// TODO Auto-generated method stub
		return null;
	}
}