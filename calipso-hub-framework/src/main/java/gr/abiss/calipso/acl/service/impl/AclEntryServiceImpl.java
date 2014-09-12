package gr.abiss.calipso.acl.service.impl;

import gr.abiss.calipso.acl.model.AclEntry;
import gr.abiss.calipso.acl.repository.AclEntryRepository;
import gr.abiss.calipso.acl.service.AclEntryService;
import gr.abiss.calipso.jpasearch.service.impl.GenericServiceImpl;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

@Named("aclEntryService")
@Transactional(readOnly = true)
public class AclEntryServiceImpl extends
		GenericServiceImpl<AclEntry, Long, AclEntryRepository> implements
		AclEntryService {

	@Override
	@Inject
	public void setRepository(AclEntryRepository aclEntryRepository) {
		super.setRepository(aclEntryRepository);
	}

}

