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
package gr.abiss.calipso.uischema.serializer;

import gr.abiss.calipso.tiers.annotation.ModelResource;
import gr.abiss.calipso.uischema.annotation.FormSchemas;
import gr.abiss.calipso.uischema.model.UiSchema;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;


public class UiSchemaSerializer extends JsonSerializer<UiSchema> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(UiSchemaSerializer.class);

	private static final char quote = '\"';
	private static final char space = ' ';
	private static final char colon = ':';
	private static final char comma = ',';
	
	private static Map<String, String> fieldTypes = new HashMap<String, String>();
	static{
//			  "fields" : {
//			    "aliases" : {
//			      "fieldType" : "Set"
//			    },
//			    "createdDate" : {
//			      "fieldType" : "DateTime"
//			    },
//			    "createdBy" : {
//			      "fieldType" : "User"
//			    },
//			    "lastModifiedDate" : {
//			      "fieldType" : "DateTime"
//			    },
//			    "domain" : {
//			      "fieldType" : "String"
//			    },
//			    "formSchema" : {
//			      "fieldType" : "FormSchema"
//			    },
//			    "lastModifiedBy" : {
//			      "fieldType" : "User"
//			    },
//			    "name" : {
//			      "fieldType" : "String"
//			    },
//			    "id" : {
//			      "fieldType" : "String"
//			    }
//			  }
//			}
		fieldTypes.put("String", "String");
		
		fieldTypes.put("Short", "Number");
		fieldTypes.put("Integer", "Number");

		fieldTypes.put("Float", "Decimal");
		fieldTypes.put("BigDecimal", "Decimal");
		fieldTypes.put("Double", "Decimal");
		
		fieldTypes.put("DateTime", "Datetime");
		fieldTypes.put("Datetime", "Datetime");
		fieldTypes.put("Date", "Date");
	}
	
	// datatypes

	private static final HashMap<String, String> CONFIG_CACHE = new HashMap<String, String>();

	private static List<String> ignoredFieldNames = new LinkedList<String>();
	static{
		ignoredFieldNames.add("new");
		ignoredFieldNames.add("class");
		ignoredFieldNames.add("metadataDomainClass");
	}
	
	@Override
	public void serialize(UiSchema schema, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonGenerationException {
		try{
			Class domainClass = schema.getDomainClass();
			
			if (null == domainClass) {
				throw new RuntimeException("formSchema has no domain class set");
			} else {
				// start json
				jgen.writeStartObject();
				
				// write superclass hint
				ModelResource superResource = (ModelResource) domainClass.getSuperclass().getAnnotation(ModelResource.class);
				if(superResource != null){
					jgen.writeFieldName("superPathFragment");
					jgen.writeString(superResource.path());
				}

				// write pathFragment
				ModelResource modelResource = (ModelResource) domainClass.getAnnotation(ModelResource.class);
				jgen.writeFieldName("pathFragment");
				jgen.writeString(modelResource.path());
				
				// write simple class name
				jgen.writeFieldName("simpleClassName");
				jgen.writeString(domainClass.getSimpleName());
				
				
				// start fields
				jgen.writeFieldName("fields");
				jgen.writeStartObject();
			    
				PropertyDescriptor[] descriptors = new PropertyUtilsBean()
						.getPropertyDescriptors(domainClass);

				for (int i = 0; i < descriptors.length; i++) {

					PropertyDescriptor descriptor = descriptors[i];
					String name = descriptor.getName();
					if(!ignoredFieldNames.contains(name)){
						String fieldValue = this.getDataType(domainClass, descriptor, name);
						if(StringUtils.isNotBlank(fieldValue)){
							jgen.writeFieldName(name);
							jgen.writeStartObject();
							jgen.writeFieldName("fieldType");
							jgen.writeString(fieldValue);
						    jgen.writeEndObject();
						}
					}
					
					
					
				    
				}
				// end fields
				jgen.writeEndObject();
				// end json
			    jgen.writeEndObject();
				
			}
		
		}
		catch(Exception e){
			new RuntimeException("Failed serializing form schema", e);
		}
	}

	private String getDataType(Class domainClass, PropertyDescriptor descriptor, String name) {
		return fieldTypes.get(descriptor.getPropertyType().getSimpleName());
	}

	private static String getFormFieldConfig(Class domainClass, PropertyDescriptor descriptor, String fieldName) {
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
							gr.abiss.calipso.uischema.annotation.FormSchemaEntry[] formSchemas = formSchemasAnnotation.value();
							LOGGER.info("getFormFieldConfig, formSchemas: "+formSchemas);
							if(formSchemas != null){
								for(int i=0; i < formSchemas.length; i++){
									if(i > 0){
										formConfig.append(comma);
									}
									gr.abiss.calipso.uischema.annotation.FormSchemaEntry formSchemaAnnotation = formSchemas[i];
									LOGGER.info("getFormFieldConfig, formSchemaAnnotation: "+formSchemaAnnotation);
									appendFormFieldSchema(formConfig,formSchemaAnnotation.state(),formSchemaAnnotation.json());
								}
							}
							//formConfig = formSchemasAnnotation.json();
						}
						else{
							appendFormFieldSchema(formConfig,gr.abiss.calipso.uischema.annotation.FormSchemaEntry.STATE_DEFAULT, gr.abiss.calipso.uischema.annotation.FormSchemaEntry.TYPE_STRING);
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