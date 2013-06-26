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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Persistable;

public class StringPredicateFactory implements IPredicateFactory<String> {

	private static final Logger LOGGER = LoggerFactory.getLogger(StringPredicateFactory.class);

	public StringPredicateFactory() {
	}


	/**
	 * @see gr.abiss.calipso.jpasearch.jpa.search.specifications.IPredicateFactory#getPredicate(javax.persistence.criteria.Root,
	 *      javax.persistence.criteria.CriteriaBuilder, java.lang.String,
	 *      java.lang.Class, java.lang.String[])
	 */
	@Override
	public Predicate getPredicate(Root<Persistable> root, CriteriaBuilder cb, String propertyName, Class fieldType,
			String[] propertyValues) {
		if (!fieldType.equals(String.class)) {
			LOGGER.warn("Non-String type for property '" + propertyName + "': " + fieldType.getName());
		}

		Predicate predicate = null;
		if (propertyValues.length == 1) {
			String val = propertyValues[0];
			// case insensitive like?
			if (val.contains("%")) {
				predicate = cb.like(cb.lower(root.<String> get(propertyName)), val.toLowerCase());
			} else {
				predicate = cb.equal(root.<String> get(propertyName), val);
			}
		} else {// if (propertyValues.length == 2) {
			predicate = cb.between(root.<String> get(propertyName), propertyValues[0], propertyValues[1]);
		}
		return predicate;
	}
}