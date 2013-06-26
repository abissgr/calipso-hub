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
package gr.abiss.calipso.jpasearch.specifications;

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
	 * @see gr.abiss.calipso.jpasearch.jpa.search.specifications.IPredicateFactory#getPredicate(javax.persistence.criteria.Root,
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