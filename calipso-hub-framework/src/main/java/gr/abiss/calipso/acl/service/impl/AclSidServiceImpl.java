package gr.abiss.calipso.acl.service.impl;

import gr.abiss.calipso.acl.model.AclSid;
import gr.abiss.calipso.acl.repository.AclSidRepository;
import gr.abiss.calipso.acl.service.AclSidService;
import gr.abiss.calipso.jpasearch.service.impl.GenericServiceImpl;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

@Named("aclSidService")
@Transactional(readOnly = true)
public class AclSidServiceImpl extends
		GenericServiceImpl<AclSid, Long, AclSidRepository> implements
		AclSidService {

	@Override
	@Inject
	public void setRepository(
			AclSidRepository aclSidRepository) {
		super.setRepository(aclSidRepository);
	}

}

