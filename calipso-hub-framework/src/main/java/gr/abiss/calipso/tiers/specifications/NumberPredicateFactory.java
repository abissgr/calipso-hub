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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Persistable;
import org.springframework.util.NumberUtils;

public class NumberPredicateFactory<T extends Number> implements IPredicateFactory<T> {

	private final Class<T> type;

	@SuppressWarnings("unused")
	private NumberPredicateFactory() {
		this.type = null;
	}

	public NumberPredicateFactory(Class<T> type) {
		this.type = type;
	}

	/**
	 * @see gr.abiss.calipso.uischema.jpa.search.specifications.IPredicateFactory#addPredicate(javax.persistence.criteria.Root,
	 *      javax.persistence.criteria.CriteriaBuilder, java.lang.String,
	 *      java.lang.Class, java.lang.String[])
	 */
	@Override
	public Predicate getPredicate(Root<Persistable> root, CriteriaBuilder cb, String propertyName, Class fieldType,
			String[] propertyValues) {
		Predicate predicate = null;
		if (!Number.class.isAssignableFrom(fieldType)) {
			throw new IllegalArgumentException(fieldType + " is not a subclass of Number for field: " + propertyName);
		}

		if (propertyValues.length == 1) {
			predicate = cb.equal(root.<T> get(propertyName), propertyValues[0]);
		} else if (propertyValues.length == 2) {
			T from = NumberUtils.parseNumber(propertyValues[0], this.type);
			T to = NumberUtils.parseNumber(propertyValues[1], this.type);
			Predicate predicate1 = cb.ge(root.<T> get(propertyName), from);
			Predicate predicate2 = cb.le(root.<T> get(propertyName), to);
			predicate = cb.and(predicate1, predicate2);
			// criteriaQuery.where(criteriaBuilder.and(predicate1,
			// predicate2));
			// predicate = cb.between(root.<T> get(propertyName), (Integer)
			// from, (Integer) to);
		}
		return predicate;
		// root...addStringSecification(personRoot, query,
		// cb, propertyName, searchTerms.get(propertyName));

	}
}