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

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.GenericGenerator;

/**
 * Abstract base class for persistent entities with assigned id
 * @param <ID> The id Serializable
 */
@MappedSuperclass
public abstract class AbstractAssignedidPersistable<ID extends Serializable> extends AbstractPersistable<ID> {

	private static final long serialVersionUID = 4340156130534111231L;

	@Id
	@Column(name = "id", unique = true)
	private ID id;
	
	@Formula("id")
	private ID savedId;

	public AbstractAssignedidPersistable(){
		
	}

	public AbstractAssignedidPersistable(ID id){
		this.id = id;
	}
	
	/**
	 * Get the entity's primary key 
	 * @see org.springframework.data.domain.Persistable#getId()
	 */
	@Override
	public ID getId() {
		return id;
	}
	
	/**
	 * Set the entity's primary key
	 * @param id the id to set
	 */
	public void setId(ID id) {
		this.id = id;
	}
	
	private ID getSavedId() {
		return savedId;
	}

	/**
	 * @see org.springframework.data.domain.Persistable#isNew()
	 */
	@Override
	public boolean isNew() {
		return null == getSavedId();
	}


}