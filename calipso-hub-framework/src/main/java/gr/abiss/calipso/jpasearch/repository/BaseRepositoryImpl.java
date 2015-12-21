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

import gr.abiss.calipso.model.base.AbstractSystemUuidPersistable;
import gr.abiss.calipso.model.cms.BinaryFile;
import gr.abiss.calipso.model.dto.ReportDataSet;
import gr.abiss.calipso.model.interfaces.MetadataSubject;
import gr.abiss.calipso.model.interfaces.Metadatum;
import gr.abiss.calipso.model.types.AggregateFunction;
import gr.abiss.calipso.model.types.TimeUnit;
import gr.abiss.calipso.jpasearch.data.ParameterMapBackedPageRequest;
import gr.abiss.calipso.jpasearch.data.RestrictionBackedPageRequest;
import gr.abiss.calipso.jpasearch.specifications.GenericSpecifications;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.SetJoin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

public class BaseRepositoryImpl<T, ID extends Serializable> extends
		SimpleJpaRepository<T, ID> implements BaseRepository<T, ID> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(BaseRepositoryImpl.class);

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

	/**
	 * @return the entityManager
	 */
	@Override
	public EntityManager getEntityManager() {
		return this.entityManager;
	}

	
	
	@Override
	public T merge(T entity) {
		return this.getEntityManager().merge(entity);
	}

	@Override
	public Metadatum addMetadatum(ID subjectId, String predicate, String object) {
		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put(predicate, object);
		List<Metadatum> saved = addMetadata(subjectId, metadata);
		if (!CollectionUtils.isEmpty(metadata)) {
			return saved.get(0);
		} else {
			return null;
		}
	}

	@Override
	public List<Metadatum> addMetadata(ID subjectId,
			Map<String, String> metadata) {
		ensureMetadataIsSupported();
		List<Metadatum> saved;
		if (!CollectionUtils.isEmpty(metadata)) {
			saved = new ArrayList<Metadatum>(metadata.size());
			for (String predicate : metadata.keySet()) {
				LOGGER.info("addMetadatum subjectId: " + subjectId
						+ ", predicate: " + predicate);
				Metadatum metadatum = this.findMetadatum(subjectId, predicate);
				LOGGER.info("addMetadatum metadatum: " + metadatum);
				if (metadatum == null) {
					T entity = this.findOne(subjectId);
					// Class<?> metadatumClass = ((MetadataSubject) entity)
					// .getMetadataDomainClass();
					MetadataSubject subject = (MetadataSubject) entity;
					metadatum = this.buildMetadatum(subject, predicate,
							metadata.get(predicate));
					this.getEntityManager().persist(metadatum);
				} else {
					// if exists, only update the value
					metadatum.setObject(metadata.get(predicate));
					metadatum = this.getEntityManager().merge(metadatum);
				}

				// subject.addMetadatum(dto.getPredicate(), dto.getObject());
				// this.entityManager.merge(entity);
				LOGGER.info("addMetadatum saved metadatum: " + metadatum);
				saved.add(metadatum);
			}
		} else {
			saved = new ArrayList<Metadatum>(0);
		}
		LOGGER.info("addMetadatum returns: " + saved);
		return saved;
	}

	@SuppressWarnings("unchecked")
	private Metadatum buildMetadatum(MetadataSubject subject, String predicate,
			String object) {
		Class<?> metadatumClass = subject.getMetadataDomainClass();
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
		Assert.notNull(subjectId);
		Assert.notNull(predicate);
		ensureMetadataIsSupported();
		T subjectEntity = this.findOne(subjectId);
		Class<?> metadatumClass = ((MetadataSubject) subjectEntity)
				.getMetadataDomainClass();
		// TODO: refactor to criteria
		Metadatum metadatum = findMetadatum(subjectId, predicate,
				metadatumClass);
		if (metadatum != null) {
			this.getEntityManager().remove(metadatum);
		}
		// CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		// CriteriaQuery criteria = builder.createQuery(metadatumClass);
		// Root root = criteria.from(metadatumClass);
		// criteria.where( builder.equal(root.get("predicate"), predicate));

		// T entity = this.findOne(subjectId);
		// MetadataSubject subject = (MetadataSubject) entity;
		// if (subject.getMetadata() != null) {
		// subject.getMetadata().remove(predicate);
		// this.merge(entity);
		// }
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
		List<Metadatum> results = this
				.getEntityManager()
				.createQuery(
						"from "
								+ metadatumClass.getSimpleName()
								+ " m where m.predicate = ?1 and m.subject.id = ?2")
				.setParameter(1, predicate).setParameter(2, subjectId)
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
		this.getEntityManager().refresh(entity);
	}

	@Override
	public T saveAndRefresh(T entity) {
		entity = this.save(entity);
		this.getEntityManager().refresh(entity);
		return entity;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Page<T> findAll(Pageable pageable) {
		if (pageable instanceof ParameterMapBackedPageRequest) {
			Specification<T> spec = GenericSpecifications.matchAll(
					getDomainClass(),
					((ParameterMapBackedPageRequest) pageable)
							.getParameterMap());
			return super.findAll(spec, pageable);
		} else if (pageable instanceof RestrictionBackedPageRequest) {
			Specification<T> spec = GenericSpecifications.matchAll(
					getDomainClass(),
					((RestrictionBackedPageRequest) pageable).getRestriction());
			return super.findAll(spec, pageable);
		} else {
			return super.findAll(pageable);
		}
	}

	/** 
	 * Get the entity's file uploads for this property
	 * @param subjectId the entity id
	 * @param propertyName the property holding the upload(s)
	 * @return the uploads
	 */
	public List<BinaryFile> getUploadsForProperty(ID subjectId, String propertyName){
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();

		CriteriaQuery<BinaryFile> query = cb.createQuery(BinaryFile.class);
		Root<T> root = query.from(this.domainClass);
		query.where(cb.equal(root.get("id"), subjectId));
		Selection<? extends BinaryFile> join = root.join(propertyName,JoinType.INNER);
		query.select(join);
		return this.entityManager.createQuery(query).getResultList();
	}
}
