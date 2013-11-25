package gr.abiss.calipso.ddd.core.model.dto;

import gr.abiss.calipso.ddd.core.model.interfaces.MetadataSubject;
import gr.abiss.calipso.ddd.core.model.interfaces.Metadatum;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class MetadatumDTO<S extends MetadataSubject> implements Metadatum {

	private static final long serialVersionUID = -1468517690700208260L;

	@JsonIgnore
	private S subject;

	private String predicate;

	private String object;

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

	public S getSubject() {
		return subject;
	}

	public void setSubject(S subject) {
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
