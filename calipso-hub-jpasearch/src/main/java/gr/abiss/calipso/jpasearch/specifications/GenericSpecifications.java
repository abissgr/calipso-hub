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

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;


/**
 * A generic specifications class that builds predicates for any implementation of 
 * org.springframework.data.domain.Persistable
 */
public class GenericSpecifications {

	private static final Logger LOGGER = LoggerFactory.getLogger(GenericSpecifications.class);

	private static final HashMap<String, Field> FIELD_CACHE = new HashMap<String, Field>();
	private static final String SEARCH_MODE = "_searchmode";

	private static final ManyToOnePredicateFactory manyToOnePredicateFactory = new ManyToOnePredicateFactory();

	private static final StringPredicateFactory stringPredicateFactory = new StringPredicateFactory();
	private static final BooleanPredicateFactory booleanPredicateFactory = new BooleanPredicateFactory();
	private static final DatePredicateFactory datePredicateFactory = new DatePredicateFactory();

	private static final NumberPredicateFactory<Short> shortPredicateFactory = new NumberPredicateFactory<Short>(Short.class);
	private static final NumberPredicateFactory<Integer> integerPredicateFactory = new NumberPredicateFactory<Integer>(Integer.class);
	private static final NumberPredicateFactory<Long> longPredicateFactory = new NumberPredicateFactory<Long>(Long.class);
	private static final NumberPredicateFactory<Double> doublePredicateFactory = new NumberPredicateFactory<Double>(Double.class);

	private static final HashMap<Class, IPredicateFactory> factoryForClassMap = new HashMap<Class, IPredicateFactory>();

	protected static final String OR = "OR";

	static {
		factoryForClassMap.put(String.class, stringPredicateFactory);
		factoryForClassMap.put(Boolean.class, booleanPredicateFactory);
		factoryForClassMap.put(Date.class, datePredicateFactory);

		factoryForClassMap.put(Short.class, shortPredicateFactory);
		factoryForClassMap.put(Integer.class, integerPredicateFactory);
		factoryForClassMap.put(Long.class, longPredicateFactory);
		factoryForClassMap.put(Double.class, doublePredicateFactory);
	}

	private static IPredicateFactory<?> getPredicateFactoryForClass(Class clazz) {
		if (clazz.isEnum()) {
			return new EnumStringPredicateFactory(clazz);
		} else if (Persistable.class.isAssignableFrom(clazz)) {
			return manyToOnePredicateFactory;
		} else {
			return factoryForClassMap.get(clazz);
		}
	}

	private static Field getField(Class<?> clazz, String fieldName) {
		Field field = null;
		String key = clazz.getName() + "#" + fieldName;
		field = FIELD_CACHE.get(key);

		// find it if not cached
		if (field == null && !FIELD_CACHE.containsKey(key)) {
			Class<?> tmpClass = clazz;
			do {
				for (Field tmpField : tmpClass.getDeclaredFields()) {
					String candidateName = tmpField.getName();
					if (candidateName.equals(fieldName)) {
						// field.setAccessible(true);
						FIELD_CACHE.put(key, tmpField);
						field = tmpField;
						break;
					}
				}
				tmpClass = tmpClass.getSuperclass();
			} while (tmpClass != null && field == null);
		}
		if (field == null) {
			LOGGER.warn("Field '" + fieldName + "' not found on class " + clazz);
			// HashMap handles null values so we can use containsKey to cach
			// invalid fields and hence skip the reflection scan
			FIELD_CACHE.put(key, null);
		}
		return field;

		// throw new RuntimeException("Field '" + fieldName +
		// "' not found on class " + clazz);
	}

	/**
	 * Dynamically create a specification for the given class and search
	 * parameters.
	 * 
	 * @param searchTerm
	 * @return
	 */
	public static Specification matchAll(final Class clazz, final Map<String, String[]> searchTerms) {

		LOGGER.info("matchAll, entity: " + clazz.getSimpleName()
				+ ", searchTerms: " + searchTerms);
		return new Specification<Persistable>() {
			@Override
			public Predicate toPredicate(Root<Persistable> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				LinkedList<Predicate> predicates = new LinkedList<Predicate>();
				Predicate predicate;
				if (!CollectionUtils.isEmpty(searchTerms)) {
					Set<String> propertyNames = searchTerms.keySet();
					for (String propertyName : propertyNames) {
						Field field = GenericSpecifications.getField(clazz, propertyName);
						if (field != null) {
							Class fieldType = field.getType();
							String[] propertyValues = searchTerms.get(propertyName);
							IPredicateFactory predicateFactory = getPredicateFactoryForClass(fieldType);
							if (predicateFactory != null) {
								predicates.add(predicateFactory.getPredicate(root, cb, propertyName, fieldType, propertyValues));
							}

						}
					}
				}
				if (searchTerms.containsKey(SEARCH_MODE)
						&& searchTerms.get(SEARCH_MODE)[0].equalsIgnoreCase(OR)) {
					predicate = cb.or(predicates.toArray(new Predicate[predicates.size()]));
				}
				else{
					predicate = cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return predicate;
			}



			private String getLikePattern(final String searchTerm) {
				StringBuffer pattern = new StringBuffer();
				pattern.append("%");
				pattern.append(searchTerm.toLowerCase());
				pattern.append("%");
				return pattern.toString();
			}
		};
	}
}
