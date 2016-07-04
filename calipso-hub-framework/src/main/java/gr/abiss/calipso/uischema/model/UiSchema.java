/**
 *
 *
 * Copyright (c) 2007 - 2013 www.Abiss.gr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gr.abiss.calipso.uischema.model;

import gr.abiss.calipso.uischema.serializer.UiSchemaSerializer;

import org.springframework.data.domain.Persistable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

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
