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

import gr.abiss.calipso.model.acl.AbstractAccount;
import gr.abiss.calipso.model.json.serializers.DateTimeToUnixTimestampSerializer;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.DateTime;
import org.springframework.data.domain.Auditable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Abstract base class for all auditable persistent entities
 */
@MappedSuperclass
public abstract class AbstractAuditable extends AbstractPersistable implements
 Auditable<AbstractAccount, String> {

	@Column(name = "deleted", nullable = false)
	private Boolean deleted = true;

	@JsonIgnore
	@ManyToOne(/* cascade=CascadeType.ALL, */fetch = FetchType.EAGER)
	@JoinColumn(name = "created_by", referencedColumnName = "id", nullable = true)
	// TODO: not null,
	private AbstractAccount createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	@JsonSerialize(using = DateTimeToUnixTimestampSerializer.class)
	private Date createdDate;

	@JsonIgnore
	@ManyToOne(/* cascade=CascadeType.ALL, */fetch = FetchType.EAGER)
	@JoinColumn(name = "updated_by", referencedColumnName = "id", nullable = true)
	// TODO: not null
	private AbstractAccount lastModifiedBy;

	@Temporal(TemporalType.TIMESTAMP)
	@JsonSerialize(using = DateTimeToUnixTimestampSerializer.class)
	private Date lastModifiedDate;

	@Override
	public String toString() {
		return new ToStringBuilder(this).appendSuper(super.toString()).toString();
	}
	/**
	 * Called by Hibernate <code>@PrePersist</code> and <code>@PreUpdate</code>
	 * to
	 * keep the email hash of the user up-to date
	 */
	@PrePersist
	@PreUpdate
	// TODO: shouldnt spring handle this?
	private void updateAuditInfo() {
		DateTime now = DateTime.now();
		if (this.getCreatedDate() == null) {
			this.setCreatedDate(now);
		}
		this.setLastModifiedDate(now);
	}

	public Boolean isDeleted() {
		return this.deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	/** 
	 * @see org.springframework.data.domain.Auditable#getCreatedBy()
	 */
	@Override
	public AbstractAccount getCreatedBy() {
		return createdBy;
	}

	/**
	 * @see org.springframework.data.domain.Auditable#setCreatedBy(java.lang.Object)
	 */
	@Override
	public void setCreatedBy(final AbstractAccount createdBy) {

		this.createdBy = createdBy;
	}

	/**
	 * @see org.springframework.data.domain.Auditable#getCreatedDate()
	 */
    @Override
	@JoinColumn(name="date_created")
	public DateTime getCreatedDate() {

		return null == createdDate ? null : new DateTime(createdDate);
	}

	/**
	 * @see org.springframework.data.domain.Auditable#setCreatedDate(org.joda.time.DateTime)
	 */
	@Override
	public void setCreatedDate(final DateTime createdDate) {

		this.createdDate = null == createdDate ? null : createdDate.toDate();
	}

	/**
	 * @see org.springframework.data.domain.Auditable#getLastModifiedBy()
	 */
	@Override
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="modified_by")
	public AbstractAccount getLastModifiedBy() {
		return lastModifiedBy;
	}

	/**
	 * @see org.springframework.data.domain.Auditable#setLastModifiedBy(java.lang.Object)
	 */
	@Override
	public void setLastModifiedBy(final AbstractAccount lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	/**
	 * @see org.springframework.data.domain.Auditable#getLastModifiedDate()
	 */
    @Override
	@JoinColumn(name="date_last_modified")
	public DateTime getLastModifiedDate() {
		return null == lastModifiedDate ? null : new DateTime(lastModifiedDate);
	}

	/** 
	 * @see org.springframework.data.domain.Auditable#setLastModifiedDate(org.joda.time.DateTime)
	 */
	@Override
	public void setLastModifiedDate(final DateTime lastModifiedDate) {
		this.lastModifiedDate = null == lastModifiedDate ? null : lastModifiedDate.toDate();
	}
}
