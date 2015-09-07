package gr.abiss.calipso.model.entities;

import gr.abiss.calipso.model.base.AbstractSystemUuidPersistable;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Abstract base class for all persistent entities representing a rating
 */
@MappedSuperclass
public abstract class AbstractRating<U extends AbstractSystemUuidPersistable> extends AbstractAuditable<U>{

	@Column(name = "rating")
	private Float rating;

	@Column(name = "comment", length=2000)
	private String comment;

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.appendSuper(super.toString())
			.append("rating", this.getRating())
			.append("comment", this.getComment())
			.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (null == obj) {
			return false;
		}

		if (this == obj) {
			return true;
		}
		if (!(obj instanceof AbstractRating)) {
			return false;
		}
		AbstractRating that = (AbstractRating) obj;
		return null == this.getId() ? false : this.getId().equals(that.getId());
	}


	public Float getRating() {
		return this.rating;
	}

	public void setRating(Float rating) {
		this.rating = rating;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
