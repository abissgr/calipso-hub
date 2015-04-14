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

import gr.abiss.calipso.jpasearch.model.FormSchema;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.base.AuditableResource;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.domain.Persistable;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * Abstract base class for all persistent entities.
 */
@MappedSuperclass
// @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
// property = "id")

public abstract class AbstractPersistable implements FormSchemaAware, Persistable<String> {

	private static final long serialVersionUID = 2131186735039838008L;
	
	public static interface FormSchemaAwareView {}
	
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", unique = true)
	private String id;
	
	//@JsonView(FormSchemaAwareView.class)
	@Transient
	private FormSchema formSchema;

	public AbstractPersistable() {
		super();
	}

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
	public void setId(String id) {
		this.id = id;
	}

	public FormSchema getFormSchema() {
		return formSchema;
	}

	public void setFormSchema(FormSchema formSchema) {
		this.formSchema = formSchema;
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
		if (null == obj) {
			return false;
		}

		if (this == obj) {
			return true;
		}
		if (!(obj instanceof User)) {
			return false;
		}
		AbstractPersistable other = (AbstractPersistable) obj;
		EqualsBuilder builder = new EqualsBuilder();
		builder.append(this.getId(), other.getId());
		return builder.isEquals();
	}

	/**
	 *  @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		int hashCode = 17;

		hashCode += null == getId() ? 0 : getId().hashCode() * 31;

		return hashCode;
	}
}