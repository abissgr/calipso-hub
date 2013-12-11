package gr.abiss.calipso.jpasearch.repository.acl;

import gr.abiss.calipso.jpasearch.model.acl.AclSid;
import gr.abiss.calipso.jpasearch.repository.BaseRepository;

public interface AclSidRepository extends BaseRepository<AclSid, Long> {

	AclSid findBySid(String sid);

}

