package gr.abiss.calipso.jpasearch.service.impl;

import gr.abiss.calipso.model.acl.AclEntry;
import gr.abiss.calipso.jpasearch.repository.acl.AclEntryRepository;
import gr.abiss.calipso.jpasearch.service.acl.AclEntryService;

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

