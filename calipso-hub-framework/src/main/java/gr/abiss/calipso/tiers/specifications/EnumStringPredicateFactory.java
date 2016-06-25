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
	 * @see gr.abiss.calipso.uischema.jpa.search.specifications.IPredicateFactory#addPredicate(javax.persistence.criteria.Root,
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