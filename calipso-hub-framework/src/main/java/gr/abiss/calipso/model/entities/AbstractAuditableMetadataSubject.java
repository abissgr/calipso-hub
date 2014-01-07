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
package gr.abiss.calipso.model.entities;

import gr.abiss.calipso.model.interfaces.MetadataSubject;
import gr.abiss.calipso.model.interfaces.Metadatum;
import gr.abiss.calipso.model.serializers.MetadataMapDeserializer;
import gr.abiss.calipso.model.serializers.MetadatumToStringValueSerializer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.FetchType;
import javax.persistence.MapKey;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

import org.jodah.typetools.TypeResolver;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Abstract base persistent class for auditable metadata bearing classes.
 * Implementations can override relational specifics via
 * javax.persistence.AssociationOverride annotations
 */
@MappedSuperclass
public abstract class AbstractAuditableMetadataSubject<M extends Metadatum, U extends AbstractPersistable>
		extends AbstractAuditable<U> implements MetadataSubject<M> {

	private static final long serialVersionUID = -1468517690700208260L;
	
	@OneToMany(mappedBy = "subject", fetch = FetchType.EAGER)
	@MapKey(name = "predicate")
	@JsonDeserialize(using = MetadataMapDeserializer.class)
	@JsonSerialize(contentUsing = MetadatumToStringValueSerializer.class)
	private Map<String, M> metadata;

	public AbstractAuditableMetadataSubject() {
		super();
	}

	@JsonIgnore
	@Override
	public abstract Class<M> getMetadataDomainClass();

	@Override
	public Map<String, M> getMetadata() {
		return metadata;
	}

	@Override
	public void setMetadata(Map<String, M> metadata) {
		this.metadata = metadata;
	}

	@Override
	public M addMetadatum(M metadatum) {
		if (this.getMetadata() == null) {
			this.setMetadata(new HashMap<String, M>());
		}
		metadatum.setSubject(this);
		return this.getMetadata().put(metadatum.getPredicate(), metadatum);
	}

	@Override
	public void addMetadata(Collection<M> metadata) {
		if (!CollectionUtils.isEmpty(metadata)) {
			for (M metadatum : metadata) {
				this.addMetadatum(metadatum);
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public M addMetadatum(String predicate, String object) {
		Class<?> metadatumClass = TypeResolver.resolveRawArguments(
				AbstractAuditableMetadataSubject.class, getClass())[0];
		M metadatum = null;
		try {
			metadatum = (M) metadatumClass.getConstructor(String.class,
					String.class).newInstance(predicate, object);
		} catch (Exception e) {
			throw new RuntimeException("Failed adding metadatum", e);
		}
		return this.addMetadatum(metadatum);
	}

}