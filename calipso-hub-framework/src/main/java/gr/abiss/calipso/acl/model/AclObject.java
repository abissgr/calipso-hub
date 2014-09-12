package gr.abiss.calipso.acl.model;

import java.io.Serializable;

/**
 * An interface for entities that expose information to create and map to an
 * instance of an ACL ObjectIdentity
 * 
 * @see gr.abiss.calipso.acl.model.AclObjectIdentity
 * 
 */
public interface AclObject<IDTYPE extends Serializable, OWNERIDTYPE extends Serializable> {

	IDTYPE getIdentity();

	IDTYPE getParentIdentity();

	OWNERIDTYPE getOwner();

	Boolean getEntriesInheriting();

}