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

import java.util.Arrays;
import java.util.List;

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
	 * @see gr.abiss.calipso.uischema.jpa.search.specifications.IPredicateFactory#addPredicate(javax.persistence.criteria.Root,
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
				predicate = cb.equal(cb.lower(root.<String> get(propertyName)), val.toLowerCase());
			}
		} else {// if (propertyValues.length == 2) {
			List<String> wordList = Arrays.asList(propertyValues);
			predicate = cb.isTrue(root.<String> get(propertyName).in(wordList));
		}
		return predicate;
	}
}