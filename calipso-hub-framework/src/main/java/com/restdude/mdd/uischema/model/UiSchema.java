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
package com.restdude.mdd.uischema.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.restdude.mdd.uischema.serializer.UiSchemaSerializer;
import org.springframework.data.domain.Persistable;

/**
 * A simple wrapper for domain entity class information, used for serializing those to JSON form
 * schemas for UI views.
 */
@JsonSerialize(using = UiSchemaSerializer.class)
public class UiSchema {
	
	private Class domainClass = null;
	private String json = null;
	
	public UiSchema(Class domainClass) {
		this.domainClass = domainClass;
	}

	public UiSchema() {
	}

	public Class<? extends Persistable> getDomainClass() {
		return domainClass;
	}

	public void setDomainClass(Class domainClass) {
		this.domainClass = domainClass;
	}


	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

}
