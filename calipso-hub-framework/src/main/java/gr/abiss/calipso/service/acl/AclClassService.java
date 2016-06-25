package gr.abiss.calipso.service.acl;

import gr.abiss.calipso.model.acl.AclClass;
import gr.abiss.calipso.tiers.service.GenericService;

public interface AclClassService extends GenericService<AclClass, Long> {

	AclClass findByClassName(String name);

}

