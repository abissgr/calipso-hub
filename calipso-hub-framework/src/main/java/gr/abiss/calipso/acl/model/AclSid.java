package gr.abiss.calipso.acl.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.domain.Persistable;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

@Entity
@Table(name = "acl_sid")
public class AclSid implements Persistable<Long> {

	private static final long serialVersionUID = -6859990313570031965L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true)
	private Long id;

	@Column(name = "principal")
	private Boolean principal;

	@Column(name = "sid")
	private String sid;

	public AclSid(String sid) {
		Assert.hasText(sid, "SID required");
		this.sid = sid;
	}

	public AclSid(Authentication authentication) {
		Assert.notNull(authentication, "Authentication required");
		Assert.notNull(authentication.getPrincipal(), "Principal required");

		if (authentication.getPrincipal() instanceof UserDetails) {
			this.sid = ((UserDetails) authentication.getPrincipal())
					.getUsername();
		} else {
			this.sid = authentication.getPrincipal().toString();
		}
	}

	@Override
	public boolean equals(Object object) {
		if ((object == null) || !(object instanceof PrincipalSid)) {
			return false;
		}

		// Delegate to getPrincipal() for the actual comparison
		return ((PrincipalSid) object).getPrincipal().equals(
				this.getPrincipal());
	}

	@Override
	public int hashCode() {
		return this.getPrincipal().hashCode();
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getPrincipal() {
		return principal;
	}

	public void setPrincipal(Boolean principal) {
		this.principal = principal;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	@Override
	public boolean isNew() {
		return this.getId() == null;
	}
}
