/**
 * calipso-hub-framework - A full stack, high level framework for lazy application hackers.
 * Copyright © 2005 Manos Batsis (manosbatsis gmail)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package gr.abiss.calipso.tiers.service.impl;

import gr.abiss.calipso.model.acl.AclClass;
import gr.abiss.calipso.model.acl.AclObject;
import gr.abiss.calipso.model.acl.AclObjectIdentity;
import gr.abiss.calipso.model.acl.AclSid;
import gr.abiss.calipso.model.cms.BinaryFile;
import gr.abiss.calipso.model.dto.ReportDataSet;
import gr.abiss.calipso.model.interfaces.CalipsoPersistable;
import gr.abiss.calipso.model.interfaces.MetadataSubject;
import gr.abiss.calipso.model.interfaces.Metadatum;
import gr.abiss.calipso.model.types.AggregateFunction;
import gr.abiss.calipso.model.types.TimeUnit;
import gr.abiss.calipso.repository.acl.AclClassRepository;
import gr.abiss.calipso.repository.acl.AclObjectIdentityRepository;
import gr.abiss.calipso.repository.acl.AclSidRepository;
import gr.abiss.calipso.tiers.repository.ModelRepository;
import gr.abiss.calipso.tiers.service.GenericService;
import gr.abiss.calipso.tiers.util.EntityUtil;
import gr.abiss.calipso.userDetails.model.ICalipsoUserDetails;
import gr.abiss.calipso.userDetails.util.SecurityUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.resthub.common.service.CrudServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * JPA Entity CRUD and search service that uses a Spring Data repository implementation.
 * You should extend it and inject your Repository bean by overriding setRepository.
 *
 * @param <T> Your resource class to manage, usually an entity class
 * @param <ID> Resource id type, usually Long or String
 * @param <R> The repository class
 */
@Transactional(readOnly = true)
public abstract class AbstractAclAwareServiceImpl<T extends CalipsoPersistable<ID>, ID extends Serializable, R extends ModelRepository<T, ID>>
		extends CrudServiceImpl<T, ID, R> implements GenericService<T, ID> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AbstractAclAwareServiceImpl.class);

	private AclObjectIdentityRepository aclObjectIdentityRepository;
	private AclClassRepository aclClassRepository;
	private AclSidRepository aclSidRepository;

	@Inject
	public void setAclObjectIdentityRepository(
			AclObjectIdentityRepository aclObjectIdentityRepository) {
		this.aclObjectIdentityRepository = aclObjectIdentityRepository;
	}

	@Inject
	public void setAclClassRepository(AclClassRepository aclClassRepository) {
		this.aclClassRepository = aclClassRepository;
	}

	@Inject
	public void setAclSidRepository(AclSidRepository aclSidRepository) {
		this.aclSidRepository = aclSidRepository;
	}

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
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = false)
	@PreAuthorize(T.PRE_AUTHORIZE_CREATE)
	public T create(@P("resource") T resource) {
		Map<String, Metadatum> metadata = noteMetadata(resource);
		T saved = super.create(resource);
		createAclObjectIdentity(saved, this.getDomainClass());
		persistNotedMetadata(metadata, saved);
		return saved;
	}

	protected void createAclObjectIdentity(T saved, Class domainClass) {
		if (AclObject.class.isAssignableFrom(domainClass)) {
			AclObject<ID, ID> aclObject = (AclObject<ID, ID>) saved;
			AclClass aclClass = this.aclClassRepository
					.findByClassName(domainClass.getName());

			Serializable sid = aclObject.getOwner();
			AclSid aclSid = null;
			// use current principal as owner?
			if (sid == null) {
				UserDetails userDetails = SecurityUtil.getPrincipal();
				if (userDetails != null) {
					sid = userDetails.getUsername();
				}
			}

			// add owner if any
			if (sid != null) {
				aclSid = this.aclSidRepository.findBySid(sid.toString());
			}

			// TODO: parent
			this.aclObjectIdentityRepository.save(new AclObjectIdentity(
					aclObject.getIdentity().toString(), aclClass, null, aclSid,
					aclObject.getEntriesInheriting()));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = false)
	@PreAuthorize(T.PRE_AUTHORIZE_UPDATE)
	public T update(@P("resource") T resource) {
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("update, resource: " + resource);
		}
		Map<String, Metadatum> metadata = noteMetadata(resource);
		T saved = super.update(resource);
		persistNotedMetadata(metadata, saved);
		return saved;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = false)
	@PreAuthorize(T.PRE_AUTHORIZE_UPDATE)
	public T patch(@P("resource") T resource) {
		// make sure entity is set to support partial updates
		T persisted = this.findById(resource.getId());
		// copy non-null properties to persisted
		BeanUtils.copyProperties(resource, persisted, EntityUtil.getNullPropertyNames(resource));
		resource = persisted;
		// FW to normal update
		return this.update(persisted);
	}


	private void persistNotedMetadata(Map<String, Metadatum> metadata, T saved) {

		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("persistNotedMetadata, noted: " + metadata);
		}
		if (!CollectionUtils.isEmpty(metadata)) {
			MetadataSubject subject = (MetadataSubject) saved;
			Metadatum[] metaArray = metadata.values().toArray(
					new Metadatum[metadata.values().size()]);
			for (int i = 0; i < metaArray.length; i++) {
				Metadatum metadatum = metaArray[i];
				subject.addMetadatum(this.repository.addMetadatum(
						saved.getId(), metadatum.getPredicate(),
						metadatum.getObject()));
			}
		}
	}

	private Map<String, Metadatum> noteMetadata(T resource) {
		Map<String, Metadatum> metadata = null;
		if (MetadataSubject.class.isAssignableFrom(this.getDomainClass())) {
			metadata = ((MetadataSubject) resource).getMetadata();
			((MetadataSubject) resource)
					.setMetadata(new HashMap<String, Metadatum>());
			if(LOGGER.isDebugEnabled()){
				LOGGER.debug("noteMetadata, noted: " + metadata);
			}
		} else {
			if(LOGGER.isDebugEnabled()){
				LOGGER.debug("noteMetadata, not a metadata subject");
			}
		}
		return metadata;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = false)
	public void addMetadatum(ID subjectId, Metadatum dto) {
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("addMetadatum subjectId: "+subjectId + ", metadatum: "+dto);
		}
		this.repository.addMetadatum(subjectId, dto.getPredicate(),
				dto.getObject());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = false)
	public void addMetadata(ID subjectId, Collection<Metadatum> dtos) {
		if (!CollectionUtils.isEmpty(dtos)) {
			for (Metadatum dto : dtos) {
				this.addMetadatum(subjectId, dto);
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void removeMetadatum(ID subjectId, String predicate) {
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("removeMetadatum subjectId: " + subjectId + ", predicate: "
				+ predicate);
		}
		this.repository.removeMetadatum(subjectId, predicate);
	}
	

	@Override
	public Page<ReportDataSet> getReportDatasets(Pageable pageable, TimeUnit timeUnit,
			String dateField, Date dateFrom, Date dateTo,
			String reportName) {
		
		return new PageImpl<ReportDataSet>(new ArrayList<ReportDataSet>(0), pageable, 0);		
		
	}
	/** 
	 * Get the entity's file uploads for this propert
	 * @param subjectId the entity id
	 * @param propertyName the property holding the upload(s)
	 * @return the uploads
	 */
	public List<BinaryFile> getUploadsForProperty(ID subjectId, String propertyName){
		return this.repository.getUploadsForProperty(subjectId, propertyName);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	@Transactional(readOnly = false)
	@PreAuthorize(T.PRE_AUTHORIZE_DELETE)
	public void delete(@P("resource") T resource) {
		// TODO Auto-generated method stub
		super.delete(resource);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	@Transactional(readOnly = false)
	@PreAuthorize(T.PRE_AUTHORIZE_DELETE_BY_ID)
	public void delete(ID id) {
		// TODO Auto-generated method stub
		super.delete(id);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	@Transactional(readOnly = false)
	@PreAuthorize(T.PRE_AUTHORIZE_DELETE_ALL)
	public void deleteAll() {
		// TODO Auto-generated method stub
		super.deleteAll();
	}

    /**
     * {@inheritDoc}
     */
	@Override
	@Transactional(readOnly = false)
	@PreAuthorize(T.PRE_AUTHORIZE_DELETE_WITH_CASCADE)
	public void deleteAllWithCascade() {
		// TODO Auto-generated method stub
		super.deleteAllWithCascade();
	}

    /**
     * {@inheritDoc}
     */
	@Override
	@PreAuthorize(T.PRE_AUTHORIZE_VIEW)
	public T findById(ID id) {
		// TODO Auto-generated method stub
		return super.findById(id);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	@PreAuthorize(T.PRE_AUTHORIZE_FIND_BY_IDS)
	public Iterable<T> findByIds(Set<ID> ids) {
		// TODO Auto-generated method stub
		return super.findByIds(ids);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	@PreAuthorize(T.PRE_AUTHORIZE_FIND_ALL)
	public Iterable<T> findAll() {
		// TODO Auto-generated method stub
		return super.findAll();
	}

    /**
     * {@inheritDoc}
     */
	@Override
	@PreAuthorize(T.PRE_AUTHORIZE_SEARCH)
	public Page<T> findAll(Pageable pageRequest) {
		// TODO Auto-generated method stub
		return super.findAll(pageRequest);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	@PreAuthorize(T.PRE_AUTHORIZE_COUNT)
	public Long count() {
		// TODO Auto-generated method stub
		return super.count();
	}
	
	

}