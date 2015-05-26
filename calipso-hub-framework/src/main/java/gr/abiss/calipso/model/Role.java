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

import gr.abiss.calipso.model.entities.AbstractAuditable;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wordnik.swagger.annotations.ApiModel;

/**
 */
@Entity
@Table(name = "role")
@Inheritance(strategy = InheritanceType.JOINED)
@ApiModel(value = "Role", description = "User principal roles. Roles are principals themselves and can be assigned to users.")
public class Role extends AbstractAuditable<User> implements GrantedAuthority {

	private static final long serialVersionUID = 3558291745762331656L;

	// global roles
	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	public static final String ROLE_SITE_OPERATOR = "ROLE_SITE_OPERATOR";
	public static final String ROLE_USER = "ROLE_USER";


	@Column(unique = true, nullable = false)
	private String name;
	
	@Column(length = 510)
	private String description;

	@JsonIgnore
	@ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
	private Collection<User> members = new ArrayList<User>(0);


	public Role() {
	}

	public Role(String name) {
		this.name = name;
	}

	public Role(String name, String description) {
		this(name);
		this.description = description;
	}

	@Override
	public boolean equals(Object obj) {
		if (null == obj) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Role)) {
			return false;
		}
		Role that = (Role) obj;
		return null == this.getName() ? false : this.getName().equals(that.getName());
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("name", this.getName()).toString();
	}

	/** 
	 *  {@inheritDoc}
	 */
	@Override
	public String getAuthority() {
		return this.getName();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Collection<User> getMembers() {
		return members;
	}

	public void setMembers(Collection<User> members) {
		this.members = members;
	}

}
