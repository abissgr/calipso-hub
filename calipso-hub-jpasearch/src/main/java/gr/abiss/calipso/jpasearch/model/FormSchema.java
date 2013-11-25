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

import org.springframework.data.domain.Persistable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A simple wrapper for domain entities, used for serializing those to JSON form
 * schemas without the need to associate the domain entity Class with a proper
 * JSON serializer.
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
