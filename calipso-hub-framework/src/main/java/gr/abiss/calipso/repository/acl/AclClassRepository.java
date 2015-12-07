package gr.abiss.calipso.repository.acl;

import gr.abiss.calipso.jpasearch.repository.BaseRepository;
import gr.abiss.calipso.model.acl.AclClass;

public interface AclClassRepository extends BaseRepository<AclClass, Long> {

	AclClass findByClassName(String name);

}

