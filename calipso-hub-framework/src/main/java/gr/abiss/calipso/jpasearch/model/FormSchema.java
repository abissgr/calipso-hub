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
package gr.abiss.calipso.jpasearch.model;

import gr.abiss.calipso.jpasearch.json.serializer.FormSchemaSerializer;
import gr.abiss.calipso.model.entities.FormSchemaAware;

import org.springframework.data.domain.Persistable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A simple wrapper for domain entity class information, used for serializing those to JSON form
 * schemas for UI views.
 * 
 * The wrapper also carries the schema mode e.g. CREATE, UPDATE,
 * SEARCH or any custom value if a subset of the complete schema is used.
 */
@JsonSerialize(using = FormSchemaSerializer.class)
public class FormSchema {
	
	public static final String MODE_DEFAULT = "default";
	public static final String MODE_CREATE = "create";
	public static final String MODE_UPDATE = "update";
	public static final String MODE_SEARCH = "search";


	public static void setToInstance(FormSchemaAware formSchemaAware) {
		setToInstance(formSchemaAware, false);
	}
	
	public static void setToInstance(FormSchemaAware formSchemaAware, boolean updateExisting) {
		if(updateExisting || formSchemaAware.getFormSchema() == null){
			formSchemaAware.setFormSchema(new FormSchema(formSchemaAware.getClass()));
		}
	}
	
	private Class<? extends FormSchemaAware> domainClass = null;
	private String json = null;
	private String action = null;
	
	public FormSchema(Class<? extends FormSchemaAware> domainClass) {
		this.domainClass = domainClass;
	}

	public FormSchema() {
	}

	public Class<? extends FormSchemaAware> getDomainClass() {
		return domainClass;
	}

	public void setDomainClass(Class<? extends FormSchemaAware> domainClass) {
		this.domainClass = domainClass;
	}


	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
}
