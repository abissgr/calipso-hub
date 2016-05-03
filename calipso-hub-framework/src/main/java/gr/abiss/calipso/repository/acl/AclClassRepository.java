package gr.abiss.calipso.repository.acl;

import gr.abiss.calipso.model.acl.AclClass;
import gr.abiss.calipso.tiers.repository.ModelRepository;

public interface AclClassRepository extends ModelRepository<AclClass, Long> {

	AclClass findByClassName(String name);

}

