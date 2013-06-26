package gr.abiss.calipso.model.acl;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.hibernate.annotations.Formula;

/**
 */
@Entity
@Table(name = "base_user_account")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AbstractAccount extends Resource {

	private static final long serialVersionUID = -7443491878604398347L;

	@Column(name = "user_name", unique = true, nullable = false)
	private String userName;

	@Formula(value = "user_name")
	private String businessKey;

	@Column(name = "avatar_url")
	private String avatarUrl;

	@Column(name = "active")
	private Boolean active = true;

	@Column(name = "inactivation_reason")
	private String inactivationReason;

	@Column(name = "inactivation_date")
	private Date inactivationDate;


	public AbstractAccount() {
	}
//	
//	@Override
//	public boolean equals(Object obj) {
//		if (null == obj) {
//			return false;
//		}
//
//		if (this == obj) {
//			return true;
//		}
//		if (!(obj instanceof AbstractAccount)) {
//			return false;
//		}
//		AbstractAccount that = (AbstractAccount) obj;
//		return null == this.getId() ? false : this.getId().equals(that.getId());
//	}
//
//	@Override
//	public String toString() {
//		return new ToStringBuilder(this).appendSuper(super.toString())
//				.append("userName", this.getUserName()).toString();
//	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getInactivationReason() {
		return inactivationReason;
	}

	public void setInactivationReason(String inactivationReason) {
		this.inactivationReason = inactivationReason;
	}

	public Date getInactivationDate() {
		return inactivationDate;
	}

	public void setInactivationDate(Date inactivationDate) {
		this.inactivationDate = inactivationDate;
	}

}