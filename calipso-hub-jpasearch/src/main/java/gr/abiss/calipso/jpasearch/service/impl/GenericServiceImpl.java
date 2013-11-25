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
package gr.abiss.calipso.jpasearch.service.impl;

import gr.abiss.calipso.ddd.core.model.dto.MetadatumDTO;
import gr.abiss.calipso.jpasearch.repository.BaseRepository;
import gr.abiss.calipso.jpasearch.service.GenericService;

import java.io.Serializable;

import org.resthub.common.service.CrudServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Persistable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Provides access to domain class type and transactional write support
 */
@Transactional(readOnly = true)
public abstract class GenericServiceImpl<T extends Persistable<ID>, ID extends Serializable, R extends BaseRepository<T, ID>>
		extends CrudServiceImpl<T, ID, R> implements GenericService<T, ID> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GenericServiceImpl.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Class<T> getDomainClass() {
		return this.repository.getDomainClass();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = false)
	public T create(T resource) {
		return super.create(resource);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = false)
	public T update(T resource) {
		return super.update(resource);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = false)
	public void delete(T resource) {
		super.delete(resource);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = false)
	public void delete(ID id) {
		super.delete(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = false)
	public void deleteAll() {
		super.deleteAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = false)
	public void deleteAllWithCascade() {
		super.deleteAllWithCascade();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = false)
	public void addMetadatum(ID subjectId, MetadatumDTO dto) {
		LOGGER.info("addMetadatum subjectId: "+subjectId + ", metadatum: "+dto);
		this.repository.addMetadatum(subjectId, dto);
	}

	@Override
	@Transactional(readOnly = false)
	public void removeMetadatum(ID subjectId, String predicate) {
		LOGGER.info("removeMetadatum subjectId: " + subjectId + ", predicate: "
				+ predicate);
		this.repository.removeMetadatum(subjectId, predicate);
	}


}