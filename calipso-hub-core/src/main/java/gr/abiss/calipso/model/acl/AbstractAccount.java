/**
 * Copyright (c) 2007 - 2013 www.Abiss.gr
 *
 * This file is part of Calipso, a software platform by www.Abiss.gr.
 *
 * Calipso is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Calipso is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Calipso. If not, see http://www.gnu.org/licenses/agpl.html
 */
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