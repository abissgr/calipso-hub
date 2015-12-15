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
import gr.abiss.calipso.model.base.PartiallyUpdateable;
import gr.abiss.calipso.model.contactDetails.LocalRegionMailingAddress;
import gr.abiss.calipso.model.entities.AbstractAuditableMetadataSubject;
import gr.abiss.calipso.model.geography.Country;
import gr.abiss.calipso.model.interfaces.ReportDataSetSubject;
import gr.abiss.calipso.model.metadata.UserMetadatum;
import gr.abiss.calipso.model.serializers.SkipPropertySerializer;
import gr.abiss.calipso.userDetails.integration.LocalUser;
import gr.abiss.calipso.utils.MD5Utils;

import java.io.Serializable;
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
import javax.persistence.ManyToOne;
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