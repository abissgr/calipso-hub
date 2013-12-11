package org.springframework.security.acls.jpa.dao;

import java.util.Collection;
import java.util.List;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.AuditableAcl;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;

/**
 * Defines the interface to persist AclEntries.
 * 
 * AclEntries grant or revoke a specific permission for a specific Sid.
 * 
 * AclEntries are managed as lists. 
 * 
 * @author cwarren
 *
 */
public interface AclEntryDao {

  /**
   * Delete all ACL Entries for the specific ObjectIdentity.
   * 
   * Note: this is not the same as deleting the ACL.
   * Note: this method will remove all permissions granted ot the ObjectIdentity.
   * 
   * @param objectIdentity The ObjectIdentity to delete ACL Entries for.
   * @throws NotFoundException if there are no ACL entries for the ObjectIdentity or if the ACL does not exist.
   */
  public void delete(ObjectIdentity objectIdentity) throws NotFoundException;
  
  /**
   * Delete the ACL entries for the for the sid within the specific ACL
   * @param acl The ACL to alter
   * @param sid The SID to delete
   */
  public void delete(MutableAcl acl, Sid sid);

  /**
   * Delete a specific entry from an ACL.
   * 
   * The index is the positional index of the item in the list.  This should not be confused with the entry order.
   * 
   * @param acl The ACL to delete the entry from.
   * @param index  The index of the entry to delete.
   * @throws NotFoundException if the Index<0 or index>number of entries.
   */
  public void deleteByIndex(MutableAcl acl, int index) throws NotFoundException;

  /**
   * Get a specific entry.
   * @param acl The ACL to retrieve the entry from.
   * @param index  The index of the entry to retrieve.
   * @return the specified entry.
   * @throws NotFoundException if the Index<0 or index>number of entries.
   */
  public AccessControlEntry getByIndex(MutableAcl acl, int index) throws NotFoundException;;

  /**
   * Get a list of AccessControlEntries for an ACL limited to entries for specific Sids
   * 
   * If the sid list is null or empty all entries will bge returned.
   * 
   * @param acl The ACL to retrieve the entry from
   * @param sid The list of Sids to limit the entries to.
   * @throws NotFoundException there are no entries for the Sids associated with the ACL.
   * @return A list of entries.
   */
  public List<AccessControlEntry> getForSid(MutableAcl acl, Collection<Sid> sid) throws NotFoundException;

  /**
   * Get the entire list of entries for the ACL.
   * 
   * Warning: the list of entries may be quite large.
   * 
   * @param acl The ACL to retrieve the entries from
   * @throws NotFoundException there are no entries for the ACL.
   * @return A list of entries.
   */
  public List<AccessControlEntry> getForAcl(MutableAcl acl) throws NotFoundException;

  /**
   * Insert a new entry in the list.
   * 
   * Inserts the specified element at the specified position in this list (optional operation). Shifts the element currently at that position (if any) and any subsequent elements to the right (adds one to their indices). 
   * The index must be >=0 and <= the number of items in the list.
   * 
   * The index is the position in the master list.  This position should not be confused with the index in any list that has
   * been filtered by Sids.
   * 
   * 
   * @param acl  The ACL to insert the entry into
   * @param atIndexLocation The position in the list to insert the entry. 
   * @param permission The permission for the entry
   * @param sid The sid that is gratned/revoked the permission
   * @param granting True to grant the permission, false to revoke it.
   * @throws NotFoundException if the atIndexLocation<0 or atIndexLocation > the number of items in the list.
   */
  public void insert(MutableAcl acl, int atIndexLocation, Permission permission, Sid sid, boolean granting) throws NotFoundException;

  /**
   * Update a specified entry in an ACL.
   * 
   * The index must be 0<= index < number of items in the list.
   * 
   * @param acl  The ACL containing the entry to edit.
   * @param aceIndex The position in the list of the entry to edit.
   * @param permission The permission to set the entry to.
   * @throws NotFoundException if the atIndexLocation<0 or atIndexLocation >= the number of items in the list.
   */
  public void update(MutableAcl acl, int aceIndex, Permission permission) throws NotFoundException;

  /**
   * Update the auditing flags for an entry.
   * 
   * If the autiditng flags are on then the approval or denial of the action will be logged to an auditLogger.
   * 
   * The index must be 0<= index < number of items in the list.
   * 
   * @param acl  The ACL containing the entry to edit.
   * @param aceIndex The position in the list of the entry to edit.
   * @param auditSuccess true if success events should be logged to the audit log.
   * @param auditFailure true if failure events should be logged to the audit log.
   * @throws NotFoundException  if the atIndexLocation<0 or atIndexLocation >= the number of items in the list.
   */
  public void updateAuditing(AuditableAcl acl, int aceIndex, boolean auditSuccess, boolean auditFailure) throws NotFoundException;

}
