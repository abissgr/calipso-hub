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
package gr.abiss.calipso.model.entities;

import gr.abiss.calipso.model.base.AbstractSystemUuidPersistable;
import gr.abiss.calipso.model.interfaces.MetadataSubject;
import gr.abiss.calipso.model.interfaces.Metadatum;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

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
public abstract class AbstractAuditabeMetadatum<S extends MetadataSubject, U extends AbstractSystemUuidPersistable>
		extends AbstractAuditable<U> implements Metadatum<S> {

	private static final long serialVersionUID = -1468517690700208260L;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "subject", nullable = false)
	private S subject;

	// @Id
	@Column(name = "predicate", nullable = false)
	private String predicate;

	@Column(name = "object")
	private String object;
	public AbstractAuditabeMetadatum(String predicate, String object) {
		super();
		this.predicate = predicate;
		this.object = object;
	}
	public AbstractAuditabeMetadatum(S subject, String predicate, String object) {
		super();
		this.predicate = predicate;
		this.object = object;
		this.subject = subject;
		// this.subject.addMetadatum(this);
	}

	public AbstractAuditabeMetadatum() {
		super();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
		.append("predicate", this.getPredicate())
		.append("object", this.getObject()).toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (null == obj) {
			return false;
		}

		if (this == obj) {
			return true;
		}
		if (!AbstractAuditabeMetadatum.class.isInstance(obj)) {
			return false;
		}
		@SuppressWarnings("rawtypes")
		AbstractAuditabeMetadatum that = (AbstractAuditabeMetadatum) obj;
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