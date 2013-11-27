package gr.abiss.calipso.ddd.core.model.serializers;

import gr.abiss.calipso.ddd.core.model.interfaces.Metadatum;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class MetadataMapDeserializer extends JsonDeserializer<Map<String, ?>>
		implements ContextualDeserializer {

	private static final Logger LOGGER = LoggerFactory.getLogger(MetadataMapDeserializer.class);

	private Class<?> targetType;
	private ObjectMapper mapper;

	public MetadataMapDeserializer() {
		super();
	}

	public MetadataMapDeserializer(Class<?> targetType, ObjectMapper mapper) {
		super();
		this.targetType = targetType;
		this.mapper = mapper;
		// LOGGER.info("constructor, targetType: " + targetType);
	}

	@Override
	public JsonDeserializer<Map<String, ?>> createContextual(
			DeserializationContext ctxt, BeanProperty property)
			throws JsonMappingException {
		return new MetadataMapDeserializer(property.getType().containedType(1)
				.getRawClass(), mapper);
	}

	@Override
	public Map<String, ?> deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		// Metadatum metadatum = targetType.newInstance();
		// metadatum.setObject(jp.getText());
		// JsonNode node = jp.readValueAsTree();
		// if ("".equals(node.asText()))
		// return null;
		if (this.mapper == null) {
			this.mapper = new ObjectMapper();
		}
		TypeFactory typeFactory = mapper.getTypeFactory();
		MapType stringValueMapType = typeFactory.constructMapType(
				HashMap.class, String.class, String.class);
		HashMap<String, String> stringValueMap = mapper.readValue(jp,
				stringValueMapType);
		// LOGGER.info("deserialize stringValueMap: " + stringValueMap);
		Map<String, Metadatum> metadata = new HashMap<String, Metadatum>();
		if (!CollectionUtils.isEmpty(stringValueMap)) {
			for (String predicate : stringValueMap.keySet()) {
				try {
					Metadatum metadatum = (Metadatum) targetType.newInstance();
					metadatum.setPredicate(predicate);
					metadatum.setObject(stringValueMap.get(predicate));
					metadata.put(predicate, metadatum);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return metadata;
	}
}
