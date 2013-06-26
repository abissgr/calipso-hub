package gr.abiss.calipso.model.acl;

import gr.abiss.calipso.model.base.AbstractAuditable;

import javax.persistence.MappedSuperclass;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@MappedSuperclass
public abstract class Resource extends AbstractAuditable {

	/**
	 * Get a human and URL friendly id such as a username, a filename etc.
	 * 
	 * @return the friendly id.
	 */
	public abstract String getBusinessKey();

	/**
	 * The API base path e.g. "/resource" or "/users". This should return a
	 * string if the resource is retreivable from the REST API (e.g.
	 * "/users/{businessKey|id}") or null otherwise.
	 * 
	 * @return the base path string
	 */
	public abstract String getApiBasePath();

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Resource) {
			Resource other = (Resource) obj;
			EqualsBuilder builder = new EqualsBuilder();
			builder.appendSuper(super.equals(obj));
			builder.append(getBusinessKey(), other.getBusinessKey());
			return builder.isEquals();
		}
		return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.appendSuper(super.hashCode());
		builder.append(getBusinessKey());
		return builder.toHashCode();
	}

}
