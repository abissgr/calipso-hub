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

import gr.abiss.calipso.jpasearch.annotation.CurrentPrincipalIdPredicate;
import gr.abiss.calipso.userDetails.util.SecurityUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Transient;
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

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A generic specifications class that builds predicates for any implementation
 * of org.springframework.data.domain.Persistable
 */
public class GenericSpecifications {

	private static final String SIMPLE_SEARCH_PARAM_NAME = "_all";

	static final Logger LOGGER = LoggerFactory.getLogger(GenericSpecifications.class);

	private static final HashMap<String, Field> FIELD_CACHE = new HashMap<String, Field>();
	private static final HashMap<Class, List<Field>> SIMPLE_SEARCH_FIELDs_CACHE = new HashMap<Class, List<Field>>();
	protected static final String SEARCH_MODE = "_searchmode";

	private static final StringPredicateFactory stringPredicateFactory = new StringPredicateFactory();
	private static final BooleanPredicateFactory booleanPredicateFactory = new BooleanPredicateFactory();
	private static final DatePredicateFactory datePredicateFactory = new DatePredicateFactory();

	private static final NumberPredicateFactory<Short> shortPredicateFactory = new NumberPredicateFactory<Short>(
			Short.class);
	private static final NumberPredicateFactory<Integer> integerPredicateFactory = new NumberPredicateFactory<Integer>(
			Integer.class);
	private static final NumberPredicateFactory<Long> longPredicateFactory = new NumberPredicateFactory<Long>(
			Long.class);
	private static final NumberPredicateFactory<Double> doublePredicateFactory = new NumberPredicateFactory<Double>(
			Double.class);

	private static final AnyToOnePredicateFactory anyToOnePredicateFactory = new AnyToOnePredicateFactory();
	private static final AnyToOneToOnePropertyPredicateFactory anyToOneToOnePredicateFactory = new AnyToOneToOnePropertyPredicateFactory();
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
		String[] ignoredFieldNames = { "page", "direction", "properties", "size", "totalPages", "_searchmode", "_all", "totalElements" };
		IGNORED_FIELD_NAMES = new ArrayList<String>(ignoredFieldNames.length);
		for (int i = 0; i < ignoredFieldNames.length; i++) {
			IGNORED_FIELD_NAMES.add(ignoredFieldNames[i]);
		}
	}

	private static IPredicateFactory<?> getPredicateFactoryForClass(Field field) {
		Class clazz = field.getType();
		if (clazz.isEnum()) {
			return new EnumStringPredicateFactory(clazz);
		} else if (Persistable.class.isAssignableFrom(clazz)) {
			if (field.isAnnotationPresent(CurrentPrincipalIdPredicate.class)) {
				return currentPrincipalPredicateFactory;
			} else {
				return anyToOnePredicateFactory;
			}
		} else {
			return factoryForClassMap.get(clazz);
		}
	}

	public static Field getField(Class<?> clazz, String fieldName) {
		Field field = null;
		if (!IGNORED_FIELD_NAMES.contains(fieldName)) {

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
	
	// TODO: add annotation config
	public static Map<String, String[]> getSimpleSearchTerms(Class<?> clazz, String[] values) {
		Map<String, String[]> simpleSearchTerms = new HashMap<String, String[]>();
		Class<?> tmpClass = clazz;
		
		// try the cache first
		List<Field> simpleSearchFieldList = SIMPLE_SEARCH_FIELDs_CACHE.get(clazz);
		if(simpleSearchFieldList == null){
			simpleSearchFieldList = new LinkedList<Field>();
			while (tmpClass != null) {
				for (Field tmpField : tmpClass.getDeclaredFields()) {
					String fieldName = tmpField.getName();
					// if string and not excluded
					if(isAcceptableSimpleSearchFieldClass(tmpField)
							&& !tmpField.isAnnotationPresent(Transient.class)
							&& !tmpField.isAnnotationPresent(JsonIgnore.class)){

						simpleSearchTerms.put(fieldName, values);
						simpleSearchFieldList.add(tmpField);
					}
				}
				tmpClass = tmpClass.getSuperclass();
			}
			// update the cache
			SIMPLE_SEARCH_FIELDs_CACHE.put(clazz, simpleSearchFieldList);
		}
		else{
			// use the caches field list
			for (Field tmpField : simpleSearchFieldList) {
				simpleSearchTerms.put(tmpField.getName(), values);
			}
		}
		return simpleSearchTerms;
	}

	//TODO: why only enum and string?
	protected static boolean isAcceptableSimpleSearchFieldClass(Field tmpField) {
		return tmpField.getClass().isEnum() || tmpField.getType().equals(String.class);
	}


	protected static void addJunctionedParams(final Class clazz, Root<Persistable> root, CriteriaBuilder cb,
			LinkedList<Predicate> predicates, Map<String, Map<String, String[]>> andJunctions, String mode) {
		if (!CollectionUtils.isEmpty(andJunctions)) {
			String[] searchMode = { mode };
			for (Map<String, String[]> params : andJunctions.values()) {
				params.put(SEARCH_MODE, searchMode);
				// TODO
				Predicate nestedPredicate = buildRootPredicate(clazz, params, root, cb/*, true*/);
				if (nestedPredicate != null) {
					predicates.add(nestedPredicate);
				}
			}
		}
	}


	/**
	 * Dynamically create a specification for the given class and search
	 * parameters. This is the entry point for query specifications construction.
	 * @param clazz the entity type to query for
	 * @param searchTerms the search terms to match
	 * @return the result specification
	 */
	@SuppressWarnings("rawtypes")
	public static Specification<Persistable> matchAll(final Class clazz, final Map<String, String[]> searchTerms) {
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("matchAll, entity: " + clazz.getSimpleName() + ", searchTerms: " + searchTerms);
		}
		return new Specification<Persistable>() {
			@Override
			public Predicate toPredicate(@SuppressWarnings("rawtypes") Root<Persistable> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return GenericSpecifications.buildRootPredicate(clazz, searchTerms, root, cb);
			}
		};
	}

	/**
	 * Get the root predicate, either a conjunction or disjunction
	 * @param clazz the entity type to query for
	 * @param searchTerms the search terms to match
	 * @param root the criteria root
	 * @param cb the criteria builder
	 * @return the resulting predicate
	 */
	protected static Predicate buildRootPredicate(final Class clazz, final Map<String, String[]> searchTerms,
			Root<Persistable> root, CriteriaBuilder cb) {
		
		// build a list of criteria/predicates
		LinkedList<Predicate> predicates = buildSearchPredicates(clazz, searchTerms, root, cb);
		
		// wrap list in AND/OR junction
		Predicate predicate;
		if (searchTerms.containsKey(SEARCH_MODE) && searchTerms.get(SEARCH_MODE)[0].equalsIgnoreCase(OR)
				// A disjunction of zero predicates is false so...
				&& predicates.size() > 0) {
			predicate = cb.or(predicates.toArray(new Predicate[predicates.size()]));
		} else {
			predicate = cb.and(predicates.toArray(new Predicate[predicates.size()]));
		}
		
		// return the resulting junction
		return predicate;
	}
	
	/**
	 * Build the list of predicates corresponding to the given search terms
	 * @param clazz the entity type to query for
	 * @param searchTerms the search terms to match
	 * @param root the criteria root
	 * @param cb the criteria builder
	 * @return the list of predicates corresponding to the search terms
	 */
	protected static LinkedList<Predicate> buildSearchPredicates(final Class clazz, final Map<String, String[]> searchTerms,
			Root<Persistable> root, CriteriaBuilder cb) {
		
		LinkedList<Predicate> predicates = new LinkedList<Predicate>();
		
		if (!CollectionUtils.isEmpty(searchTerms)) {
			Set<String> propertyNames = searchTerms.keySet();
			// storage for nested junctions
			NestedJunctions junctions = new NestedJunctions();
			for (String propertyName : propertyNames) {
				String[] values = searchTerms.get(propertyName);
				// store if nested junction or add a predicate
				if (!junctions.addIfNestedJunction(propertyName, values)) {
					addPredicate(clazz, root, cb, predicates, values, propertyName);
				}
			}
			// add stored junctions
			Map<String, Map<String, String[]>> andJunctions = junctions.getAndJunctions();
			addJunctionedParams(clazz, root, cb, predicates, andJunctions, AND);
			Map<String, Map<String, String[]>> orJunctions = junctions.getOrJunctions();
			addJunctionedParams(clazz, root, cb, predicates, orJunctions, OR);
		}
		// return the list of predicates
		return predicates;
	}
	

	/**
	 * Add a predicate to the given list if valid
	 * @param clazz the entity type to query for
	 * @param root the criteria root
	 * @param cb the criteria builder
	 * @param predicates the list to add the predicate into
	 * @param propertyValues the predicate values
	 * @param propertyName the predicate name
	 */
	protected static void addPredicate(final Class clazz, Root<Persistable> root, CriteriaBuilder cb,
			LinkedList<Predicate> predicates, String[] propertyValues, String propertyName) {
		// dot notation only supports toOne.toOne.id
		if (propertyName.contains(".")) {
			 LOGGER.info("addPredicate, property name is a path: " +
			 propertyName);
			predicates.add(anyToOneToOnePredicateFactory.getPredicate(root, cb, propertyName, null, propertyValues));
		} else {// normal single step predicate
			Field field = GenericSpecifications.getField(clazz, propertyName);
			if (field != null) {
				LOGGER.info("addPredicate, property: " + propertyName);
				Class fieldType = field.getType();
				IPredicateFactory predicateFactory = getPredicateFactoryForClass(field);
				LOGGER.info("addPredicate, predicateFactory: " +
				predicateFactory);
				if (predicateFactory != null) {
					predicates.add(predicateFactory.getPredicate(root, cb, propertyName, fieldType, propertyValues));
				}
			}
		}
	}
}
