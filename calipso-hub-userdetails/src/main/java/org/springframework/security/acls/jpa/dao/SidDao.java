package org.springframework.security.acls.jpa.dao;

import java.util.List;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Defines the interface to persist Sids.
 * 
 * Sids are an abstract representation of a security identity. It generally
 * represents a person, group, or role.
 * 
 * 
 * @author cwarren
 * 
 */
public interface SidDao {

  /**
   * Get the Sid for an authentication object. If the Sid is not in the database
   * it is created.
   * 
   * The Sid is created from the principal contained in the authenticaiton
   * object.
   * 
   * @param authentication
   * @return a JPA based Sid.
   */
  public abstract DaoBackedSid getSid(Authentication authentication);

  /**
   * Get a Sid. If the Sid is not in the database it is created.
   * 
   * @param principal
   *          True if this Sid represents a principal (person) as opposed to a
   *          group or role
   * @param id
   *          The name of the principal, group or role.
   * @return a JPA based SId
   */
  public abstract DaoBackedSid getSid(Boolean principal, String id);

  /**
   * Get a Sid. If the Sid is not in the database it is created.
   * 
   * @param sid
   *          The JPA based sid.
   * @return a JPA based SId
   */
  public abstract DaoBackedSid getSid(DaoBackedSid sid);

  /**
   * Get a Sid. If the Sid is not in the database it is created.
   * 
   * @param sid
   *          Any sid.
   * @return a JPA based SId
   */
  public abstract DaoBackedSid getSid(Sid sid);

  /**
   * Get a Sid.
   * 
   * @param id
   *          The id of the JPA based sid.
   * @return a JPA based SId
   * @Throws NotFoundException if no Sid with the ID exists.
   */
  public DaoBackedSid getSid(Long id) throws NotFoundException;

  /**
   * Get a list of sid that have the specified permissions to the object
   * identity
   * 
   * @param objectIdentity
   * @return
   */
  public List<Sid> getPrincipalSidsFor(List<Permission> perms, ObjectIdentity objectIdentity);

  /**
   * An implementation of a JPA Dao based sid.
   * 
   * @author cwarren
   * 
   */
  public class DaoBackedSid implements Sid {

    private static final long serialVersionUID = -531373324335308070L;
    private Long id;
    private String sid;
    private Boolean principal;
    private Integer hash;

    /**
     * Create the Sid from an authentication object.
     * 
     * Uses the authentication principal as the sid.
     * 
     * @param authentication
     */
    public DaoBackedSid(Authentication authentication) {
      this.id = null;
      this.principal = true;
      if (authentication.getPrincipal() instanceof UserDetails) {
        this.sid = ((UserDetails) authentication.getPrincipal()).getUsername();
      } else {
        this.sid = authentication.getPrincipal().toString();
      }
    }

    /**
     * Create an Sid from an id string and a principal flag.
     * 
     * @param principal
     *          True if this Sid is a principal and not a group or role.
     * @param id
     *          The id or name of the principal.
     */
    public DaoBackedSid(Boolean principal, String id) {
      this(null, principal, id);
    }

    /**
     * Create a Sid from a PrincipalSid.
     * 
     * @param pSid
     */
    public DaoBackedSid(PrincipalSid pSid) {
      this(true, pSid.getPrincipal());
    }

    /**
     * Create a sid from a GrantedAuthoritySid (i.e. group or role)
     * 
     * @param gaSid
     */
    public DaoBackedSid(GrantedAuthoritySid gaSid) {
      this(false, gaSid.getGrantedAuthority());
    }

    /**
     * create a Sid when all elements are known
     * 
     * @param id
     *          The database ID for the sid
     * @param principal
     *          The principal flag
     * @param sid
     *          The name of the principal or granted authority.
     */
    public DaoBackedSid(Long id, Boolean principal, String sid) {
      this.id = id;
      this.principal = principal;
      this.sid = sid;
    }

    /**
     * @return the id
     */
    public Long getId() {
      return id;
    }

    /**
     * @param id
     *          the id to set
     */
    public void setId(Long id) {
      this.id = id;
      this.hash = null;
    }

    /**
     * @return the principal
     */
    public Boolean isPrincipal() {
      return principal;
    }

    /**
     * @param principal
     *          the principal to set
     */
    public void setPrincipal(Boolean principal) {
      this.principal = principal;
    }

    /**
     * @return the sid
     */
    public String getSid() {
      return sid;
    }

    /**
     * @param sid
     *          the sid to set
     */
    public void setSid(String sid) {
      this.sid = sid;
      this.hash = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
      if (obj instanceof DaoBackedSid) {
        DaoBackedSid sid = (DaoBackedSid) obj;
        return new EqualsBuilder().append(this.getSid(), sid.getSid()).append(this.getId(), sid.getId()).isEquals();
      }
      return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
      if (hash == null) {
        hash = new HashCodeBuilder().append(getSid()).append(getId()).toHashCode();
      }
      return hash;
    }

    @Override
    public String toString() {
      return String.format("SGSid[Principal=%s,Value='%s',id=%s]", isPrincipal(), getSid(), getId());
    }

  }
}