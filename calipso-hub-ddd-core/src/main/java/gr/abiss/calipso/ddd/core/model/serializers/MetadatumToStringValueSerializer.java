package gr.abiss.calipso.ddd.core.model.serializers;

import gr.abiss.calipso.ddd.core.model.interfaces.Metadatum;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class MetadatumToStringValueSerializer extends JsonSerializer<Metadatum> {
	private static final Logger LOGGER = LoggerFactory.getLogger(MetadatumToStringValueSerializer.class);

	@Override
	public void serialize(Metadatum value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonGenerationException {
		// LOGGER.info("serialize value: "+value);
		if (null == value || null == value.getObject()) {
			// write the word 'null' if there's no value available
			jgen.writeNull();
		} else {
			jgen.writeRawValue(new StringBuffer("\"").append(value.getObject())
					.append("\"").toString());
		}
	}
}
