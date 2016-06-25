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
package gr.abiss.calipso.tiers.specifications;

import gr.abiss.calipso.userDetails.model.ICalipsoUserDetails;
import gr.abiss.calipso.userDetails.util.SecurityUtil;

import java.io.Serializable;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 * A predicate that automatically enforces the current principal (i.e. loggedin user) id
 * as the parent of a many2one. The predicate is used  by {@link gr.abiss.calipso.tiers.specifications.GenericSpecifications} for entity attributes using the 
 * {@link gr.abiss.calipso.tiers.annotation.CurrentPrincipalField}.
 * @see gr.abiss.calipso.tiers.annotation.CurrentPrincipalField
 * @see gr.abiss.calipso.tiers.specifications.GenericSpecifications
 */
public class CurrentPrincipalPredicateFactory<T extends Serializable> implements IPredicateFactory<T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CurrentPrincipalPredicateFactory.class);

	public CurrentPrincipalPredicateFactory() {
	}


	/**
	 * @see gr.abiss.calipso.uischema.jpa.search.specifications.IPredicateFactory#addPredicate(javax.persistence.criteria.Root,
	 *      javax.persistence.criteria.CriteriaBuilder, java.lang.String,
	 *      java.lang.Class, java.lang.String[])
	 */
	@Override
	public Predicate getPredicate(Root<Persistable> root, CriteriaBuilder cb, String propertyName, Class fieldType,
			String[] propertyValues) {
		if (!AbstractPersistable.class.isAssignableFrom(fieldType)) {
			LOGGER.warn("Non-Entity type for property '" + propertyName + "': " + fieldType.getName());
		}
		// ignore given values, enforce current principal
		ICalipsoUserDetails userDetails = SecurityUtil.getPrincipal();
		Path<T> parentId = root.<AbstractPersistable> get(propertyName).<T> get("id");

		LOGGER.info("Creating predicate for current principal: " + userDetails);
		Predicate predicate = cb.equal(parentId, userDetails.getId());
		
		return predicate;
	}
}