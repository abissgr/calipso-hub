package org.springframework.security.acls.jpa.entities;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.security.acls.domain.CumulativePermission;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.AuditableAccessControlEntry;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;

public class JpaAclEntry implements AuditableAccessControlEntry, Comparable<JpaAclEntry> {

  /**
   * 
   */
  private static final long serialVersionUID = 8374146261701773238L;
  private Long id;
  private Acl acl;
  private Permission permission;
  private Sid sid;
  private Boolean granting;
  private Integer order;
  private Integer hash;
  private boolean auditFailure;
  private boolean auditSuccess;
  
  @Override
  public boolean isAuditFailure() {
    return auditFailure;
  }

  @Override
  public boolean isAuditSuccess() {
    return auditSuccess;
  }

  public JpaAclEntry() {
  }

  public JpaAclEntry(Acl acl, int atIndexLocation, Permission permission, Sid sid, boolean granting, boolean auditSuccess, boolean auditFailure) {
    this.acl = acl;
    this.permission = permission;
    this.sid = sid;
    this.granting = granting;
    this.order = atIndexLocation;
    this.auditSuccess = auditSuccess;
    this.auditFailure = auditFailure;
  }

  public JpaAclEntry(long id, Acl acl, int atIndexLocation, Permission permission, Sid sid, boolean granting, boolean auditSuccess, boolean auditFailure) {
    this(acl, atIndexLocation, permission, sid, granting, auditSuccess, auditFailure);
    this.id = id;
  }

  protected void setOrder(Integer order) {
    this.order = order;
  }

  protected void setAcl(Acl acl) {
    this.acl = acl;
  }

  @Override
  public Acl getAcl() {
    return acl;
  }

  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getOrder() {
    return order;
  }

  @Override
  public Permission getPermission() {
    return permission;
  }

  public void setPermission(Permission permission) {
    this.permission = permission;
  }

  @Override
  public Sid getSid() {
    return sid;
  }

  @Override
  public boolean isGranting() {
    return granting;
  }

  @Override
  public int compareTo(JpaAclEntry arg0) {
    return new CompareToBuilder().append(this.acl, arg0.acl).append(this.order, arg0.order).toComparison();
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof JpaAclEntry) {
      JpaAclEntry ace = (JpaAclEntry) o;
      return new EqualsBuilder().append(this.acl, ace.getAcl()).append(this.sid, ace.getSid()).append(this.permission, ace.getPermission()).isEquals();
    }
    return false;
  }

  @Override
  public int hashCode() {
    if (hash == null) {
      hash = new HashCodeBuilder().append(this.acl).append(this.sid).append(this.permission).toHashCode();
    }
    return hash;
  }

  public void merge(JpaAclEntry otherEntry) {
    if (this.equals(otherEntry)) {
      CumulativePermission newPermission;
      if (this.getPermission() instanceof CumulativePermission) {
        newPermission = (CumulativePermission) this.getPermission();
      } else {
        newPermission = new CumulativePermission();
        newPermission.set(this.getPermission());
        this.setPermission(newPermission);
      }
      if (otherEntry.isGranting() == this.isGranting()) {
        newPermission.set(otherEntry.getPermission());
      } else {
        newPermission.clear(otherEntry.getPermission());
      }
    }
  }

  /**
   * @param auditFailure
   *          the auditFailure to set
   */
  public void setAuditFailure(boolean auditFailure) {
    this.auditFailure = auditFailure;
  }

  /**
   * @param auditSuccess
   *          the auditSuccess to set
   */
  public void setAuditSuccess(boolean auditSuccess) {
    this.auditSuccess = auditSuccess;
  }

}
