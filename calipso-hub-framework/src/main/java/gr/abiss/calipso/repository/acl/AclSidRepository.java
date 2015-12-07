package gr.abiss.calipso.repository.acl;

import gr.abiss.calipso.jpasearch.repository.BaseRepository;
import gr.abiss.calipso.model.acl.AclSid;

public interface AclSidRepository extends BaseRepository<AclSid, Long> {

	AclSid findBySid(String sid);

}

