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
 * You should have received a copy of the GNU General Public License
 * along with Calipso. If not, see http://www.gnu.org/licenses/agpl.html
 */
package gr.abiss.calipso.model.base;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.domain.Persistable;

/**
 * Abstract base persistent class. Uses UUIDs for priimary key values.
 */
@MappedSuperclass
// @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
// property = "id")
public abstract class AbstractPersistable implements Persistable<String> {

	private static final long serialVersionUID = 2622873684791646229L;
	
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", unique = true)
	private String id;

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", this.getId()).toString();
	}

	/**
	 * Get the entity's primary key 
	 * @see org.springframework.data.domain.Persistable#getId()
	 */
	@Override
	public String getId() {

		return id;
	}

	/**
	 * Set the entity's primary key
	 * @param id the id to set
	 */
	protected void setId(final String id) {

		this.id = id;
	}

	/**
	 * @see org.springframework.data.domain.Persistable#isNew()
	 */
	@Override
	public boolean isNew() {

		return null == getId();
	}

	/** 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		boolean equals = false;
		if (obj != null && obj instanceof AbstractPersistable) {
			AbstractPersistable other = (AbstractPersistable) obj;
			EqualsBuilder builder = new EqualsBuilder();
			builder.append(getId(), other.getId());
			equals = builder.isEquals();
		}
		return equals;
	}

	/**
	 *  @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(getId());
		return builder.toHashCode();
	}
}