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
package gr.abiss.calipso.jpasearch.service;

import gr.abiss.calipso.ddd.core.model.dto.MetadatumDTO;

import java.io.Serializable;

import org.resthub.common.service.CrudService;
import org.springframework.data.domain.Persistable;

/**
 * CRUD Service interface.
 * 
 * @param <T>
 *            Your resource POJO to manage, maybe an entity or DTO class
 * @param <ID>
 *            Resource id type, usually Long or String
 */
public interface GenericService<T extends Persistable<ID>, ID extends Serializable>
		extends
		CrudService<T, ID> {

	/**
	 * Get the entity Class corresponding to the generic T
	 * 
	 * @return the corresponding entity Class
	 */
	public Class<T> getDomainClass();

	public void addMetadatum(ID subjectId, MetadatumDTO dto);

	public void removeMetadatum(ID subjectId, String predicate);
}
