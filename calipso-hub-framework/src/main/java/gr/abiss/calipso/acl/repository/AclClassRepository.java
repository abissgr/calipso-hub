package gr.abiss.calipso.acl.repository;

import gr.abiss.calipso.acl.model.AclClass;
import gr.abiss.calipso.jpasearch.repository.BaseRepository;

public interface AclClassRepository extends BaseRepository<AclClass, Long> {

	AclClass findByClassName(String name);

}

