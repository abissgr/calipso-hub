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

import gr.abiss.calipso.jpasearch.annotation.UserDetailsCriterion;
import gr.abiss.calipso.jpasearch.model.structuredquery.Restriction;
import gr.abiss.calipso.userDetails.util.SecurityUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;


/**
 * A generic specifications class that builds predicates for any implementation of 
 * org.springframework.data.domain.Persistable
 */
public class GenericSpecifications {

	static final Logger LOGGER = LoggerFactory
			.getLogger(GenericSpecifications.class);

	private static final HashMap<String, Field> FIELD_CACHE = new HashMap<String, Field>();
	protected static final String SEARCH_MODE = "_searchmode";

	private static final StringPredicateFactory stringPredicateFactory = new StringPredicateFactory();
	private static final BooleanPredicateFactory booleanPredicateFactory = new BooleanPredicateFactory();
	private static final DatePredicateFactory datePredicateFactory = new DatePredicateFactory();

	private static final NumberPredicateFactory<Short> shortPredicateFactory = new NumberPredicateFactory<Short>(Short.class);
	private static final NumberPredicateFactory<Integer> integerPredicateFactory = new NumberPredicateFactory<Integer>(Integer.class);
	private static final NumberPredicateFactory<Long> longPredicateFactory = new NumberPredicateFactory<Long>(Long.class);
	private static final NumberPredicateFactory<Double> doublePredicateFactory = new NumberPredicateFactory<Double>(Double.class);

	private static final AnyToOnePredicateFactory anyToOnePredicateFactory = new AnyToOnePredicateFactory();
	private static final CurrentPrincipalPredicateFactory currentPrincipalPredicateFactory = new CurrentPrincipalPredicateFactory();
	
	private static final HashMap<Class, IPredicateFactory> factoryForClassMap = new HashMap<Class, IPredicateFactory>();

	protected static final String OR = "OR";
	protected static final String AND = "AND";
	private static List<String> IGNORED_FIELD_NAMES;

	static {
		factoryForClassMap.put(String.class, stringPredicateFactory);
		factoryForClassMap.put(Boolean.class, booleanPredicateFactory);
		factoryForClassMap.put(Date.class, datePredicateFactory);

		factoryForClassMap.put(Short.class, shortPredicateFactory);
		factoryForClassMap.put(Integer.class, integerPredicateFactory);
		factoryForClassMap.put(Long.class, longPredicateFactory);
		factoryForClassMap.put(Double.class, doublePredicateFactory);
		
		// init ignore list
		String[] ignoredFieldNames = {"page", "direction", "size"};
		IGNORED_FIELD_NAMES = new ArrayList<String>(ignoredFieldNames.length);
		for(int i = 0; i < ignoredFieldNames.length; i++){
			IGNORED_FIELD_NAMES.add(ignoredFieldNames[i]);
		}
	}

	private static IPredicateFactory<?> getPredicateFactoryForClass(Field field) {
		Class clazz = field.getType();
		if (clazz.isEnum()) {
			return new EnumStringPredicateFactory(clazz);
		} else if (Persistable.class.isAssignableFrom(clazz)) {
			if (field.isAnnotationPresent(UserDetailsCriterion.class)) {
				return currentPrincipalPredicateFactory;
			}
			else{
				return anyToOnePredicateFactory;
			}
		} else {
			return factoryForClassMap.get(clazz);
		}
	}

	private static Field getField(Class<?> clazz, String fieldName) {
		Field field = null;
		if(!IGNORED_FIELD_NAMES.contains(fieldName)){

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
			// throw new RuntimeException("Field '" + fieldName +
			// "' not found on class " + clazz);
		}

		return field;
	}

	protected static Predicate getPredicate(final Class clazz,
			final Map<String, String[]> searchTerms, Root<Persistable> root,
			CriteriaBuilder cb) {
		LinkedList<Predicate> predicates = new LinkedList<Predicate>();
		Predicate predicate;
		if (!CollectionUtils.isEmpty(searchTerms)) {
			Set<String> propertyNames = searchTerms.keySet();
			// put aside nested AND/OR param groups
			NestedJunctions junctions = new NestedJunctions();
			for (String propertyName : propertyNames) {
				String[] values = searchTerms.get(propertyName);
				if (!junctions.addIfNestedJunction(propertyName, values)) {
					addPredicate(clazz, root, cb, predicates, values, propertyName);
				}
			}
			// add nested AND/OR param groups
			Map<String, Map<String, String[]>> andJunctions = junctions.getAndJunctions();
			addJunctionedParams(clazz, root, cb, predicates, andJunctions, AND);
			Map<String, Map<String, String[]>> orJunctions = junctions.getOrJunctions();
			addJunctionedParams(clazz, root, cb, predicates, orJunctions, OR);
		}
		if (searchTerms.containsKey(SEARCH_MODE)
				&& searchTerms.get(SEARCH_MODE)[0].equalsIgnoreCase(OR)) {
			predicate = cb.or(predicates.toArray(new Predicate[predicates
					.size()]));
		} else {
			predicate = cb.and(predicates.toArray(new Predicate[predicates
					.size()]));
		}
		return predicate;
	}

	protected static void addJunctionedParams(final Class clazz,
			Root<Persistable> root, CriteriaBuilder cb,
			LinkedList<Predicate> predicates,
			Map<String, Map<String, String[]>> andJunctions, String mode) {
		if (!CollectionUtils.isEmpty(andJunctions)) {
			String[] searchMode = { mode };
			for (Map<String, String[]> params : andJunctions.values()) {
				params.put(SEARCH_MODE, searchMode);
				Predicate nestedPredicate = getPredicate(clazz, params, root,
						cb);
				if (nestedPredicate != null) {
					predicates.add(nestedPredicate);
				}
			}
		}
	}

	@Deprecated
	protected static Predicate getPredicate(final Class clazz,
			final Restriction searchTerms, Root<Persistable> root,
			CriteriaBuilder cb) {
		LinkedList<Predicate> predicates = new LinkedList<Predicate>();
		Predicate predicate;
		// process child restrictions
		if (!CollectionUtils.isEmpty(searchTerms.getRestrictions())) {
			for (Restriction restriction : searchTerms.getRestrictions()) {
				predicates.add(getPredicate(clazz, restriction, root, cb));
			}
		}
		// process main restriction
		if (StringUtils.isNotBlank(searchTerms.getField())) {
			String propertyName = searchTerms.getField();
			addPredicate(clazz, root, cb, predicates, searchTerms.getValues()
					.toArray(new String[searchTerms.getValues().size()]),
					propertyName);
		}
		if (searchTerms.getJunction().equals(Restriction.Junction.OR)) {
			predicate = cb.or(predicates.toArray(new Predicate[predicates
					.size()]));
		} else {
			predicate = cb.and(predicates.toArray(new Predicate[predicates
					.size()]));
		}
		return predicate;
	}


	protected static void addPredicate(final Class clazz,
			Root<Persistable> root, CriteriaBuilder cb,
			LinkedList<Predicate> predicates, String[] propertyValues,
			String propertyName) {
		Field field = GenericSpecifications.getField(clazz,
				propertyName);
		if (field != null) {
			LOGGER.info("addPredicate, property: " + propertyName);
			Class fieldType = field.getType();
			IPredicateFactory predicateFactory = getPredicateFactoryForClass(field);
			LOGGER.info("addPredicate, predicateFactory: " + predicateFactory);
			if (predicateFactory != null) {
				predicates.add(predicateFactory.getPredicate(root, cb,
						propertyName, fieldType, propertyValues));
			}
		}
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
				return GenericSpecifications.getPredicate(clazz, searchTerms, root, cb);
			}



		};
	}

	/**
	 * Dynamically create a specification for the given class and restriction
	 * 
	 * @param searchTerm
	 * @return
	 */
	public static Specification matchAll(final Class clazz,
			final Restriction searchTerms) {

		LOGGER.info("matchAll, entity: " + clazz.getSimpleName()
				+ ", searchTerms: " + searchTerms);
		return new Specification<Persistable>() {
			@Override
			public Predicate toPredicate(Root<Persistable> root,
					CriteriaQuery<?> query, CriteriaBuilder cb) {
				return GenericSpecifications.getPredicate(clazz, searchTerms,
						root, cb);
			}

		};
	}
}
