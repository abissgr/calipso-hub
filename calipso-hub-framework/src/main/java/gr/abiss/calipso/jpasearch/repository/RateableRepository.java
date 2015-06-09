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
package gr.abiss.calipso.jpasearch.repository;

import gr.abiss.calipso.model.interfaces.Rateable;

import java.io.Serializable;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base repository for entities extending AbstractAuditableRateable or otherwise implementing Rateable
 * 
 * @see gr.abiss.calipso.model.entities.AbstractAuditableRateable
 * @see gr.abiss.calipso.model.interfaces.Rateable
 */
@NoRepositoryBean
public interface RateableRepository<T extends Rateable<?>, ID extends Serializable> extends BaseRepository<T, ID> {

	@Transactional(readOnly = false)
	@Modifying
	@Query("update #{#entityName} e set e.rating = ((e.rating * e.ratingsSize) + ?2) / (e.ratingsSize + 1), e.ratingsSize = (e.ratingsSize + 1) WHERE e.id = ?1")
	public void addRating(ID id, float rating);
	
}
