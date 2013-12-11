package org.springframework.security.acls.jpa.dao.impl;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.AccessControlEntryImpl;
import org.springframework.security.acls.domain.AuditLogger;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.jpa.dao.AclDao;
import org.springframework.security.acls.jpa.dao.AclEntryDao;
import org.springframework.security.acls.jpa.dao.SidDao;
import org.springframework.security.acls.jpa.dao.SidDao.DaoBackedSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.AlreadyExistsException;
import org.springframework.security.acls.model.AuditableAcl;
import org.springframework.security.acls.model.ChildrenExistException;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.acls.model.UnloadedSidException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * JPA implementaiton of the AclDao.
 * 
 * @author cwarren
 * 
 */
@Transactional
@Repository
public class AclDaoImpl implements AclDao {
  @PersistenceContext
  private EntityManager em;

  @Autowired
  private AclEntryDao aclEntryDao;

  @Autowired
  private SidDao sidDao;

  @Autowired
  private AclCache aclCache;

  @Autowired
  private AuditLogger auditLogger;

  @Autowired
  private UserDetailsService userDetailsService;

  private AclDao aclDao = this;

  private boolean foreignKeysInDatabase = true;

  private String getObjectIdentityWhereClause(String aclTableName, String classTableName, String idVarName, String classVarName) {
    return String.format("%1$s.object_id_identity=:%3$s AND %1$s.object_id_class=%2$s.id AND %2$s.class=:%4$s", aclTableName, classTableName, idVarName, classVarName);
  }

  public void setForeignKeysInDatabase(boolean b) {
    foreignKeysInDatabase = b;
  }

  public void delete(ObjectIdentity objectIdentity, boolean deleteChildren) throws ChildrenExistException {
    if (objectIdentity == null) {
      throw new IllegalArgumentException();
    }

    if (deleteChildren) {
      List<ObjectIdentity> children = findChildren(objectIdentity);
      if (children != null) {
        for (int i = 0; i < children.size(); i++) {
          delete(children.get(i), true);
        }
      }
    } else {
      if (!foreignKeysInDatabase) {
        // We need to perform a manual verification for what a FK would normally
        // do
        // We generally don't do this, in the interests of deadlock management
        List<ObjectIdentity> children = findChildren(objectIdentity);
        if (children != null) {
          throw new ChildrenExistException("Cannot delete '" + objectIdentity + "' (has " + children.size()
                        + " children)");
        }
      }
    }

    aclEntryDao.delete(objectIdentity);

    // Delete this ACL's ACEs in the acl_entry table
    Query q = em.createNativeQuery("DELETE acl_object_identity FROM acl_object_identity,acl_class WHERE " + getObjectIdentityWhereClause("acl_object_identity", "acl_class", "id", "class"));
    q.setParameter("id", objectIdentity.getIdentifier());
    q.setParameter("class", objectIdentity.getType());
    q.executeUpdate();

    // Clear the cache
    aclCache.evictFromCache(objectIdentity);
  }

  public void deleteEntries(ObjectIdentity objectIdentity, Sid sid) {
    MutableAcl acl = read(objectIdentity);
    aclEntryDao.delete(acl, sid);
    // Clear the cache
    aclCache.evictFromCache(objectIdentity);
  }

  @Override
  public MutableAcl create(ObjectIdentity object, Long parentId, Sid owner, boolean inheriting) {
    if (object == null) {
      throw new IllegalArgumentException();
    }
    DaoBackedSid sgOwner = sidDao.getSid(owner);

    Query q = em
        .createNativeQuery("INSERT IGNORE INTO acl_object_identity (object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting) values (get_acl_class_id(:class), :id, :parent, :owner, :inheriting)");
    q.setParameter("class", object.getType());
    q.setParameter("id", object.getIdentifier());
    q.setParameter("parent", parentId);
    q.setParameter("owner", sgOwner.getId());
    q.setParameter("inheriting", inheriting);
    int i = q.executeUpdate();
    if (i == 0) {
      throw new AlreadyExistsException(object.toString());
    }

    return read(object);
  }

  /**
   * Can only change parent, owner and inheriting flag.
   */
  public MutableAcl update(MutableAcl acl) throws NotFoundException {
    StringBuilder sb = new StringBuilder(
        "UPDATE acl_object_identity, acl_class SET parent_object=:parent, owner_sid=:sid, entries_inheriting=:inheriting WHERE ")
        .append(getObjectIdentityWhereClause("acl_object_identity", "acl_class", "id", "class"));
    Query q = em.createNativeQuery(sb.toString());
    
    q.setParameter("class", acl.getObjectIdentity().getType());
    q.setParameter("id", acl.getObjectIdentity().getIdentifier());
    Acl parentAcl = acl.getParentAcl();
    if (parentAcl != null && !(parentAcl instanceof MutableAcl)) {
      parentAcl = read(parentAcl.getObjectIdentity());
    }
    q.setParameter("parent", parentAcl == null ? null : ((MutableAcl) parentAcl).getId());
    q.setParameter("sid", sidDao.getSid(acl.getOwner()).getId());
    q.setParameter("inheriting", acl.isEntriesInheriting());
    if (q.executeUpdate() == 0) {
      throw new NotFoundException("Could not find " + acl);
    }
    // Clear the cache
    aclCache.evictFromCache(acl.getObjectIdentity());
    return read(acl.getObjectIdentity());
  }

  public List<ObjectIdentity> findChildren(ObjectIdentity parentIdentity) {
    StringBuilder sb = new StringBuilder("SELECT childclass.class, childacl.object_id_identity FROM acl_object_identity childacl, acl_class childclass, acl_object_identity,acl_class WHERE ")
        .append(getObjectIdentityWhereClause("acl_object_identity", "acl_class", "id", "class"))
        .append(" AND childclass.id=childacl.object_id_class AND childacl.parent_object=acl_object_identity.id");
    Query q = em.createNativeQuery(sb.toString());
    q.setParameter("id", parentIdentity.getIdentifier());
    q.setParameter("class", parentIdentity.getType());
    List<Object[]> result = q.getResultList();
    List<ObjectIdentity> retval = new ArrayList<ObjectIdentity>(result.size());
    for (Object[] obj : result) {
      retval.add(new ObjectIdentityImpl(obj[0].toString(), (Serializable) obj[1]));
    }
    return retval;
  }

  public boolean isGranted(Acl acl, List<Permission> permission, List<Sid> sids, boolean administrativeMode) throws NotFoundException, UnloadedSidException {
    if (acl instanceof MutableAcl) {
      return isGranted((MutableAcl) acl, permission, sids, administrativeMode);
    } else {
      return isGranted(read(acl.getObjectIdentity()), permission, sids, administrativeMode);
    }
  }

  /*
   * Determines authorization. The order of the permission and sid arguments is
   * extremely important! The method will iterate through each of the
   * permissions in the order specified. For each iteration, all of the sids
   * will be considered, again in the order they are presented. A search will
   * then be performed for the first AccessControlEntry object that directly
   * matches that permission:sid combination. When the first full match is found
   * (ie an ACE that has the SID currently being searched for and the exact
   * permission bit mask being search for), the grant or deny flag for that ACE
   * will prevail. If the ACE specifies to grant access, the method will return
   * true. If the ACE specifies to deny access, the loop will stop and the next
   * permission iteration will be performed. If each permission indicates to
   * deny access, the first deny ACE found will be considered the reason for the
   * failure (as it was the first match found, and is therefore the one most
   * logically requiring changes - although not always). If absolutely no
   * matching ACE was found at all for any permission, the parent ACL will be
   * tried (provided that there is a parent and isEntriesInheriting() is true.
   * The parent ACL will also scan its parent and so on. If ultimately no
   * matching ACE is found, a NotFoundException will be thrown and the caller
   * will need to decide how to handle the permission check. Similarly, if any
   * of the SID arguments presented to the method were not loaded by the ACL,
   * UnloadedSidException will be thrown.
   * 
   * Since we access the database and do not use an in memory list the
   * UnloadedSidException is never thrown
   */
  public boolean isGranted(MutableAcl acl, List<Permission> permission, List<Sid> sids, boolean administrativeMode) throws NotFoundException {
    Assert.notEmpty(permission, "Permission list may not be empty");
    Assert.notEmpty(sids, "Sid list may not be empty");
    List<DaoBackedSid> jpaSidList = new ArrayList<DaoBackedSid>(sids.size());

    // This method creates a query that will return all the ACL entries for the
    // specific permission and sid combinations. It then calls a follow separate
    // method to perform the query
    // for the specific acl. This allows us to build a single query and use it
    // for all parents as we move up the inheratance tree.

    if (!acl.isSidLoaded(sids)) {
      throw new UnloadedSidException("ACL was not loaded for one or more SID");
    }

    StringBuilder permissionList = new StringBuilder();
    for (Permission p : permission) {
      permissionList.append(p.getMask()).append(',');
    }
    permissionList.deleteCharAt(permissionList.length() - 1);

    StringBuilder sidList = new StringBuilder();
    DaoBackedSid jpaSid;
    for (Sid sid : sids) {
      jpaSid = sidDao.getSid(sid);
      jpaSidList.add(jpaSid);
      sidList.append(jpaSid.getId()).append(",");
    }
    sidList.deleteCharAt(sidList.length() - 1);
    String queryStr = String
        .format(
            "SELECT * FROM acl_entry  WHERE acl_object_identity=:oid AND sid in (%s) AND mask in (%s) ORDER BY ace_order",
            sidList.toString(), permissionList
                .toString());

    return isGranted(acl, em.createNativeQuery(queryStr), permission, jpaSidList, administrativeMode);
  }

  /**
   * This method actually performs the check specificed in the public isGranted
   * documentation. The advantage here is that if the check fails the parent can
   * be cheked in a recursive call.
   * 
   * The code for this method was lifted from the Spring AclImpl class.
   * 
   * @param acl
   *          The Acl we are checking
   * @param query
   *          The queery that extracts the proper entries.
   * @param permissions
   *          THe permissions we are looking for
   * @param sids
   *          The JpaSids that we are limiting the query to.
   * @param administrativeMode
   *          True if we are in administrative mode
   * @return True if the permission is granted, false if it is revoked.
   * @throws NotFoundException
   *           If the permission is neither granted nor revoked.
   */
  private boolean isGranted(MutableAcl acl, Query query, List<Permission> permissions, List<DaoBackedSid> sids, boolean administrativeMode) throws NotFoundException {
    // DEBUG
    // displayQueryResults(em.createNativeQuery(
    // "SELECT * from acl_object_identity where id=" + acl.getId()
    // ).getResultList());
    // displayQueryResults(em.createNativeQuery(
    // "SELECT * from acl_entry where acl_object_identity=" + acl.getId()
    // ).getResultList());
    // displayQueryResults(em.createNativeQuery(
    // "SELECT * from acl_sid ").getResultList());

    List<DbAclEntry> aceList = new ArrayList<DbAclEntry>();

    try {
      query.setParameter("oid", (acl.getId()));
      for (Object[] data : (List<Object[]>) query.getResultList()) {
        aceList.add(new DbAclEntry(data));
      }

      AccessControlEntry firstRejection = null;

      // iterate by permission and then sid
      for (Permission permission : permissions) {
        for (DaoBackedSid sid : sids) {
          // Attempt to find exact match for this permission mask and SID
          boolean scanNextSid = true;

          for (DbAclEntry ace : aceList) {
            if (ace.getMask() == permission.getMask() && ace.getSidId() == sid.getId()) {
              if (ace.isGranting()) {
                // Success

                if (!administrativeMode) {
                  auditLogger.logIfNeeded(true, new AccessControlEntryImpl(ace.getId(), acl, sid, permission, ace.isGranting(), ace.isAuditSuccess(), ace.isAuditFailure()));
                }
                return true;
              } else {
                // Failure for this permission, so stop search
                // We will see if they have a different permission
                // (this permission is 100% rejected for this SID)
                if (firstRejection == null) {
                  // Store first rejection for auditing reasons
                  firstRejection = new AccessControlEntryImpl(ace.getId(), acl, sid, permission, ace.isGranting(), ace.isAuditSuccess(), ace.isAuditFailure());
                }

                scanNextSid = false; // helps break the loop

                break; // exit ace loop
              }
            }
          } // end for ace

          if (!scanNextSid) {
            break; // exit SID for loop (now try next permission)
          }
        } // end for sid
      } // end for permission
      if (firstRejection != null) {
        if (!administrativeMode) {
          auditLogger.logIfNeeded(false, firstRejection);
        }
        return false;
      }
      // No matches have been found so far
      if (acl.isEntriesInheriting() && (acl.getParentAcl() != null)) {
        // We have a parent, so let them try to find a matching ACE
        aceList = null;
        return isGranted((MutableAcl) acl.getParentAcl(), query, permissions, sids, administrativeMode);
      } else {
        // We either have no parent, or we're the uppermost parent
        throw new NotFoundException("Unable to locate a matching ACE for passed permissions and SIDs");
      }
    }

    catch (NoResultException e) {
      // No matches for this ACL
      if (acl.isEntriesInheriting() && (acl.getParentAcl() != null)) {
        // We have a parent, so let them try to find a matching ACE
        aceList = null;
        return isGranted((MutableAcl) acl.getParentAcl(), query, permissions, sids, administrativeMode);
      } else {
        // We either have no parent, or we're the uppermost parent
        throw new NotFoundException("Unable to locate a matching ACE for passed permissions and SIDs");
      }
    }

  }

  public MutableAcl read(Long id) throws NotFoundException {
    // SID fields, ObjectID fields
    StringBuilder sb = new StringBuilder("SELECT  sid.principal, sid.sid, sid.id, acl.object_id_identity, class.class, acl.parent_object, acl.entries_inheriting")
        .append(" FROM acl_object_identity acl, acl_class class, acl_sid sid")
        .append(" WHERE class.id=acl.object_id_class AND sid.id=acl.owner_sid AND acl.id=:id");
    Query q = em.createNativeQuery(sb.toString());
    q.setParameter("id", id);

    Object[] result = (Object[]) q.getSingleResult();

    // SID fields
    Boolean sid_isPrincipal = (Boolean) result[0];
    String sid_sid = result[1].toString();
    Long sid_id = ((Number) result[2]).longValue();

    // Object Identity fields
    Long object_id = ((Number) result[3]).longValue();
    String object_class = result[4].toString();

    // ACL fields
    Long parent = result[5] == null ? null : ((Number) result[5]).longValue();
    Boolean inheriting = (Boolean) result[6];

    DaoBackedSid owner = new DaoBackedSid(sid_id, sid_isPrincipal, sid_sid);
    ObjectIdentity objectIdentity = new ObjectIdentityImpl(object_class, object_id);
    return new JpaAcl(id, objectIdentity, parent, owner, inheriting);
  }

  public MutableAcl read(ObjectIdentity object) throws NotFoundException {
    StringBuilder sb = new StringBuilder("SELECT acl.id, acl.parent_object, acl.entries_inheriting, sid.principal, sid.sid, acl.owner_sid")
         .append(" FROM acl_object_identity acl, acl_class class, acl_sid sid")
        .append(" WHERE class.class=:class AND acl.object_id_class=class.id AND acl.object_id_identity=:id AND sid.id=acl.owner_sid");
    Query q = em.createNativeQuery(sb.toString());
    q.setParameter("id", object.getIdentifier());
    q.setParameter("class", object.getType());
    try {
      Object[] result = (Object[]) q.getSingleResult();

      // ACL fields
      Long id = ((Number) result[0]).longValue();
      Long parent = result[1] == null ? null : ((Number) result[1]).longValue();
      Boolean inheriting = (Boolean) result[2];

      // SID fields
      Boolean sid_isPrincipal = (Boolean) result[3];
      String sid_sid = result[4].toString();
      Long sid_id = ((Number) result[5]).longValue();

      DaoBackedSid owner = new DaoBackedSid(sid_id, sid_isPrincipal, sid_sid);
      return new JpaAcl(id, object, parent, owner, inheriting);
    } catch (NoResultException e) {
      throw new NotFoundException("Could not find ACL for " + object.toString());
    }

  }

  public MutableAcl read(ObjectIdentity object, List<Sid> sids) throws NotFoundException {
    JpaAcl retval = (JpaAcl) read(object);
    retval.setSidFilter(sids);
    return retval;
  }

  public Map<ObjectIdentity, Acl> read(List<ObjectIdentity> objects) throws NotFoundException {
    Map<ObjectIdentity, Acl> retval = new HashMap<ObjectIdentity, Acl>();
    for (ObjectIdentity id : objects) {
      retval.put(id, read(id));
    }
    return retval;
  }

  public Map<ObjectIdentity, Acl> read(List<ObjectIdentity> objects, List<Sid> sids) throws NotFoundException {
    Map<ObjectIdentity, Acl> retval = new HashMap<ObjectIdentity, Acl>();
    for (ObjectIdentity id : objects) {
      retval.put(id, read(id, sids));
    }
    return retval;
  }

  @Override
  public List<ObjectIdentity> getObjectIdentitiesByTypeAndAccess(String principal, String type, List<Permission> perms) {
    Assert.notNull(principal, "Principal may not be null");
    ObjectIdentity oi;
    List<Sid> sids = new ArrayList<Sid>();

    sids.add(sidDao.getSid(true, principal));
    for (GrantedAuthority ga : userDetailsService.loadUserByUsername(principal).getAuthorities()) {
      if (ga.getAuthority() != null) {
        sids.add(sidDao.getSid(false, ga.getAuthority()));
      }
    }

    List<ObjectIdentity> retval = new ArrayList<ObjectIdentity>();
    Query q = em.createNativeQuery("SELECT acl_object_identity.object_id_identity " +
        "FROM acl_object_identity, acl_class " +
        "WHERE acl_object_identity.object_id_class=acl_class.id AND acl_class.class=:class");
    q.setParameter("class", type);

    List<?> result = q.getResultList();
    for (Object o : result) {
      oi = new ObjectIdentityImpl(type, (Serializable) o);
      try {
        if (isGranted(read(oi), perms, sids, true)) {
          retval.add(oi);
        }
      } catch (NotFoundException e) {
        // no requested ACE so do nothing
      }
    }

    return retval;

  }

  // DEBUG
  // private void displayQueryResults(List<Object[]> results) {
  // for (Object[] result : results) {
  // displayQueryResult(result);
  // }
  // }
  //
  // private void displayQueryResult(Object[] result) {
  // StringBuilder sb = new
  // StringBuilder();
  // for (Object o : result) {
  // sb.append("[").append(o
  // ).append("]");
  // }
  // System.out.println(sb.toString());
  // }

  /**
   * Implements a JPA based auditable ACL.
   */
  public class JpaAcl implements AuditableAcl {

    /**
     * 
     */
    private static final long serialVersionUID = 2548807323971597635L;

    private Long id;

    private ObjectIdentity object;

    private MutableAcl parent;

    private DaoBackedSid owner;

    private boolean inheriting;

    private Integer hash;

    private Collection<Sid> sidFilter; // JpaSid only

    private JpaAcl(Long id, ObjectIdentity object, Long parentId, Sid owner, boolean inheriting) {
      this.id = id;
      this.object = object;
      this.parent = getAclProxy(parentId);
      this.owner = sidDao.getSid(owner);
      this.inheriting = inheriting;
      this.sidFilter = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.smartgrid.security.entities.SGAclInterface#deleteAce(int)
     */
    public void deleteAce(int aceIndex) throws NotFoundException {
      aclEntryDao.deleteByIndex(this, aceIndex);
      aclCache.evictFromCache(this.getObjectIdentity());
    }

    @Override
    public void updateAuditing(int aceIndex, boolean auditSuccess, boolean auditFailure) {
      aclEntryDao.updateAuditing(this, aceIndex, auditSuccess, auditFailure);

    }

    /**
     * Set the Sid filter so that the call to get Entries is limited to only the
     * Sids listed here.
     * 
     * @param sidFilter
     *          The list of Sid to limit the results to.
     */
    public void setSidFilter(Collection<Sid> sidFilter) {
      if (sidFilter != null) {
        this.sidFilter = new HashSet<Sid>();
        for (Sid sid : sidFilter) {
          this.sidFilter.add(sidDao.getSid(sid));
        }
      }
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.smartgrid.security.entities.SGAclInterface#getId()
     */
    @Override
    public Serializable getId() {
      return id;

    }

    public void setId(long id) {
      this.id = id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.smartgrid.security.entities.SGAclInterface#insertAce(int,
     * org.springframework.security.acls.model.Permission,
     * org.springframework.security.acls.model.Sid, boolean)
     */
    @Override
    public void insertAce(int atIndexLocation, Permission permission, Sid sid, boolean granting) throws NotFoundException {
      aclEntryDao.insert(this, atIndexLocation, permission, sid, granting);
      aclCache.evictFromCache(this.getObjectIdentity());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * gov.smartgrid.security.entities.SGAclInterface#setEntriesInheriting(boolean
     * )
     */
    @Override
    public void setEntriesInheriting(boolean entriesInheriting) {
      inheriting = entriesInheriting;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * gov.smartgrid.security.entities.SGAclInterface#setOwner(org.springframework
     * .security.acls.model.Sid)
     */
    @Override
    public void setOwner(Sid newOwner) {
      this.owner = sidDao.getSid(newOwner);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * gov.smartgrid.security.entities.SGAclInterface#setParent(org.springframework
     * .security.acls.model.Acl)
     */
    @Override
    public void setParent(Acl newParent) {
      // aclAuthorizationStrategy.securityCheck(this,
      // AclAuthorizationStrategy.CHANGE_GENERAL);
      Assert.isTrue(newParent != null, "Parent can not be null");

      MutableAcl mAcl = null;
      if ((newParent instanceof JpaAcl)) {
        mAcl = (MutableAcl) newParent;
      } else {
        mAcl = aclDao.read(newParent.getObjectIdentity());
      }
      Assert.isTrue(!mAcl.equals(this), "Cannot be the parent of yourself");
      this.parent = mAcl;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.smartgrid.security.entities.SGAclInterface#updateAce(int,
     * org.springframework.security.acls.model.Permission)
     */
    @Override
    public void updateAce(int aceIndex, Permission permission) throws NotFoundException {
      aclEntryDao.update(this, aceIndex, permission);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.smartgrid.security.entities.SGAclInterface#getEntries()
     */
    @Override
    public List<AccessControlEntry> getEntries() {
      return aclEntryDao.getForSid(this, sidFilter);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.smartgrid.security.entities.SGAclInterface#getObjectIdentity()
     */
    @Override
    public ObjectIdentity getObjectIdentity() {
      return object;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.smartgrid.security.entities.SGAclInterface#getOwner()
     */
    @Override
    public DaoBackedSid getOwner() {
      return owner;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.smartgrid.security.entities.SGAclInterface#getParentAcl()
     */
    @Override
    public MutableAcl getParentAcl() {
      return parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.smartgrid.security.entities.SGAclInterface#isEntriesInheriting()
     */
    @Override
    public boolean isEntriesInheriting() {
      return inheriting;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * gov.smartgrid.security.entities.SGAclInterface#isGranted(java.util.List,
     * java.util.List, boolean)
     */
    @Override
    /**
     * This implementation cascades checks as follows:
     * 1) if there is an entry for one of the Sids in the current ACL that permission is returned.
     * 2) get the parent ACL and check it.
     */
    public boolean isGranted(List<Permission> permission, List<Sid> sids, boolean administrativeMode) throws NotFoundException, UnloadedSidException {
      Assert.notEmpty(permission, "Permission list  may not be null");
      return aclDao.isGranted(this, permission, sids, administrativeMode);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * gov.smartgrid.security.entities.SGAclInterface#isSidLoaded(java.util.
     * List)
     */
    @Override
    public boolean isSidLoaded(List<Sid> sids) {
      if (this.sidFilter != null && sids != null) {
        for (Sid sid : sids) {
          if (!this.sidFilter.contains(sidDao.getSid(sid))) {
            return false;
          }
        }
      }
      // if there sid filter is emtpy or we dropped throug the loop check then
      // we are good to go.
      return true;
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof JpaAcl) {
        JpaAcl acl = (JpaAcl) o;
        return new EqualsBuilder().append(this.object.getType(), acl.object.getType()).append(this.object.getIdentifier(), acl.object.getIdentifier()).isEquals();
      }
      return false;
    }

    @Override
    public int hashCode() {
      if (hash == null) {
        hash = new HashCodeBuilder().append(this.object.getType()).append(this.object.getIdentifier()).toHashCode();
      }
      return hash;
    }

    /**
     * Create a proxy to the specified JpaAcl.
     * 
     * This is used in cases where we don't want to load the parent ACL when we
     * load the child but may need to access it later.
     * 
     * @param id
     *          The JpaAcl id.
     * @return A proxy to the JpaAcl object.
     */
    private AuditableAcl getAclProxy(Long id) {
      if (id != null) {
        return (AuditableAcl) Proxy.newProxyInstance(JpaAcl.class.getClassLoader(),
            new Class[] { AuditableAcl.class },
            new JpaAclProxy(id));
      } else {
        return null;
      }
    }

    /**
     * A proxy to a JpaAcl.
     * 
     * Used in cases where we don't want to load the parent ACL when we load the
     * child but may need to access it later.
     * 
     * @author cwarren
     * 
     */
    private class JpaAclProxy implements InvocationHandler {
      MutableAcl object;
      Long id;

      public JpaAclProxy(Long id) {

        this.id = id;
        this.object = null;
      }

      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("getId")) {
          return id;
        } else {
          if (object == null) {
            this.object = aclDao.read(id);
          }
          return method.invoke(object, args);
        }

      }
    }
  }

  /**
   * An class that wraps the Object[] returned by the native Sql queries to the
   * Acl_entry table. This makes it much easier to write readable code that
   * iterates over the result set.
   * 
   * @author cwarren
   * 
   */
  private class DbAclEntry {
    Object[] data;

    DbAclEntry(Object[] data) {
      this.data = data;
    }

    public long getId() {
      return ((Number) data[0]).longValue();
    }

    public long getAclId() {
      return ((Number) data[1]).longValue();
    }

    public int getMask() {
      return ((Number) data[4]).intValue();
    }

    public long getSidId() {
      return ((Number) data[3]).intValue();
    }

    public boolean isGranting() {
      return (Boolean) data[5];
    }

    public boolean isAuditSuccess() {
      return (Boolean) data[6];
    }

    public boolean isAuditFailure() {
      return (Boolean) data[7];
    }

    public int getOrder() {
      return ((Number) data[2]).intValue();
    }
  }

}
