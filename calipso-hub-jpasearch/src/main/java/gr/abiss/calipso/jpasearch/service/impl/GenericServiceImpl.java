/**
 * Copyright (c) 2007 - 2013 www.Abiss.gr
 *
 * This file is part of Calipso, a software platform by www.Abiss.gr.
 *
 * Calipso is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Calipso is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser Public License
 * along with Calipso. If not, see http://www.gnu.org/licenses/lgpl-3.0.txt
 */
package gr.abiss.calipso.jpasearch.service.impl;

import gr.abiss.calipso.jpasearch.repository.BaseRepository;

import java.io.Serializable;

import org.resthub.common.service.CrudServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * Provides access to domain class type and transactional write support
 */
public abstract class GenericServiceImpl<T, ID extends Serializable, R extends BaseRepository<T, ID>>
		extends CrudServiceImpl<T, ID, R> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GenericServiceImpl.class);

	/**
	 * Get the entity Class corresponding to the generic T
	 * @return the corresponding entity Class
	 */
	public Class<T> getDomainClass() {
		return this.repository.getDomainClass();
	}

	@Override
	@Transactional(readOnly = false)
	public T create(T resource) {
		return super.create(resource);
	}

	@Override
	@Transactional(readOnly = false)
	public T update(T resource) {
		return super.update(resource);
	}

	@Override
	@Transactional(readOnly = false)
	public void delete(T resource) {
		super.delete(resource);
	}

	@Override
	@Transactional(readOnly = false)
	public void delete(ID id) {
		super.delete(id);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteAll() {
		super.deleteAll();
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteAllWithCascade() {
		super.deleteAllWithCascade();
	}
	
}