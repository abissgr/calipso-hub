package gr.abiss.calipso.model.cms;

import gr.abiss.calipso.model.base.ResourceCategory;

import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * Tags are used for content categorization. They are hierarchical and can have aliases
 */
@Entity
@Table(name = "content_tag")
public class Tag extends ResourceCategory<Tag> {

	private static final long serialVersionUID = -3020367940457381316L;

	@Override
	public boolean equals(Object obj) {
		if (null == obj) {
			return false;
		}

		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Tag)) {
			return false;
		}
		Tag that = (Tag) obj;
		return null == this.getId() ? false : this.getId().equals(that.getId());
	}

}
