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
package gr.abiss.calipso.jpasearch.json.serializer;

import gr.abiss.calipso.jpasearch.annotation.FormSchemas;
import gr.abiss.calipso.jpasearch.model.FormSchema;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

//import org.apache.commons.beanutils.PropertyUtilsBean;

public class FormSchemaSerializer extends JsonSerializer<FormSchema> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(FormSchemaSerializer.class);

	private static final char quote = '\"';
	private static final char space = ' ';
	private static final char colon = ':';
	private static final char comma = ',';

	private static final HashMap<String, String> CONFIG_CACHE = new HashMap<String, String>();

	private static List<String> ignoredFieldNames = new LinkedList<String>();
	static{
		ignoredFieldNames.add("new");
		ignoredFieldNames.add("class");
		ignoredFieldNames.add("metadataDomainClass");
		
		
	}
	
	@Override
	public void serialize(FormSchema schema, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonGenerationException {
		try{
			Class domainClass = schema.getDomainClass();
			
			if (null == domainClass) {
				throw new RuntimeException("formSchema has no domain class set");
			} else {
				jgen.writeStartObject();
			    
			    
				PropertyDescriptor[] descriptors = new PropertyUtilsBean()
						.getPropertyDescriptors(domainClass);

				for (int i = 0; i < descriptors.length; i++) {

					PropertyDescriptor descriptor = descriptors[i];
					String name = descriptor.getName();
					if(!ignoredFieldNames.contains(name)){
						jgen.writeFieldName(name);
						jgen.writeStartObject();
					    
//						
						String fieldValue = getFormFieldConfig(domainClass, name);
						if(StringUtils.isNotBlank(fieldValue)){
							jgen.writeRaw(fieldValue);
						}
						
						
					    jgen.writeEndObject();
					}
					
					
					
				    
				}

			    jgen.writeEndObject();
				
			}
		
		}
		catch(Exception e){
			LOGGER.error("Failed serializing form schema", e);
		}
	}

	private static String getFormFieldConfig(Class domainClass,
			String fieldName) {
		String formSchemaJson = null;
		Field field = null;
		StringBuffer formConfig = new StringBuffer();
		String key = domainClass.getName() + "#" + fieldName;
		String cached = CONFIG_CACHE.get(key);
		if(StringUtils.isNotBlank(cached)){
			formConfig.append(cached);
		}else{
			Class tmpClass = domainClass;
			do {
				for (Field tmpField : tmpClass.getDeclaredFields()) {
					String candidateName = tmpField.getName();
					if (candidateName.equals(fieldName)) {
						field = tmpField;
						FormSchemas formSchemasAnnotation = null;
						if (field.isAnnotationPresent(FormSchemas.class)) {
							formSchemasAnnotation = field.getAnnotation(FormSchemas.class);
							gr.abiss.calipso.jpasearch.annotation.FormSchemaEntry[] formSchemas = formSchemasAnnotation.value();
							LOGGER.info("getFormFieldConfig, formSchemas: "+formSchemas);
							if(formSchemas != null){
								for(int i=0; i < formSchemas.length; i++){
									if(i > 0){
										formConfig.append(comma);
									}
									gr.abiss.calipso.jpasearch.annotation.FormSchemaEntry formSchemaAnnotation = formSchemas[i];
									LOGGER.info("getFormFieldConfig, formSchemaAnnotation: "+formSchemaAnnotation);
									appendFormFieldSchema(formConfig,formSchemaAnnotation.state(),formSchemaAnnotation.json());
								}
							}
							//formConfig = formSchemasAnnotation.json();
						}
						else{
							appendFormFieldSchema(formConfig,gr.abiss.calipso.jpasearch.annotation.FormSchemaEntry.STATE_DEFAULT, gr.abiss.calipso.jpasearch.annotation.FormSchemaEntry.TYPE_STRING);
						}
						break;
					}
				}
				tmpClass = tmpClass.getSuperclass();
			} while (tmpClass != null && field == null);
			formSchemaJson = formConfig.toString();
			CONFIG_CACHE.put(key, formSchemaJson);
		}

		return formSchemaJson;
	}

	private static void appendFormFieldSchema(
			StringBuffer formConfig,String state, String json) {
		formConfig.append(quote).append(state).append(quote).append(colon).append(json);
	}
}