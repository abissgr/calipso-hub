package gr.abiss.calipso.service.impl.acl;

import gr.abiss.calipso.jpasearch.service.impl.GenericServiceImpl;
import gr.abiss.calipso.model.acl.AclSid;
import gr.abiss.calipso.repository.acl.AclSidRepository;
import gr.abiss.calipso.service.acl.AclSidService;

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

