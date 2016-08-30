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
package gr.abiss.calipso.model.base;

import gr.abiss.calipso.model.interfaces.ReportDataSetSubject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * Resource categories are hierarchical, have aliases and can be used as tags
 */
@MappedSuperclass
public abstract class AbstractCategory<T extends AbstractCategory<T>> extends AuditableResource<T>{

	private static final long serialVersionUID = -1329254539598110186L;

	public AbstractCategory() {
		super();
	}
	public AbstractCategory(String name) {
		super(name);
	}

	public AbstractCategory(String name, T parent) {
		super(name, parent);
	}
	
	@Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AbstractCategory<?>)) {
            return false;
        }
        AbstractCategory<?> that = (AbstractCategory<?>) obj;
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(this.getName(), that.getName());
        eb.append(this.getPath(), that.getPath());
        return eb.isEquals();
    }

	
}
