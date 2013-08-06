/**
 * Copyright (c) 2007 - 2013 www.Abiss.gr
 *
 * This file is part of Calipso, a software platform by www.Abiss.gr.
 *
 * Calipso is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Calipso is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser Public License
 * along with Calipso. If not, see http://www.gnu.org/licenses/lgpl-3.0.txt
 */
package gr.abiss.calipso.jpasearch.specifications;

import java.util.ArrayList;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Persistable;

public class EnumStringPredicateFactory implements IPredicateFactory<Enum> {

	private static final Logger LOGGER = LoggerFactory.getLogger(EnumStringPredicateFactory.class);

	private Class type;

	private EnumStringPredicateFactory() {
	}

	public EnumStringPredicateFactory(Class clazz) {
		this.type = clazz;
	}


	/**
	 * @see gr.abiss.calipso.jpasearch.jpa.search.specifications.IPredicateFactory#getPredicate(javax.persistence.criteria.Root,
	 *      javax.persistence.criteria.CriteriaBuilder, java.lang.String,
	 *      java.lang.Class, java.lang.String[])
	 */
	@Override
	public Predicate getPredicate(Root<Persistable> root, CriteriaBuilder cb, String propertyName, Class fieldType,
			String[] propertyValues) {
		if (!fieldType.isEnum()) {
			LOGGER.warn("Non-Enum type for property '" + propertyName + "': " + fieldType.getName() + ", class: " + fieldType.getClass());
		}

		ArrayList<Enum> choices = new ArrayList<Enum>(propertyValues.length);
		for (int i = 0; i < propertyValues.length; i++) {
			try{
			choices.add(Enum.valueOf((Class<Enum>) this.type, propertyValues[i]));
			}catch(Exception e){
				LOGGER.warn(
						"Invalid Enum entry '" + propertyValues[i] + "' for property '" + propertyName + "' and class "
								+ fieldType.getName(), e);
			}
		}
		Expression<? extends Enum> exp = root.<Enum> get(propertyName);
		Predicate predicate = exp.in(choices.toArray());
		return predicate;
	}
}