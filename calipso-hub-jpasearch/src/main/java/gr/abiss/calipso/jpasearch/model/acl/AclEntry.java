package gr.abiss.calipso.jpasearch.model.acl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "acl_entry")
public class AclEntry implements Persistable<Long> {

	private static final long serialVersionUID = -6426926101684163445L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true)
	private Long id;

	// @ManyToOne(/* cascade=CascadeType.ALL, */fetch = FetchType.EAGER)
	// @JoinColumn(name = "acl_object_identity", referencedColumnName = "id",
	// nullable = false, updatable = false)
	// private AclObject objectIdentity;

	@Column(name = "acl_object_identity", unique = true)
	private String objectIdentity;

	@Column(name = "ace_order")
	private Integer order;

	@ManyToOne(/* cascade=CascadeType.ALL, */fetch = FetchType.EAGER)
	@JoinColumn(name = "sid", referencedColumnName = "id", nullable = false, updatable = false)
	private AclSid sid;

	@Column(name = "mask")
	private Integer mask;

	@Column(name = "granting")
	private Boolean granting;

	@Column(name = "audit_success")
	private Boolean auditSuccess;

	@Column(name = "audit_failure")
	private Boolean auditFailure;

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getObjectIdentity() {
		return objectIdentity;
	}

	public void setObjectIdentity(String objectIdentity) {
		this.objectIdentity = objectIdentity;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public AclSid getSid() {
		return sid;
	}

	public void setSid(AclSid sid) {
		this.sid = sid;
	}

	public Integer getMask() {
		return mask;
	}

	public void setMask(Integer mask) {
		this.mask = mask;
	}

	public Boolean getGranting() {
		return granting;
	}

	public void setGranting(Boolean granting) {
		this.granting = granting;
	}

	public Boolean getAuditSuccess() {
		return auditSuccess;
	}

	public void setAuditSuccess(Boolean auditSuccess) {
		this.auditSuccess = auditSuccess;
	}

	public Boolean getAuditFailure() {
		return auditFailure;
	}

	public void setAuditFailure(Boolean auditFailure) {
		this.auditFailure = auditFailure;
	}

	@Override
	public boolean isNew() {
		return this.getId() == null;
	}

}
