package gr.abiss.calipso.model.notifications;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;

import gr.abiss.calipso.model.entities.AbstractPersistable;

@Entity
@Table(name = "notification_type")
@Inheritance(strategy = InheritanceType.JOINED)
public class NotificationType extends AbstractPersistable {

	private static final long serialVersionUID = 6168668373348817558L;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "description", nullable = false)
	private String description;
	
	@Column(name = "template", nullable = false, length = 1024)
	private String template;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean equals(Object obj) {
		if (null == obj) {
			return false;
		}

		if (this == obj) {
			return true;
		}
		if (!(obj instanceof NotificationType)) {
			return false;
		}
		NotificationType other = (NotificationType) obj;
		EqualsBuilder builder = new EqualsBuilder();
        builder.append(getId(), other.getId());
        builder.append(getName(), other.getName());
        builder.append(getDescription(), other.getDescription());
        return builder.isEquals();
	}
}
