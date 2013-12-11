package org.springframework.security.acls.jpa.dao;

import java.util.List;
import java.util.Map;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.ChildrenExistException;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.acls.model.UnloadedSidException;

/**
 * Defines the interface to persist ACLs.
 * 
 * @author cwarren
 *
 */
public interface AclDao {

  /**
   * Create a MutableAcl.
   * 
   * @param object
   * @param parentId
   * @param owner
   * @param inheriting
   * @return a MutableAcl.
   */
  public MutableAcl create(ObjectIdentity object, Long parentId, Sid owner, boolean inheriting);

  /**
   * Delete an ACL based on the object Identity.
   * 
   * If the deleteChildren flag is not set then the children are not deleted
   * first. This means that the database schema will determine if the children
   * should be deleted on the delete operation. If there are children and they
   * are not deleted the operation will fail.
   * 
   * @param objectIdentity
   *          The object identity of the ACL to delete
   * @param deleteChildren
   *          The delete children flag, if true children will be deleted
   * @throws ChildrenExistException
   */
  public void delete(ObjectIdentity objectIdentity, boolean deleteChildren) throws ChildrenExistException;
  
  /**
   * Delete the entries for the SID on the object.
   * @param objectIdentity The object to modify the ACL for
   * @param sid the SID to delete the entries of.
   */
  public void deleteEntries(ObjectIdentity objectIdentity, Sid sid);

  /**
   * Update the ACL.
   * 
   * At a minimum update() must save the parent, owner and inheriting flag. In
   * implementations where the ACE list is managed as a Java List the ACE list
   * must also be updated.
   * 
   * @param acl
   * @return The updated ACL.
   * @throws NotFoundException
   *           if the ACL can not be located.
   */
  public MutableAcl update(MutableAcl acl) throws NotFoundException;

  /**
   * Get a list of ObjectIdentities that are the children of an ObjectIdentity.
   * 
   * @param parentIdentity
   *          the parent ObjectIdentity
   * @return The list of children.
   */
  public List<ObjectIdentity> findChildren(ObjectIdentity parentIdentity);

  /**
   * Determines authorization.
   * 
   * <emph>The order of the permission and sid arguments is extremely
   * important!</emph>
   * 
   * The method will iterate through each of the permissions in the order
   * specified. For each iteration, all of the sids will be considered, again in
   * the order they are presented. A search will then be performed for the first
   * AccessControlEntry object that directly matches that permission:sid
   * combination. When the first full match is found (ie an ACE that has the SID
   * currently being searched for and the exact permission bit mask being search
   * for), the grant or deny flag for that ACE will prevail. If the ACE
   * specifies to grant access, the method will return true. If the ACE
   * specifies to deny access, the loop will stop and the next permission
   * iteration will be performed. If each permission indicates to deny access,
   * the first deny ACE found will be considered the reason for the failure (as
   * it was the first match found, and is therefore the one most logically
   * requiring changes - although not always). If absolutely no matching ACE was
   * found at all for any permission, the parent ACL will be tried (provided
   * that there is a parent and isEntriesInheriting() is true. The parent ACL
   * will also scan its parent and so on. If ultimately no matching ACE is
   * found, a NotFoundException will be thrown and the caller will need to
   * decide how to handle the permission check. Similarly, if any of the SID
   * arguments presented to the method were not loaded by the ACL,
   * UnloadedSidException will be thrown.
   * 
   * @param acl The ACL to check
   * @param permission The list of permissions to accept (Order is important, see above)
   * @param sids The list of Sids to check (Order is important, see above)
   * @param administrativeMode If true the result will not be logged to the auditLogger
   * @return True if the permissions are found, false if they are denied.
   * @throws NotFoundException If access is neither approved or denied.
   * @throws UnloadedSidException If the Sid was not "loaded" for the ACL.  (See read(ObjectIdentity, List<Sid>))
   */
  public boolean isGranted(Acl acl, List<Permission> permission, List<Sid> sids, boolean administrativeMode) throws NotFoundException, UnloadedSidException;

  /**
   * Read the ACL based on the internal ACL number.
   * @param id THe ACL number in the database
   * @return The ACL
   * @throws NotFoundException if no ACL with the number exists
   */
  public MutableAcl read(Long id) throws NotFoundException;

  /**
   * Read the ACL for the specific ObjectIdentity.
   * @param object The ObjectIdentity to search for
   * @return The ACL
   * @throws NotFoundException if no ACL for the ObjectIdentity exists.
   */
  public MutableAcl read(ObjectIdentity object) throws NotFoundException;

  /**
   * Read the ACL for the specific ObjectIdentity and limit the entry search to the specific Sids.
   * @param object The ObjectIdentity to search for 
   * @param sids The list of Sids to limit the search to.
   * @return The ACL with entries limited by the specified sid list.
   * @throws NotFoundException if no ACL for the ObjectIdentity exists.
   */
  public MutableAcl read(ObjectIdentity object, List<Sid> sids) throws NotFoundException;

  /**
   * Read a list of ACLs.
   * @param objects The list of ObjectIdentites to find
   * @return  A map of ObjectIdenties to ACLs
   * @throws NotFoundException If any one of the ObjectIdenties is not found.
   */
  public Map<ObjectIdentity, Acl> read(List<ObjectIdentity> objects) throws NotFoundException;

  /**
   * Read a list of ACLs and limit the entries to specific Sids
   * @param objects The list of ObjectIdentites to find
   * @param sids The list of Sids to limit the search to.
   * @return  A map of ObjectIdenties to ACLs with entries limited by the specified sid list.
   * @throws NotFoundException If any one of the ObjectIdenties is not found.
   */
  public Map<ObjectIdentity, Acl> read(List<ObjectIdentity> objects, List<Sid> sids) throws NotFoundException;

  /**
   * If set true then the database foreign keys are in effect and will be used to determine if orphaned child rows would exist after a delete.  (see delete()).
   * @param b the state to the the flag to.
   */
  public void setForeignKeysInDatabase(boolean b);
  
  /**
   * Get a list of all ObjectIds that represent object of type <code>type</code> and for which the user has <code>perms</code> access.
   * 
   * @param perms
   * @return
   */
 public List<ObjectIdentity> getObjectIdentitiesByTypeAndAccess( String principal, String type, List<Permission> perms);

}
