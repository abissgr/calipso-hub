package org.springframework.security.acls.jpa.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.jpa.JpaPermission;
import org.springframework.security.acls.jpa.dao.AclEntryDao;
import org.springframework.security.acls.jpa.dao.SidDao;
import org.springframework.security.acls.jpa.dao.SidDao.DaoBackedSid;
import org.springframework.security.acls.jpa.entities.JpaAclEntry;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.AuditableAcl;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * A JPA based AclEntryDao implementation
 * 
 * @author cwarren
 * 
 */
@Transactional
@Repository
public class AclEntryDaoImpl implements AclEntryDao {
  @PersistenceContext
  private EntityManager em;

  @Autowired
  private SidDao sidDao;

  @Autowired
  private AclCache aclCache;

  private static final String BAD_ACL_INDEX = "%d is not a valid ACL index";

  private static final int DEFAULT_INCREMENT = 5;

  public void delete(ObjectIdentity objectIdentity) {
    Query q = em.createNativeQuery("DELETE acl_entry " +
        "FROM acl_entry, acl_object_identity,acl_class " +
        "WHERE acl_entry.acl_object_identity=acl_object_identity.id " +
        "AND acl_object_identity.object_id_class=acl_class.id " +
        "AND acl_object_identity.object_id_identity=:id " +
        "AND acl_class.class=:class");
    q.setParameter("id", objectIdentity.getIdentifier());
    q.setParameter("class", objectIdentity.getType());
    q.executeUpdate();
  }

  public void delete(MutableAcl acl, Sid sid) {
    DaoBackedSid jpaSid = sidDao.getSid(sid);
    Query q = em.createNativeQuery("DELETE acl_entry " +
        "FROM acl_entry " +
        "WHERE acl_entry.acl_object_identity=:aclid " +
        " AND acl_entry.sid=:sid");
    q.setParameter("aclid", acl.getId());
    q.setParameter("sid", jpaSid.getId());
    q.executeUpdate();
  }

  public JpaAclEntry update(JpaAclEntry aclEntry) {
    Query q = em.createNativeQuery("UPDATE acl_entry SET mask=:mask,granting=:granting WHERE id=:id");
    q.setParameter("id", aclEntry.getId());
    q.setParameter("mask", aclEntry.getPermission().getMask());
    q.setParameter("granting", aclEntry.isGranting());
    q.executeUpdate();
    return aclEntry;
  }

  public AccessControlEntry getByIndex(MutableAcl acl, int index) throws NotFoundException {
    Integer order = order = getOrderForIndex(acl, index);

    Query q = em.createNativeQuery("SELECT id, mask, sid, granting, audit_success, audit_failure " +
        "FROM acl_entry " +
        "WHERE acl_entry.acl_object_identity=:id " +
        "AND acl_entry.ace_order=:order ");
    q.setParameter("order", order);
    q.setParameter("id", acl.getId());
    try {
      Object[] result = (Object[]) q.getSingleResult();

      Permission permission = new JpaPermission(((Number) result[1]).intValue());
      DaoBackedSid sid = sidDao.getSid(((Number) result[2]).longValue());
      return new JpaAclEntry(((Number) result[0]).longValue(), acl, order, permission, sid, (Boolean) result[3], (Boolean) result[4], (Boolean) result[5]);
    } catch (NoResultException e) {
      throw new NotFoundException("Control list entry  with order" + order + " not found");
    }
  }

  public List<AccessControlEntry> getForAcl(MutableAcl acl) throws NotFoundException {
    return getForSid(acl, Collections.EMPTY_LIST);
  }

  @Override
  public List<AccessControlEntry> getForSid(MutableAcl acl, Collection<Sid> sidFilter) throws NotFoundException {
    SidMap sidMap = new SidMap();
    StringBuilder query = new StringBuilder("SELECT acl_entry.id,  acl_entry.sid, mask, granting, ace_order, audit_success, audit_failure ")
        .append("FROM acl_entry ")
        .append("WHERE acl_entry.acl_object_identity=:id ");
    if (sidFilter != null && !sidFilter.isEmpty()) {

      StringBuilder sb = new StringBuilder();
      for (Sid sid : sidFilter) {
        sb.append(sidDao.getSid(sid).getId()).append(",");
      }
      sb.deleteCharAt(sb.length() - 1);
      query.append("AND sid in (" + sb.toString() + ") ");

    }
    query.append("ORDER BY acl_entry.ace_order");

    Query q = em.createNativeQuery(query.toString());
    q.setParameter("id", acl.getId());
    try {
      List<Object[]> resultList = q.getResultList();
      List<AccessControlEntry> retval = new ArrayList<AccessControlEntry>();

      JpaAclEntry entry = null;
      Long id = null;
      Long sid_id = null;
      Integer mask = null;
      Boolean granting = null;
      Integer order = null;
      Sid sid = null;
      Boolean auditSuccess;
      Boolean auditFailure;
      for (Object[] result : resultList) {
        id = ((Number) result[0]).longValue();
        sid_id = ((Number) result[1]).longValue();
        sid = sidMap.get(sid_id);

        mask = ((Number) result[2]).intValue();
        granting = (Boolean) result[3];
        order = ((Number) result[4]).intValue();
        auditSuccess = (Boolean) result[5];
        auditFailure = (Boolean) result[6];
        entry = new JpaAclEntry(id, acl, order, new JpaPermission(mask), sid, granting, auditSuccess, auditFailure);
        retval.add(entry);
      }
      return retval;
    } catch (NoResultException e) {
      throw new NotFoundException("No entries for ACL found");
    }

  }

  @Override
  public void deleteByIndex(MutableAcl acl, int index) {

    int order = getOrderForIndex(acl, index);

    Query q = em.createNativeQuery("DELETE FROM acl_entry WHERE acl_object_identity=:acl_id AND ace_order=:order");
    q.setParameter("acl_id", acl.getId());
    q.setParameter("order", order);
    if (q.executeUpdate() == 0) {
      throw new NotFoundException(String.format(BAD_ACL_INDEX, index));
    }

  }

  /**
   * A method to change the index value to the order value.
   * 
   * @param acl
   *          The Acl we are looking at
   * @param index
   *          The index we want
   * @return The order value for the entry.
   * @throws NotFoundException
   */
  private Integer getOrderForIndex(MutableAcl acl, int index) throws NotFoundException {
    if (index < 0) {
      throw new NotFoundException(String.format(BAD_ACL_INDEX, index));
    }
    // find the order for the entry we want
    Query q = em.createNativeQuery("SELECT ace_order FROM acl_entry WHERE acl_object_identity=:acl_id LIMIT :index, 1");
    q.setParameter("acl_id", acl.getId());
    q.setParameter("index", index);
    try {
      return ((Integer) q.getSingleResult());
    } catch (NoResultException e) {
      throw new NotFoundException(String.format(BAD_ACL_INDEX, index));
    }
  }

  @Override
  public void insert(MutableAcl acl, int atIndexLocation, Permission permission, Sid sid, boolean granting) {
    Assert.notNull(permission, "Permission may not be null");
    Assert.notNull(sid, "Sid may not be null");
    if (atIndexLocation < 0) {
      throw new NotFoundException("index may not be less than 0");
    }
    // DEBUG
    // {
    // Query q =
    // em.createNativeQuery("SELECT * FROM acl_entry WHERE acl_object_identity=:acl_id");
    // q.setParameter("acl_id", acl.getId());
    // displayQueryResults(q.getResultList());
    // }

    // get the number of records in the database as well as the maximum order
    // value.
    Query q = em.createNativeQuery("SELECT count(*), IFNULL(max(ace_order),0) FROM acl_entry WHERE acl_object_identity=:acl_id");
    q.setParameter("acl_id", acl.getId());
    Object[] result = (Object[]) q.getSingleResult();
    int max_index = ((Number) result[0]).intValue() - 1;
    int max_order = ((Number) result[1]).intValue();

    try {
      // find the order for the entry we want
      int target_order = getOrderForIndex(acl, atIndexLocation);

      // get the order numbers that are +/-5 on either side of the order. and
      // place them in a navigable set.
      // we will then try to find an open slot where we can insert the new
      // number.
      // debug displayQueryResults( em.createNativeQuery(
      // "SELECT * from acl_entry WHERE acl_object_identity="+acl.getId()).getResultList()
      // );
      q = em.createNativeQuery("SELECT ace_order FROM acl_entry WHERE acl_object_identity=:acl_id AND ace_order BETWEEN (:target - " + DEFAULT_INCREMENT +
          ") AND (:target+" + DEFAULT_INCREMENT + ")");
      q.setParameter("acl_id", acl.getId());
      q.setParameter("target", target_order);
      NavigableSet<Integer> range = new TreeSet<Integer>(q.getResultList());
      if (range.size() == 1) {
        // only one element in the list so just insert this on before or after
        // it
        int new_order = (target_order - DEFAULT_INCREMENT + 1);
        insertAt(acl, new_order, permission, sid, granting);
      } else {
        // look for space backwards
        Integer firstTarget = target_order;
        Integer prevTarget = range.lower(firstTarget);
        Integer nextTarget = null;
        while (prevTarget != null && prevTarget + 1 == firstTarget) {
          firstTarget = prevTarget;
          prevTarget = range.lower(firstTarget);
        }
        if (prevTarget == null) {
          // there was no space backwards so look forwards
          firstTarget = target_order;
          nextTarget = range.higher(firstTarget);
          while (nextTarget != null && nextTarget - 1 == firstTarget) {
            firstTarget = nextTarget;
            nextTarget = range.higher(firstTarget);
          }
          if (nextTarget == null) {
            // no space in the local region (+/- 5 elements so reorder and
            // recurse
            resequence(acl, DEFAULT_INCREMENT, DEFAULT_INCREMENT);
            insert(acl, atIndexLocation, permission, sid, granting);
          } else {
            // we found space forward so
            // we need to add 1 to every element from target through fisrtTarget
            // to make room for the new entry
            changeOrder(acl, target_order, firstTarget, true);
            insertAt(acl, target_order, permission, sid, granting);
          }
        } else {
          if (firstTarget == target_order) {
            // there is space before the target so insert at the midpoint to
            // leave
            // space for future
            // inserts
            int new_order = target_order - ((target_order - prevTarget + 1) / 2);
            insertAt(acl, new_order, permission, sid, granting);
          } else {
            // we need to subtract 1 from every element from firstTarget to
            // target-1 and then insert the new record.
            changeOrder(acl, firstTarget, target_order - 1, false);
            insertAt(acl, target_order, permission, sid, granting);
          }
        }
      }
    } catch (NotFoundException e) {
      // no entry at that position
      if (atIndexLocation == max_index + 1) {
        // asking to insert at the first empty position so insert the
        // first one at max_order + DEFAULT_INCREMENT
        insertAt(acl, max_order + DEFAULT_INCREMENT, permission, sid, granting);
      } else {
        throw new NotFoundException(String.format(BAD_ACL_INDEX, atIndexLocation));
      }
    }
  }

  /**
   * adds or subtracts one from each order specified in the start - stop range.
   * 
   * @param acl
   *          The Acl we are working on
   * @param startOrder
   *          The first order to change.
   * @param stopOrder
   *          The last order to change
   * @param increment
   *          true to increment by one, false to decrement by one.
   */
  private void changeOrder(MutableAcl acl, Integer startOrder, Integer stopOrder, boolean increment) {
    Query q = em.createNativeQuery("UPDATE acl_entry SET ace_order=ace_order+:increment WHERE acl_object_identity=:acl_id AND ace_order BETWEEN :start AND :stop ORDER BY ace_order "
        + (increment ? "DESC" : ""));
    q.setParameter("acl_id", acl.getId());
    q.setParameter("increment", (increment ? 1 : -1));
    q.setParameter("start", startOrder);
    q.setParameter("stop", stopOrder);
    q.executeUpdate();
  }

  /**
   * Inserts a record at a specific order location.
   * 
   * This is contracted with insert() which inserts at a specific list index.
   * 
   * The order is calculated by the insert() method.
   * 
   * @param acl
   * @param order
   * @param permission
   * @param sid
   * @param granting
   */
  private void insertAt(MutableAcl acl, int order, Permission permission, Sid sid, boolean granting) {

    DaoBackedSid sgSid = sidDao.getSid(sid);
    Query q = em.createNativeQuery("INSERT INTO acl_entry (acl_object_identity,ace_order,sid,mask,granting,audit_success, audit_failure) " +
          "VALUES (:acl_id, :order, :sid, :mask, :granting, 0, 0) " +
          "ON DUPLICATE KEY UPDATE" +
          " ace_order=:order," +
          " sid=:sid," +
          " mask=:mask, " +
          " granting=:granting");
    q.setParameter("acl_id", acl.getId());
    q.setParameter("order", order);
    q.setParameter("sid", sgSid.getId());
    q.setParameter("mask", permission.getMask());
    q.setParameter("granting", granting);
    q.executeUpdate();
  }

  /**
   * Renumber the order fields so that the first one is startOrder and the
   * numbers are regularly spaced.
   * 
   * @param acl
   *          The ACL we are working with.
   * @param startOrder
   *          The inital order value
   * @param increment
   *          The spacing between the values.
   */
  public void resequence(MutableAcl acl, int startOrder, int increment) {
    // get the maximum number of entries
    Query q = em.createNativeQuery("SELECT count(*) FROM acl_entry WHERE acl_object_identity=:acl_id");
    q.setParameter("acl_id", acl.getId());
    int aclCount = ((Number) q.getSingleResult()).intValue();
    int currentIdx = startOrder + (increment * aclCount);
    // get the list of order values in descending order.
    q = em.createNativeQuery("SELECT ace_order FROM acl_entry WHERE acl_object_identity=:acl_id ORDER BY ace_order Desc");
    q.setParameter("acl_id", acl.getId());
    List<Number> orig_order = q.getResultList();
    // Iterate through the list (descending order) updating the order numbers.
    q = em.createNativeQuery("UPDATE acl_entry SET ace_order = :new_order WHERE acl_object_identity=:acl_id AND ace_order=:old_order");
    q.setParameter("acl_id", acl.getId());
    for (Number o : orig_order) {
      q.setParameter("new_order", currentIdx);
      q.setParameter("old_order", o);
      q.executeUpdate();
      currentIdx -= increment;
    }
  }

  public void update(MutableAcl acl, int aceIndex, Permission permission) {
    JpaAclEntry entry = (JpaAclEntry) getByIndex(acl, aceIndex);
    entry.setPermission(permission);
    update(entry);
  }

  @Override
  public void updateAuditing(AuditableAcl acl, int aceIndex, boolean auditSuccess, boolean auditFailure) {
    int order = getOrderForIndex(acl, aceIndex);
    Query q = em.createNativeQuery("UPDATE acl_entry SET audit_success=:success, audit_failure=:failure WHERE acl_object_identity=:acl_id AND ace_order=:order");
    q.setParameter("acl_id", acl.getId());
    q.setParameter("success", auditSuccess);
    q.setParameter("failure", auditFailure);
    q.setParameter("order", order);
    if (q.executeUpdate() == 0) {
      throw new NotFoundException(String.format(BAD_ACL_INDEX, aceIndex));
    }
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
   * A map of sid Ids to sids used when filtering entry retrieval.
   */
  private class SidMap {
    private Map<Long, DaoBackedSid> map = new HashMap<Long, DaoBackedSid>();

    /**
     * Get the sid ID from the map if it exists there otherwise get it from the
     * database and put it in the map.
     * 
     * @param id
     * @return
     */
    public DaoBackedSid get(Long id) {
      DaoBackedSid retval = map.get(id);
      if (retval == null) {
        retval = sidDao.getSid(id);
        map.put(id, retval);
      }
      return retval;
    }
  }

}
