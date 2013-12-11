package org.springframework.security.acls.jpa;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.jpa.dao.AclDao;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Sid;

/**
 * A JPA implementation of LookupStrategy that uses the AclDao implementation.
 * 
 * @author cwarren
 * 
 */
public class JpaLookupStrategy implements LookupStrategy {
  @Autowired
  AclDao aclDao;

  @Override
  public Map<ObjectIdentity, Acl> readAclsById(List<ObjectIdentity> objects, List<Sid> sids) {
    return aclDao.read(objects, sids);
  }

}
