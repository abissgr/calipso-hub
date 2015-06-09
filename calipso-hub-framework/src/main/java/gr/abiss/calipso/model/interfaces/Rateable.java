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
package gr.abiss.calipso.model.interfaces;

import gr.abiss.calipso.model.entities.AbstractRating;

import java.util.List;

/**
 * Simple interface for rating POJOs that have no typed target. 
 * If you are using this interface in your persistend classes you might want to check out 
 * the provided AbstractAuditableRateable entity and RateableRepository.
 * 
 * @see gr.abiss.calipso.model.entities.AbstractAuditableRateable
 * @see gr.abiss.calipso.jpasearch.repository.RateableRepository
 */
public interface Rateable<R extends AbstractRating> {

	/**
	 * Get the average rating for this entity
	 * @return the average rating
	 */
	public Float getRating();

	/**
	 * Set the average rating for this entity
	 */
	public void setRating(Float rating);

	/**
	 * Get the number of ratings for this entity
	 */
	public Integer getRatingsSize();

	/**
	 * Set the number of ratings for this entity
	 */
	public void setRatingsSize(Integer ratingsSize);

	/**
	 * Get the ratings for this entity that have been created by the current user
	 */
	public List<R> getCurrentUserRatings();

	/**
	 * Get the ratings for this entity that have been created by the current user
	 */
	public void setCurrentUserRatings(List<R> currentUserRatings);

}