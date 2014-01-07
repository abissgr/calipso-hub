package gr.abiss.calipso.jpasearch.service.acl;

import gr.abiss.calipso.userDetails.model.UserDetails;

import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.ObjectIdentityRetrievalStrategy;

/**
 * overwrite the strategy: build ObjectIdentity based on user object login
 * property, instead of Spring Security default getId() call
 */
public class UserNameRetrievalStrategy implements
		ObjectIdentityRetrievalStrategy {

	@Override
	public ObjectIdentity getObjectIdentity(Object domainObject) {
		UserDetails user = (UserDetails) domainObject;
		return new ObjectIdentityImpl(UserDetails.class, user.getUsername());
	}

}