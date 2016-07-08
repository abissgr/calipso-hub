/**
 * calipso-hub-framework - A full stack, high level framework for lazy application hackers.
 * Copyright © 2005 Manos Batsis (manosbatsis gmail)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package gr.abiss.calipso.tiers.specifications;

import java.io.Serializable;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 * A predicates for many2one members implementing org.springframework.data.domain.Persistable
 */
public class AnyToOneToOnePropertyPredicateFactory<T extends Serializable> implements IPredicateFactory<T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AnyToOneToOnePropertyPredicateFactory.class);

	public AnyToOneToOnePropertyPredicateFactory() {
	}


	/**
	 * @see gr.abiss.calipso.uischema.jpa.search.specifications.IPredicateFactory#addPredicate(javax.persistence.criteria.Root,
	 *      javax.persistence.criteria.CriteriaBuilder, java.lang.String,
	 *      java.lang.Class, java.lang.String[])
	 */
	@Override
	public Predicate getPredicate(Root<Persistable> root, CriteriaBuilder cb, String propertyName, Class fieldType,
			String[] propertyValues) {
		LOGGER.info("getPredicate, propertyName: " + propertyName + ", propertyValues: " + propertyValues);

		Predicate predicate = null;
		String[] pathSteps = propertyName.split("\\.");
		if (propertyValues.length > 0) {
			Path<Persistable> basePath = root.<Persistable> get(pathSteps[0]);
			for(int i = 1; i < pathSteps.length; i++){
				basePath = basePath.<Persistable> get(pathSteps[i]);
				LOGGER.info("getPredicate, adding path step: " + pathSteps[i] + ", new basePath: " + basePath);
			}
			//Path<T> parentId = basePath.<T> get("id");
			predicate = cb.equal(basePath, propertyValues[0]);
		}
		return predicate;
	}
}