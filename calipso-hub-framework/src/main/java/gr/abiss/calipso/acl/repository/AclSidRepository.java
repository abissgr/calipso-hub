package gr.abiss.calipso.acl.repository;

import gr.abiss.calipso.acl.model.AclSid;
import gr.abiss.calipso.jpasearch.repository.BaseRepository;

public interface AclSidRepository extends BaseRepository<AclSid, Long> {

	AclSid findBySid(String sid);

}

