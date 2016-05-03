package gr.abiss.calipso.service.impl.acl;

import gr.abiss.calipso.jpasearch.service.impl.AbstractAclAwareServiceImpl;
import gr.abiss.calipso.model.acl.AclEntry;
import gr.abiss.calipso.repository.acl.AclEntryRepository;
import gr.abiss.calipso.service.acl.AclEntryService;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

@Named("aclEntryService")
@Transactional(readOnly = true)
public class AclEntryServiceImpl extends
		AbstractAclAwareServiceImpl<AclEntry, Long, AclEntryRepository> implements
		AclEntryService {

}

