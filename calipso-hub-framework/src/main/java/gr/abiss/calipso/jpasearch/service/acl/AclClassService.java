package gr.abiss.calipso.jpasearch.service.acl;

import gr.abiss.calipso.model.acl.AclClass;
import gr.abiss.calipso.jpasearch.service.GenericService;

public interface AclClassService extends GenericService<AclClass, Long> {

	AclClass findByClassName(String name);

}

