package gr.abiss.calipso.jpasearch.service.impl;

import gr.abiss.calipso.model.acl.AclObjectIdentity;
import gr.abiss.calipso.jpasearch.repository.acl.AclObjectIdentityRepository;
import gr.abiss.calipso.jpasearch.service.acl.AclObjectIdentityService;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

@Named("aclObjectIdentityService")
@Transactional(readOnly = true)
public class AclObjectIdentityServiceImpl
		extends
		GenericServiceImpl<AclObjectIdentity, Long, AclObjectIdentityRepository>
		implements AclObjectIdentityService {

	@Override
	@Inject
	public void setRepository(
			AclObjectIdentityRepository aclObjectIdentityRepository) {
		super.setRepository(aclObjectIdentityRepository);
	}

}

