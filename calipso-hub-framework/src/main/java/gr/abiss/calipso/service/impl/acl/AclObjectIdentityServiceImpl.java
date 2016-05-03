package gr.abiss.calipso.service.impl.acl;

import gr.abiss.calipso.jpasearch.service.impl.AbstractAclAwareServiceImpl;
import gr.abiss.calipso.model.acl.AclObjectIdentity;
import gr.abiss.calipso.repository.acl.AclObjectIdentityRepository;
import gr.abiss.calipso.service.acl.AclObjectIdentityService;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

@Named("aclObjectIdentityService")
@Transactional(readOnly = true)
public class AclObjectIdentityServiceImpl
		extends
		AbstractAclAwareServiceImpl<AclObjectIdentity, Long, AclObjectIdentityRepository>
		implements AclObjectIdentityService {

}

