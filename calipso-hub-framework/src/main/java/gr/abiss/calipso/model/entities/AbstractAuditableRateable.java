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
 * @see gr.abiss.calipso.repository.RateableRepository
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
