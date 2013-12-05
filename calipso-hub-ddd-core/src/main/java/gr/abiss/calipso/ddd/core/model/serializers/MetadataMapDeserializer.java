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
package gr.abiss.calipso.ddd.core.model.serializers;

import gr.abiss.calipso.ddd.core.model.interfaces.Metadatum;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

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
//		TypeFactory typeFactory = mapper.getTypeFactory();
//		TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {
//		};
//		Map<String, String> stringValueMap = jp.readValueAsTree().readValueAs(typeRef);// mapper.readValue(jp,
		TreeNode metadataTreeNode = jp.readValueAsTree();
		Iterator<String> names = metadataTreeNode.fieldNames();
		LOGGER.info("metadatum metadataTreeNode: " + metadataTreeNode
				+ " field names: " + metadataTreeNode.size()
				+ ", isContainerNode: " + metadataTreeNode.isContainerNode()
				+ ", isObject: " + metadataTreeNode.isObject()
				+ ", isValueNode: " + metadataTreeNode.isValueNode());
		// LOGGER.info("deserialize stringValueMap: " + stringValueMap);
		Map<String, Metadatum> metadata = new HashMap<String, Metadatum>();
		while (metadataTreeNode.fieldNames().hasNext()) {
			String predicateName = names.next();
			LOGGER.info("metadatum predicateName: " + predicateName);
			try {
				/*** read value from predicate name ***/
				TreeNode predicateObject = metadataTreeNode.path(predicateName);

				LOGGER.info("metadatum predicateObject: " + predicateObject);
				Metadatum metadatum = (Metadatum) targetType.newInstance();
				metadatum.setPredicate(predicateName);
				metadatum.setObject(predicateObject.toString());
				metadata.put(predicateName, metadatum);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		LOGGER.info("deserialize returning metadata: " + metadata);
		return metadata;
	}
}