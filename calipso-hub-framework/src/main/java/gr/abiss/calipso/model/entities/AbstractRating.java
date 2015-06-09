package gr.abiss.calipso.model.entities;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * Abstract base class for all persistent entities representing a rating
 */
@MappedSuperclass
public abstract class AbstractRating<U extends AbstractPersistable> extends AbstractAuditable<U>{

	@Column(name = "rating")
	private Float rating;

	@Column(name = "comment", length=2000)
	private String comment;


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
