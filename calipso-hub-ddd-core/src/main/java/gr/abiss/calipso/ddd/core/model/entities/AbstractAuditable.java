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
package gr.abiss.calipso.ddd.core.model.entities;

import gr.abiss.calipso.ddd.core.model.serializers.DateTimeToUnixTimestampSerializer;

import java.util.Date;

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
public abstract class AbstractAuditable<U> extends AbstractPersistable
		implements Auditable<U, String> {

	@JsonIgnore
	@ManyToOne(/* cascade=CascadeType.ALL, */fetch = FetchType.EAGER)
	@JoinColumn(name = "created_by", referencedColumnName = "id", nullable = true)
	// TODO: not null,
	private U createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	@JsonSerialize(using = DateTimeToUnixTimestampSerializer.class)
	private Date createdDate;

	@JsonIgnore
	@ManyToOne(/* cascade=CascadeType.ALL, */fetch = FetchType.EAGER)
	@JoinColumn(name = "updated_by", referencedColumnName = "id", nullable = true)
	// TODO: not null
	private U lastModifiedBy;

	@Temporal(TemporalType.TIMESTAMP)
	@JsonSerialize(using = DateTimeToUnixTimestampSerializer.class)
	private Date lastModifiedDate;

	public AbstractAuditable() {
		super();
	}

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
	private void updateAuditInfo() {
		DateTime now = DateTime.now();
		if (this.getCreatedDate() == null) {
			this.setCreatedDate(now);
		}
		this.setLastModifiedDate(now);
	}

	/** 
	 * @see org.springframework.data.domain.Auditable#getCreatedBy()
	 */
	@Override
	public U getCreatedBy() {
		return createdBy;
	}

	/**
	 * @see org.springframework.data.domain.Auditable#setCreatedBy(java.lang.Object)
	 */
	@Override
	public void setCreatedBy(final U createdBy) {

		this.createdBy = createdBy;
	}

	/**
	 * @see org.springframework.data.domain.Auditable#getCreatedDate()
	 */
	@Override
	@JoinColumn(name = "date_created")
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
	public U getLastModifiedBy() {
		return lastModifiedBy;
	}

	/**
	 * @see org.springframework.data.domain.Auditable#setLastModifiedBy(java.lang.Object)
	 */
	@Override
	public void setLastModifiedBy(final U lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	/**
	 * @see org.springframework.data.domain.Auditable#getLastModifiedDate()
	 */
	@Override
	@JoinColumn(name = "date_last_modified")
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
