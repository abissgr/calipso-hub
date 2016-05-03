package gr.abiss.calipso.repository.acl;

import gr.abiss.calipso.model.acl.AclSid;
import gr.abiss.calipso.tiers.repository.ModelRepository;

public interface AclSidRepository extends ModelRepository<AclSid, Long> {

	AclSid findBySid(String sid);

}

