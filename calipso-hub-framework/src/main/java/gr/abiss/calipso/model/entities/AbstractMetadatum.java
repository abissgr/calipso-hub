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
package gr.abiss.calipso.model.entities;

import gr.abiss.calipso.model.interfaces.MetadataSubject;
import gr.abiss.calipso.model.interfaces.Metadatum;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Abstract base persistent class for metadata entries. Implementations can
 * override relational specifics via javax.persistence.AssociationOverride
 * annotations
 */
@MappedSuperclass
@Table(uniqueConstraints = { 
		@UniqueConstraint(columnNames = { "subject", "predicate" }) 
	})
public abstract class AbstractMetadatum<S extends MetadataSubject>
		extends AbstractPersistable implements Metadatum<S> {

	private static final long serialVersionUID = -1468517690700208260L;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "subject", nullable = false)
	private S subject;

	@Column(name = "predicate", nullable = false, insertable = true, updatable = false)
	private String predicate;

	@Column(name = "object")
	private String object;

	public AbstractMetadatum() {
		super();
	}

	public AbstractMetadatum(S subject, String predicate, String object) {
		super();
		this.predicate = predicate;
		this.object = object;
		this.subject = subject;
		// this.subject.addMetadatum(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (null == obj) {
			return false;
		}

		if (this == obj) {
			return true;
		}
		if (!AbstractMetadatum.class.isInstance(obj)) {
			return false;
		}
		@SuppressWarnings("rawtypes")
		AbstractMetadatum that = (AbstractMetadatum) obj;
		return new EqualsBuilder()
				.append(this.getSubject(), that.getSubject())
				.append(this.getPredicate(), that.getPredicate()).isEquals();
	}
	
	@Override
	public S getSubject() {
		return subject;
	}

	@Override
	public void setSubject(S subject) {
		this.subject = subject;
	}

	@Override
	public String getPredicate() {
		return predicate;
	}

	@Override
	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}

	@Override
	public String getObject() {
		return object;
	}

	@Override
	public void setObject(String object) {
		this.object = object;
	}

}