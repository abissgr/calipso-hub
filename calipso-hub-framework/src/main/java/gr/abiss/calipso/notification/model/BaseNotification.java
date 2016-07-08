/**
 * calipso-hub-framework - A full stack, high level framework for lazy application hackers.
 * Copyright © 2005 Manos Batsis (manosbatsis gmail)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package gr.abiss.calipso.notification.model;

import java.util.Date;

import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.base.AbstractSystemUuidPersistable;
import gr.abiss.calipso.model.serializers.DateTimeToUnixTimestampSerializer;
import gr.abiss.calipso.tiers.annotation.CurrentPrincipalField;
import gr.abiss.calipso.tiers.annotation.ModelResource;
import io.swagger.annotations.ApiModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
//import com.wordnik.swagger.annotations.ApiModel;

/**
 * Base notification class, corresponds to a Log Table that aggregates basic information for all 
 * notifications.  
 */
@CurrentPrincipalField( value = "recepient", ignoreforRoles = {"ROLE_ADMIN", "ROLE_SITE_OPERATOR"} )
@Entity
@Table(name = "base_notification")
@Inheritance(strategy = InheritanceType.JOINED)
@ModelResource(path = "baseNotifications", apiName = "Notifications", apiDescription = "Operations about notifications")
@ApiModel(value = "Notifications", description = "A model representing a notification addressed to a user.")
public class BaseNotification extends AbstractSystemUuidPersistable {

	
	private static final long serialVersionUID = -473785131521745319L;


	@Column(name = "url", nullable = true)
	private String url;
	
	@ManyToOne(/* cascade=CascadeType.ALL, */fetch = FetchType.EAGER)
	@JoinColumn(name = "actor", referencedColumnName = "id", nullable = false, updatable = false)
	private User actor;

	@ManyToOne(/* cascade=CascadeType.ALL, */fetch = FetchType.EAGER)
	@JoinColumn(name = "recepient", referencedColumnName = "id", nullable = false, updatable = false)
	private User recepient;

	@ManyToOne(/* cascade=CascadeType.ALL, */fetch = FetchType.EAGER)
	@JoinColumn(name = "type", referencedColumnName = "id", nullable = true, updatable = false)
	private NotificationType type;
	
	@Column(name = "seen", nullable = false)
	private Boolean seen = false;

	@Temporal(TemporalType.TIMESTAMP)
	@JsonSerialize(using = DateTimeToUnixTimestampSerializer.class)
	private Date createdDate;

	public BaseNotification() {
		super();
	}

	public BaseNotification(String id) {
		this.setId(id);
	}

	public BaseNotification(User actor, User recepient, NotificationType type) {
		this(actor, recepient, type, new Date());
	}
	
	public BaseNotification(User actor, User recepient, NotificationType type, Date createdDate) {
		this(actor, recepient, type, createdDate, false);
	}
	
	public BaseNotification(User actor, User recepient, NotificationType type, Date createdDate, Boolean seen) {
		super();
		this.actor = actor;
		this.recepient = recepient;
		this.type = type;
		this.createdDate = createdDate;
		this.seen = seen;
	}

	@Override
	public boolean equals(Object obj) {
		if (null == obj) {
			return false;
		}

		if (this == obj) {
			return true;
		}
		if (!(obj instanceof BaseNotification)) {
			return false;
		}
		BaseNotification other = (BaseNotification) obj;
		EqualsBuilder builder = new EqualsBuilder();
        builder.append(this.getId(), other.getId());
        builder.append(this.getActor(), other.getActor());
        builder.append(this.getRecepient(), other.getRecepient());
        
        return builder.isEquals();
	}

	
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public User getActor() {
		return actor;
	}

	public void setActor(User actor) {
		this.actor = actor;
	}


	public User getRecepient() {
		return recepient;
	}

	public void setRecepient(User recepient) {
		this.recepient = recepient;
	}

	public NotificationType getType() {
		return type;
	}

	public void setType(NotificationType type) {
		this.type = type;
	}


	public Boolean getSeen() {
		return seen;
	}

	public void setSeen(Boolean seen) {
		this.seen = seen;
	}

	public DateTime getCreatedDate() {
		return null == createdDate ? null : new DateTime(createdDate);
	}

	public void setCreatedDate(final DateTime createdDate) {
		this.createdDate = null == createdDate ? null : createdDate.toDate();
	}

}
