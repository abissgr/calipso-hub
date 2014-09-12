package gr.abiss.calipso.acl.service.impl;

import gr.abiss.calipso.acl.model.AclObjectIdentity;
import gr.abiss.calipso.acl.repository.AclObjectIdentityRepository;
import gr.abiss.calipso.acl.service.AclObjectIdentityService;
import gr.abiss.calipso.jpasearch.service.impl.GenericServiceImpl;

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

