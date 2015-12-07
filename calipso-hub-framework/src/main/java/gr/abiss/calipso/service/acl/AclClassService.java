package gr.abiss.calipso.service.acl;

import gr.abiss.calipso.jpasearch.service.GenericService;
import gr.abiss.calipso.model.acl.AclClass;

public interface AclClassService extends GenericService<AclClass, Long> {

	AclClass findByClassName(String name);

}

