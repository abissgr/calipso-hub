package gr.abiss.calipso.jpasearch.repository.acl;

import gr.abiss.calipso.model.acl.AclClass;
import gr.abiss.calipso.jpasearch.repository.BaseRepository;

public interface AclClassRepository extends BaseRepository<AclClass, Long> {

	AclClass findByClassName(String name);

}

