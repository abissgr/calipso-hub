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
 * You should have received a copy of the GNU Affero General Public License
 * along with Calipso. If not, see http://www.gnu.org/licenses/agpl.html
 */
package gr.abiss.calipso.ddd.core.model.entities;

import gr.abiss.calipso.ddd.core.model.interfaces.MetadataSubject;
import gr.abiss.calipso.ddd.core.model.interfaces.Metadatum;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.FetchType;
import javax.persistence.MapKey;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

import org.jodah.typetools.TypeResolver;

/**
 * Abstract base persistent class for auditable metadata bearing classes.
 * Implementations can override relational specifics via
 * javax.persistence.AssociationOverride annotations
 */
@MappedSuperclass
public abstract class AbstractAuditableMetadataSubject<M extends AbstractAuditabeMetadatum, U>
		extends AbstractAuditable<U> implements MetadataSubject<M> {

	private static final long serialVersionUID = -1468517690700208260L;
	
	@OneToMany(mappedBy = "subject", fetch = FetchType.EAGER)
	@MapKey(name = "predicate")
	private Map<String, M> metadata;

	@Override
	public abstract Class<? extends Metadatum> getMetadataDomainClass();

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