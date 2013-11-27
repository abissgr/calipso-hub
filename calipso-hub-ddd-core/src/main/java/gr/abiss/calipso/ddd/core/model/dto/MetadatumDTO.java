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
package gr.abiss.calipso.ddd.core.model.dto;

import gr.abiss.calipso.ddd.core.model.interfaces.MetadataSubject;
import gr.abiss.calipso.ddd.core.model.interfaces.Metadatum;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class MetadatumDTO implements Metadatum {

	private static final long serialVersionUID = -1468517690700208260L;

	@JsonIgnore
	private MetadataSubject subject;

	private String predicate;

	private String object;

	public MetadatumDTO() {

	}

	@Override
	public boolean equals(Object obj) {
		if (null == obj) {
			return false;
		}

		if (this == obj) {
			return true;
		}
		if (!MetadatumDTO.class.isInstance(obj)) {
			return false;
		}
		@SuppressWarnings("rawtypes")
		MetadatumDTO that = (MetadatumDTO) obj;
		return new EqualsBuilder().append(this.getSubject(), that.getSubject())
				.append(this.getPredicate(), that.getPredicate()).isEquals();
	}

	@Override
	public MetadataSubject getSubject() {
		return subject;
	}

	@Override
	public void setSubject(MetadataSubject subject) {
		this.subject = subject;
	}

	@Override
	public String getPredicate() {
		return predicate;
	}

	@Override
	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}

	@Override
	public String getObject() {
		return object;
	}

	@Override
	public void setObject(String object) {
		this.object = object;
	}

}
