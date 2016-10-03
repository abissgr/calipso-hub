package gr.abiss.calipso.friends.binding;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import gr.abiss.calipso.friends.model.FriendshipId;

public class FriendshipIdDeserializer extends JsonDeserializer<FriendshipId> implements Converter<String, FriendshipId> {

	private static final Logger LOGGER = LoggerFactory.getLogger(FriendshipIdDeserializer.class);

	@Override
	public FriendshipId deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		
		ObjectCodec oc = p.getCodec();
		String id = oc.readValue(p, String.class);
		
        return new FriendshipId(id);
	} 

	@Override
	public FriendshipId convert(String source) {
		return new FriendshipId(source);
	}

}