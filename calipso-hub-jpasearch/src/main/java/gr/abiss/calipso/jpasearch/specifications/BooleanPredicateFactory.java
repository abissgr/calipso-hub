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

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.data.domain.Persistable;

public class BooleanPredicateFactory implements IPredicateFactory<Boolean> {

	public BooleanPredicateFactory() {
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
		if (!Boolean.class.isAssignableFrom(fieldType)) {
			throw new IllegalArgumentException(fieldType
					+ " is not a subclass of Boolean for field: "
					+ propertyName);
		}

		Boolean b = BooleanUtils.toBooleanObject(propertyValues[0]);
		if (b == null) {
			b = Boolean.FALSE;
		}

		predicate = cb.equal(root.<Boolean> get(propertyName), b);
		return predicate;
	}
}