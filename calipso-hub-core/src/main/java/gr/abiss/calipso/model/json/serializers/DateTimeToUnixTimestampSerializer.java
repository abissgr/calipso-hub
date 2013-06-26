package gr.abiss.calipso.model.json.serializers;

import java.io.IOException;

import org.joda.time.DateTime;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class DateTimeToUnixTimestampSerializer extends JsonSerializer<DateTime> {

	@Override
	public void serialize(DateTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {

		if (null == value) {
			// write the word 'null' if there's no value available
			jgen.writeNull();
		} else {
			jgen.writeNumber(value.toDate().getTime());
		}
	}
}