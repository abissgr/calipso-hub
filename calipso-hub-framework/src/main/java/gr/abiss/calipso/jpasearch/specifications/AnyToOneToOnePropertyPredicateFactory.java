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
package gr.abiss.calipso.jpasearch.specifications;

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
 * A predicates for many2one members implementing org.springframework.data.domain.Persistable
 */
public class AnyToOneToOnePropertyPredicateFactory<T extends Serializable> implements IPredicateFactory<T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AnyToOneToOnePropertyPredicateFactory.class);

	public AnyToOneToOnePropertyPredicateFactory() {
	}


	/**
	 * @see gr.abiss.calipso.jpasearch.jpa.search.specifications.IPredicateFactory#addPredicate(javax.persistence.criteria.Root,
	 *      javax.persistence.criteria.CriteriaBuilder, java.lang.String,
	 *      java.lang.Class, java.lang.String[])
	 */
	@Override
	public Predicate getPredicate(Root<Persistable> root, CriteriaBuilder cb, String propertyName, Class fieldType,
			String[] propertyValues) {
		LOGGER.info("getPredicate, propertyName: " + propertyName + ", propertyValues: " + propertyValues);

		Predicate predicate = null;
		String[] pathSteps = propertyName.split("\\.");
		if (propertyValues.length > 0) {
			Path<Persistable> basePath = root.<Persistable> get(pathSteps[0]);
			for(int i = 1; i < pathSteps.length; i++){
				basePath = basePath.<Persistable> get(pathSteps[i]);
				LOGGER.info("getPredicate, adding path step: " + pathSteps[i] + ", new basePath: " + basePath);
			}
			//Path<T> parentId = basePath.<T> get("id");
			predicate = cb.equal(basePath, propertyValues[0]);
		}
		return predicate;
	}
}