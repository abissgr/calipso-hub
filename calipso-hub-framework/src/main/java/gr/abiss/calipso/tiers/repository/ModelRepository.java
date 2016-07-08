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
package gr.abiss.calipso.tiers.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import gr.abiss.calipso.model.cms.BinaryFile;
import gr.abiss.calipso.model.interfaces.Metadatum;

/**
 * Captures the domain type to manage as well as the domain type's id type.
 * Provides complete Search and CRUD operations, as well as operations 
 * to manage metadata and attachments. 
 * 
 * @param <T> the domain type the repository manages
 * @param <ID> the type of the id of the entity the repository manages
 * 
 * @see org.springframework.data.domain.Sort
 * @see org.springframework.data.domain.Pageable
 * @see org.springframework.data.domain.Page
 */
@NoRepositoryBean
public interface ModelRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

	public EntityManager getEntityManager();

	public T merge(T entity);

	public T saveAndRefresh(T entity);

	public void refresh(T entity);
	
	public Class<T> getDomainClass();

	public Metadatum addMetadatum(ID subjectId, String predicate, String object);

	public List<Metadatum> addMetadata(ID subjectId, Map<String, String> metadata);

	public void removeMetadatum(ID subjectId, String predicate);

	public Metadatum findMetadatum(ID subjectId, String predicate);
	
	/** 
	 * Get the entity's file uploads for this property
	 * @param subjectId the entity id
	 * @param propertyName the property holding the upload(s)
	 * @return the uploads
	 */
	public List<BinaryFile> getUploadsForProperty(ID subjectId, String propertyName);
}
