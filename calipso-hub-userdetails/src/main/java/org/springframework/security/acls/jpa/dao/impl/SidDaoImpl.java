package org.springframework.security.acls.jpa.dao.impl;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.jpa.dao.AclDao;
import org.springframework.security.acls.jpa.dao.SidDao;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class SidDaoImpl implements SidDao {
  @PersistenceContext
  private EntityManager em;

  @Autowired
  AclDao aclDao;
  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.smartgrid.security.dao.impl.SGSidDao#getSid(org.springframework.security
   * .core.Authentication)
   */
  public DaoBackedSid getSid(Authentication authentication) {
    if (authentication == null) {
      throw new IllegalArgumentException();
    }
    if (authentication.getPrincipal() instanceof UserDetails) {
      return getSid(true, ((UserDetails) authentication.getPrincipal()).getUsername());
    } else {
      return getSid(true, authentication.getPrincipal().toString());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see gov.smartgrid.security.dao.impl.SGSidDao#getSid(java.lang.Boolean,
   * java.lang.String)
   */
  public DaoBackedSid getSid(Boolean principal, String id) {
    return getSid(new DaoBackedSid(principal, id));
  }

  @Override
  public DaoBackedSid getSid(Sid sid) {
    if (sid instanceof DaoBackedSid) {
      return getSid((DaoBackedSid) sid);
    }
    if (sid instanceof GrantedAuthoritySid) {
      return getSid(false, ((GrantedAuthoritySid) sid).getGrantedAuthority());
    }
    if (sid instanceof PrincipalSid) {
      return getSid(true, ((PrincipalSid) sid).getPrincipal());
    }
    // Unknown sid type.
    throw new IllegalArgumentException(String.format("%s is an unknown Sid type", sid.getClass()));
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.smartgrid.security.dao.impl.SGSidDao#getSid(gov.smartgrid.security.
   * dao.impl.SGSidDaoImpl.SGSid)
   */
  public DaoBackedSid getSid(DaoBackedSid sid) {
    if (sid.getId() != null)
    { // allready complete so return it.
      return sid;
    }
    Query q = em.createNativeQuery("INSERT IGNORE INTO acl_sid (principal, sid) VALUES (:principal, :sid)");
    q.setParameter("principal", sid.isPrincipal());
    q.setParameter("sid", sid.getSid());
    q.executeUpdate();

    q = em.createNativeQuery("SELECT id FROM acl_sid WHERE principal=:principal AND sid=:sid");
    q.setParameter("principal", sid.isPrincipal());
    q.setParameter("sid", sid.getSid());
    Number n = (Number) q.getSingleResult();
    sid.setId(n.longValue());
    return sid;
  }

  public DaoBackedSid getSid(Long id) {
    Query q = em.createNativeQuery("SELECT principal, sid FROM acl_sid WHERE id=:id");
    q.setParameter("id", id);
    try {
      Object[] result = (Object[]) q.getSingleResult();
      return new DaoBackedSid(id, (Boolean) result[0], result[1].toString());
    } catch (NoResultException e) {
      throw new NotFoundException("No sid with id of " + id);
    }

  }

  @Override
  public List<Sid> getPrincipalSidsFor(List<Permission> perms, ObjectIdentity objectIdentity) {
    List<Sid> result = new ArrayList<Sid>();
    DaoBackedSid sid = null;
    List<Sid> param = new ArrayList<Sid>();
    param.add(sid);
    MutableAcl acl = aclDao.read(objectIdentity);
    Query q = em.createNativeQuery( "SELECT id, sid FROM acl_sid WHERE principal=TRUE" );
    for (Object o : q.getResultList() )
    {
      Object[] vals = (Object[]) o;
      sid = new DaoBackedSid( ((Number)vals[0]).longValue(),true, vals[1].toString() );
      param.set(0, sid);
      try {
      if (acl.isGranted(perms, param,  true ))
      {
        result.add( sid );
      }
      }
      catch (NotFoundException e)
      {
        // ignore this exception
      }

    }
    return result;
  }
}
