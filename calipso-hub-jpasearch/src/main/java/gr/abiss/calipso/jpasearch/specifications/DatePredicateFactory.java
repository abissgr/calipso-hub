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

import java.util.Date;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Persistable;
import org.springframework.util.NumberUtils;

public class DatePredicateFactory implements IPredicateFactory<Date> {

	public DatePredicateFactory() {
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
			throw new IllegalArgumentException(fieldType
					+ " is not a subclass of Date for field: " + propertyName);
		}

		if (propertyValues.length == 1) {
			Date date = new Date(NumberUtils.parseNumber(propertyValues[0], Long.class));
			predicate = cb.equal(root.<Date> get(propertyName), date);
		} else if (propertyValues.length == 2) {
			Date from = new Date(NumberUtils.parseNumber(propertyValues[0], Long.class));
			Date to = new Date(NumberUtils.parseNumber(propertyValues[1], Long.class));
			predicate = cb.between(root.<Date> get(propertyName), from, to);
		}
		return predicate;
	}
}