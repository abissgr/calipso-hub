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
 * You should have received a copy of the GNU General Public License
 * along with Calipso. If not, see http://www.gnu.org/licenses/agpl.html
 */
package gr.abiss.calipso.model.attrs;


import gr.abiss.calipso.model.base.AbstractAuditable;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class CustomAttribute<T extends Serializable> extends
		AbstractAuditable {

	private static final long serialVersionUID = 4008152148196369725L;

	@Column(name = "name", nullable = false, updatable = false)
	private String name;

	// @ManyToOne
	// private BaseCustomAttributesEntity subject;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public abstract T getValue();

	public abstract void setValue(T value);

	// public BaseCustomAttributesEntity getSubject() {
	// return subject;
	// }
	//
	// public void setSubject(BaseCustomAttributesEntity subject) {
	// this.subject = subject;
	// }

}
