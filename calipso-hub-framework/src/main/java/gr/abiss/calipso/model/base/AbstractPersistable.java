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
package gr.abiss.calipso.model.base;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.domain.Persistable;

import gr.abiss.calipso.model.User;

/**
 * Abstract base class for all persistent entities.
 * @param <ID> The id Serializable
 */
@MappedSuperclass
public abstract class AbstractPersistable<ID extends Serializable> implements Persistable<ID> {

	private static final long serialVersionUID = -6009587976502456848L;

    public static interface ItemView {}
    public static interface CollectionView {}

	public AbstractPersistable() {
		super();
	}
	
	public AbstractPersistable(ID id) {
		this.setId(id);
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
	public abstract ID getId();

	/**
	 * Set the entity's primary key
	 * @param id the id to set
	 */
	public abstract void setId(ID id);

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