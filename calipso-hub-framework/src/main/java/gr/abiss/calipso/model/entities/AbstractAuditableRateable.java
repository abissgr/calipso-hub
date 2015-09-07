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
package gr.abiss.calipso.model.entities;

import gr.abiss.calipso.model.base.AbstractSystemUuidPersistable;
import gr.abiss.calipso.model.interfaces.Rateable;
import gr.abiss.calipso.model.serializers.DoublePlacesFloatSerializer;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Abstract base class for persistent entities that can be rated.
 * If you are extending this class  you might want to check out 
 * the provided RateableRepository.
 * 
 * @see gr.abiss.calipso.jpasearch.repository.RateableRepository
 */
@MappedSuperclass
public abstract class AbstractAuditableRateable<U extends AbstractSystemUuidPersistable, R extends AbstractRating> 
	extends AbstractAuditable<U> 
	implements Rateable<R>{

	private static final long serialVersionUID = 8809874829822002089L;

	@Transient
	private List<R> currentUserRatings;

	@JsonSerialize(using = DoublePlacesFloatSerializer.class)
	@Column(name = "rating")
	private Float rating = new Float(0);

	@Column(name = "ratings_size")
	private Integer ratingsSize = new Integer(0);


	@Override
	public String toString() {
		return new ToStringBuilder(this).appendSuper(super.toString()).toString();
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<R> getCurrentUserRatings() {
		return currentUserRatings;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCurrentUserRatings(List<R> currentUserRatings) {
		this.currentUserRatings = currentUserRatings;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Float getRating() {
		return rating;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setRating(Float rating) {
		this.rating = rating;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer getRatingsSize() {
		return ratingsSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setRatingsSize(Integer ratingsSize) {
		
		this.ratingsSize = ratingsSize;
	}

	
}
