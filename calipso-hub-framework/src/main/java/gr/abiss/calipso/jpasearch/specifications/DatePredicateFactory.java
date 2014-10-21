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
	 * @see gr.abiss.calipso.jpasearch.jpa.search.specifications.IPredicateFactory#addPredicate(javax.persistence.criteria.Root,
	 *      javax.persistence.criteria.CriteriaBuilder, java.lang.String,
	 *      java.lang.Class, java.lang.String[])
	 */
	@Override
	public Predicate getPredicate(Root<Persistable> root, CriteriaBuilder cb, String propertyName, Class fieldType,
			String[] propertyValues) {
		Predicate predicate = null;
		if (!Date.class.isAssignableFrom(fieldType)) {
			throw new IllegalArgumentException(fieldType
					+ " is not a subclass of java.util.Date for field: " + propertyName);
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