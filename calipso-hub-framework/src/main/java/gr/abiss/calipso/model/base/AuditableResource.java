package gr.abiss.calipso.model.base;

import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.entities.AbstractAuditable;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;


/**
 * A base class for auditable resource entities
 */

@MappedSuperclass
public abstract class AuditableResource extends AbstractAuditable<User> {

	/**
	 * The HTTP URL of the resource, excluding the protocol, domain and port. Starts with a slash. 
	 */
	@Column(name = "name", nullable = false)
	private String name;
	
	/**
	 * The HTTP URL of the resource, excluding the protocol, domain and port. Starts with a slash. 
	 */
	@Column(name = "path", nullable = false)
	private String path;
}
