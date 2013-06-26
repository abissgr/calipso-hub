package gr.abiss.calipso.model.json.serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class DoublePlacesFloatSerializer extends JsonSerializer<Float> {

	@Override
	public void serialize(Float value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {

		if (null == value) {
			// write the word 'null' if there's no value available
			jgen.writeNull();
		} else {
			jgen.writeNumber(Math.round(value * 100.0f) / 100.0f);
		}
	}
}