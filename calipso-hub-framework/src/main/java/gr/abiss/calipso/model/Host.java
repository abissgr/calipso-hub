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
import gr.abiss.calipso.model.entities.AbstractAuditableMetadataSubject;
import gr.abiss.calipso.model.serializers.SkipPropertySerializer;
import gr.abiss.calipso.model.metadata.UserMetadatum;
import gr.abiss.calipso.userDetails.integration.LocalUser;
import gr.abiss.calipso.utils.MD5Utils;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Formula;
import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
//import com.wordnik.swagger.annotations.ApiModel;

/**
 */
@Entity
//@ApiModel()
@Table(name = "host")
public class Host extends AbstractAuditable<User> {

	private static final long serialVersionUID = -7942906897981646998L;

	@Formula("domain")
	private String name;
	
	@Column(name = "domain", nullable = false)
	private String domain;
	
	@ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "host_aliases", joinColumns = @JoinColumn(name = "host_id"))
	@Column(name = "host_alias")
	Set<String> aliases = new HashSet<String>();

	public Host() {
		super();
	}

	public Host(String domain) {
		this();
		this.domain = domain;
	}


	@Override
	public boolean equals(Object obj) {
		if (null == obj) {
			return false;
		}

		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Host)) {
			return false;
		}
		Host that = (Host) obj;
		return null == this.getId() ? false : this.getId().equals(that.getId());
	}

	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public Set<String> getAliases() {
		return aliases;
	}

	public void setAliases(Set<String> aliases) {
		this.aliases = aliases;
	}

	public void addAlias(String alias) {
		if(this.aliases == null){
			this.aliases = new HashSet<String>();
		}
		this.aliases.add(alias);
	}
	
	

}