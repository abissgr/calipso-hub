package org.springframework.security.acls.jpa;

import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.jpa.dao.AclDao;
import org.springframework.security.acls.jpa.dao.SidDao;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.AlreadyExistsException;
import org.springframework.security.acls.model.ChildrenExistException;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * A JPA based implementation of the MutableAclService. Uses the AclDao and
 * SidDao classes to perform most work
 * 
 * @author cwarren
 * 
 */

@Transactional
@Service
public class JpaMutableAclService implements MutableAclService {

  @PersistenceContext
  EntityManager em;

  @Autowired
  AclDao aclDao;

  @Autowired
  SidDao sidDao;

  @Override
  public MutableAcl createAcl(ObjectIdentity objectIdentity) throws AlreadyExistsException {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    Sid owner = sidDao.getSid(auth);
    return aclDao.create(objectIdentity, null, owner, true);
  }

  @Override
  public void deleteAcl(ObjectIdentity objectIdentity, boolean deleteChildren) throws ChildrenExistException {
    aclDao.delete(objectIdentity, deleteChildren);
  }

  @Override
  public MutableAcl updateAcl(MutableAcl acl) throws NotFoundException {
    return aclDao.update(acl);
  }

  @Override
  public List<ObjectIdentity> findChildren(ObjectIdentity parentIdentity) {
    return aclDao.findChildren(parentIdentity);
  }

  @Override
  public Acl readAclById(ObjectIdentity object) throws NotFoundException {
    return aclDao.read(object);
  }

  @Override
  public Acl readAclById(ObjectIdentity object, List<Sid> sids) throws NotFoundException {
    return aclDao.read(object, sids);
  }

  @Override
  public Map<ObjectIdentity, Acl> readAclsById(List<ObjectIdentity> objects) throws NotFoundException {
    return aclDao.read(objects);
  }

  @Override
  public Map<ObjectIdentity, Acl> readAclsById(List<ObjectIdentity> objects, List<Sid> sids) throws NotFoundException {
    return aclDao.read(objects, sids);
  }

}
