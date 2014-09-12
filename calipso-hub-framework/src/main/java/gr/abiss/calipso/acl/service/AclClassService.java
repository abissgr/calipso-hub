package gr.abiss.calipso.acl.service;

import gr.abiss.calipso.acl.model.AclClass;
import gr.abiss.calipso.jpasearch.service.GenericService;

public interface AclClassService extends GenericService<AclClass, Long> {

	AclClass findByClassName(String name);

}

