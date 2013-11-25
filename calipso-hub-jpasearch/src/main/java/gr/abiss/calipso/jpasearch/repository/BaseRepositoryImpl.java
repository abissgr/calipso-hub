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

import gr.abiss.calipso.ddd.core.model.dto.MetadatumDTO;
import gr.abiss.calipso.ddd.core.model.interfaces.MetadataSubject;
import gr.abiss.calipso.ddd.core.model.interfaces.Metadatum;
import gr.abiss.calipso.jpasearch.data.ParameterMapBackedPageRequest;
import gr.abiss.calipso.jpasearch.data.RestrictionBackedPageRequest;
import gr.abiss.calipso.jpasearch.specifications.GenericSpecifications;

import java.io.Serializable;
import java.util.List;

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
	public void addMetadatum(ID subjectId, MetadatumDTO dto) {
		ensureMetadataIsSupported();
		LOGGER.info("addMetadatum subjectId: " + subjectId + ", dto: " + dto);
		Metadatum metadatum = this.findMetadatum(subjectId, dto.getPredicate());
		LOGGER.info("addMetadatum metadatum: " + metadatum);
		if(metadatum == null){
			T entity = this.findOne(subjectId);
//			Class<?> metadatumClass = ((MetadataSubject) entity)
//				.getMetadataDomainClass();
			MetadataSubject subject = (MetadataSubject) entity;
			metadatum = this.createMetadatum(subject, dto.getPredicate(), dto.getObject());
		} else {
			// if exists, only update the value
			metadatum.setObject(dto.getObject());
		}
		
		// subject.addMetadatum(dto.getPredicate(), dto.getObject());
		// this.entityManager.merge(entity);
		this.entityManager.merge(metadatum);
	}

	@SuppressWarnings("unchecked")
	private Metadatum createMetadatum(MetadataSubject subject,
			String predicate,
			String object) {
		Class<?> metadatumClass = subject
				.getMetadataDomainClass();
		Metadatum metadatum = null;
		try {
			metadatum = (Metadatum) metadatumClass.getConstructor(
					this.getDomainClass(), String.class, String.class)
					.newInstance(subject, predicate, object);
		} catch (Exception e) {
			throw new RuntimeException("Failed adding metadatum", e);
		}
		return metadatum;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void removeMetadatum(ID subjectId, String predicate) {
		ensureMetadataIsSupported();
		T subjectEntity = this.findOne(subjectId);
		Class<?> metadatumClass = ((MetadataSubject) subjectEntity)
				.getMetadataDomainClass();
		// TODO: refactor to criteria
		Metadatum metadatum = findMetadatum(subjectId, predicate,
				metadatumClass);
		this.entityManager.remove(metadatum);
		// CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		// CriteriaQuery criteria = builder.createQuery(metadatumClass);
		// Root root = criteria.from(metadatumClass);
		// criteria.where( builder.equal(root.get("predicate"), predicate));
		
		
		
//		T entity = this.findOne(subjectId);
//		MetadataSubject subject = (MetadataSubject) entity;
//		if (subject.getMetadata() != null) {
//			subject.getMetadata().remove(predicate);
//			this.merge(entity);
//		}
	}

	@Override
	public Metadatum findMetadatum(ID subjectId, String predicate) {
		T subjectEntity = this.findOne(subjectId);
		Class<?> metadatumClass = ((MetadataSubject) subjectEntity)
				.getMetadataDomainClass();
		return this.findMetadatum(subjectId, predicate, metadatumClass);
		
	}
	
	protected Metadatum findMetadatum(ID subjectId, String predicate,
			Class<?> metadatumClass) {
		List<Metadatum> results = this.entityManager
				.createQuery(
				"from " + metadatumClass.getSimpleName()
						+ " m where m.predicate = ?1 and m.subject.id = ?2")
						.setParameter(1, predicate)
						.setParameter(2, subjectId)
				.getResultList();
		Metadatum metadatum = results.isEmpty() ? null : results.get(0);
		return metadatum;
	}

	protected void ensureMetadataIsSupported() {
		if (!MetadataSubject.class.isAssignableFrom(getDomainClass())) {
			throw new UnsupportedOperationException();
		}
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
		} 
		else if (pageable instanceof RestrictionBackedPageRequest) {
			Specification<T> spec = GenericSpecifications.matchAll(getDomainClass(), ((RestrictionBackedPageRequest) pageable).getRestriction());
			return super.findAll(spec, pageable);
		} else {
			return super.findAll(pageable);
		}
	}

}
