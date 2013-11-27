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

import gr.abiss.calipso.ddd.core.model.interfaces.Metadatum;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

	public T merge(T entity);

	public T saveAndRefresh(T entity);

	public void refresh(T entity);
	
	public Class<T> getDomainClass();

	Metadatum addMetadatum(ID subjectId, String predicate, String object);

	List<Metadatum> addMetadata(ID subjectId, Map<String, String> metadata);

	void removeMetadatum(ID subjectId, String predicate);

	Metadatum findMetadatum(ID subjectId, String predicate);


}
