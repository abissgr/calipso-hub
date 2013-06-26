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

	protected Class<T> getDomainClass() {
		return domainClass;
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
