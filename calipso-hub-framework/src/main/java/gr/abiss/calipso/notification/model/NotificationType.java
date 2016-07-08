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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;

import gr.abiss.calipso.model.base.AbstractSystemUuidPersistable;

@Entity
@Table(name = "notification_type")
@Inheritance(strategy = InheritanceType.JOINED)
public class NotificationType extends AbstractSystemUuidPersistable {

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
