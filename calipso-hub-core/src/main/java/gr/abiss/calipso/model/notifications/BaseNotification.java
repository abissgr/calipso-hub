package gr.abiss.calipso.model.notifications;

import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.entities.AbstractPersistable;
import gr.abiss.calipso.model.serializers.DateTimeToUnixTimestampSerializer;

import java.util.Date;

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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 *Base nitification class
 */
@Entity
@Table(name = "base_notification")
@Inheritance(strategy = InheritanceType.JOINED)
public class BaseNotification extends AbstractPersistable {

	
	private static final long serialVersionUID = -473785131521745319L;

	@ManyToOne(/* cascade=CascadeType.ALL, */fetch = FetchType.EAGER)
	@JoinColumn(name = "actor", referencedColumnName = "id", nullable = false, updatable = false)
	private User actor;

	@ManyToOne(/* cascade=CascadeType.ALL, */fetch = FetchType.EAGER)
	@JoinColumn(name = "type", referencedColumnName = "id", nullable = false, updatable = false)
	private NotificationType type;
	
	@Column(name = "seen", nullable = false)
	private Boolean seen = false;

	@Column(name = "timestamp", nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@JsonSerialize(using = DateTimeToUnixTimestampSerializer.class)
	private Date timestamp;

	public BaseNotification() {
		super();
	}

	public BaseNotification(String id) {
		this.setId(id);
	}

	public BaseNotification(User actor, NotificationType type, Boolean seen, Date timestamp) {
		super();
		this.actor = actor;
		this.type = type;
		this.seen = seen;
		this.timestamp = timestamp;
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
		BaseNotification that = (BaseNotification) obj;
		return null == this.getId() ? false : this.getId().equals(that.getId());
	}

	public User getActor() {
		return actor;
	}

	public void setActor(User actor) {
		this.actor = actor;
	}

	public NotificationType getAction() {
		return type;
	}

	public void setAction(NotificationType type) {
		this.type = type;
	}


	public Boolean getSeen() {
		return seen;
	}

	public void setSeen(Boolean seen) {
		this.seen = seen;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}
