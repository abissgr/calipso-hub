package gr.abiss.calipso.friends.binding;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import gr.abiss.calipso.friends.model.FriendshipId;

public class FriendshipIdSerializer extends JsonSerializer<FriendshipId> {

	@Override
	public void serialize(FriendshipId id, JsonGenerator gen, SerializerProvider provider)
			throws IOException, JsonProcessingException {

		gen.writeString(id.toStringRepresentation());
	}

}