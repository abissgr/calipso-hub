package gr.abiss.calipso.ddd.core.model.interfaces;

import java.util.Map;

public interface MetadataSubject<M extends Metadatum> {

	Map<String, M> getMetadata();

	void setMetadata(Map<String, M> metadata);

	M addMetadatum(M metadatum);

	M addMetadatum(String predicate, String object);

	Class<? extends Metadatum> getMetadataDomainClass();
}
