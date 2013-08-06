/**
 * Copyright (c) 2007 - 2013 www.Abiss.gr
 *
 * This file is part of Calipso, a software platform by www.Abiss.gr.
 *
 * Calipso is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Calipso is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser Public License
 * along with Calipso. If not, see http://www.gnu.org/licenses/lgpl-3.0.txt
 */
package gr.abiss.calipso.jpasearch.model;

import gr.abiss.calipso.jpasearch.json.serializer.FormSchemaSerializer;

import org.springframework.data.domain.Persistable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A simple wrapper used when we want to serialize classes as form schemas
 * without using the Class type to associate with a proper JSON serializer.
 * 
 * The wrapper also carries the schema mode i.e. which one of CREATE, UPDATE,
 * SEARCH
 */
@JsonSerialize(using = FormSchemaSerializer.class)
public class FormSchema {

	public static enum Type {
		CREATE, UPDATE, SEARCH;
	}

	private Type type = Type.SEARCH;

	public Class<? extends Persistable> domainClass = null;

	public FormSchema(Class<? extends Persistable> domainClass) {
		this.domainClass = domainClass;
	}

	public FormSchema() {
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Class<? extends Persistable> getDomainClass() {
		return domainClass;
	}

	public void setDomainClass(Class<? extends Persistable> domainClass) {
		this.domainClass = domainClass;
	}
}
