package gr.abiss.calipso.model.base;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * Resource categories are hierarchical, have aliases and can be used as tags
 */
@MappedSuperclass
public class ResourceCategory<T extends ResourceCategory<T>> extends AuditableResource {

	private static final long serialVersionUID = -1329254539598110186L;

	@JsonIgnore
	@ManyToOne(/* cascade=CascadeType.ALL, */fetch = FetchType.EAGER)
	@JoinColumn(name = "same_as", referencedColumnName = "id", nullable = true)
	private T sameAs;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "parent", referencedColumnName = "id", nullable = true)
	private T parent;

	@JsonIgnore
	@OneToMany(mappedBy = "parent", /* cascade=CascadeType.ALL, */ fetch=FetchType.LAZY)
	private List<T> children = new ArrayList<T>(0);
	
	public T getSameAs() {
		return sameAs;
	}


	public void setSameAs(T sameAs) {
		this.sameAs = sameAs;
	}


	@Override
	public boolean equals(Object obj) {
		if (null == obj) {
			return false;
		}

		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ResourceCategory<?>)) {
			return false;
		}
		T that = (T) obj;
		return null == this.getId() ? false : this.getId().equals(that.getId());
	}

}
