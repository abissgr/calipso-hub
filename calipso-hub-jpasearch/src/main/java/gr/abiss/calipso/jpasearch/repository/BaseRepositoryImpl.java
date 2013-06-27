/**
 * Copyright (c) 2007 - 2013 www.Abiss.gr
 *
 * This file is part of Calipso, a software platform by www.Abiss.gr.
 *
 * Calipso is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Calipso is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Calipso. If not, see http://www.gnu.org/licenses/agpl.html
 */
package gr.abiss.calipso.jpasearch.repository;

import gr.abiss.calipso.jpasearch.data.ParameterMapBackedPageRequest;
import gr.abiss.calipso.jpasearch.specifications.GenericSpecifications;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

public class BaseRepositoryImpl<T, ID extends Serializable>
 extends SimpleJpaRepository<T, ID> implements
		BaseRepository<T, ID> {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseRepositoryImpl.class);

	private final EntityManager entityManager;
	private final Class<T> domainClass;

	// There are two constructors to choose from, either can be used.
	public BaseRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
		super(domainClass, entityManager);

		// This is the recommended method for accessing inherited class
		// dependencies.
		this.entityManager = entityManager;
		this.domainClass = domainClass;
	}

	@Override
	public Class<T> getDomainClass() {
		return this.domainClass;
	}

	@Override
	public T merge(T entity) {
		return this.entityManager.merge(entity);
	}

	@Override
	public void refresh(T entity) {
		this.entityManager.refresh(entity);
	}

	@Override
	public T saveAndRefresh(T entity) {
		entity = this.save(entity);
		this.entityManager.refresh(entity);
		return entity;
	}

	@Override
	public Page<T> findAll(Pageable pageable) {
		if (pageable instanceof ParameterMapBackedPageRequest) {
			Specification<T> spec = GenericSpecifications.matchAll(getDomainClass(), ((ParameterMapBackedPageRequest) pageable).getParameterMap());
			return super.findAll(spec, pageable);
		} else {
			return super.findAll(pageable);
		}
	}

}
