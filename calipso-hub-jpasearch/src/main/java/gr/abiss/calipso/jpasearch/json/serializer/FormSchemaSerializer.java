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

import gr.abiss.calipso.jpasearch.annotation.FormSchemaEntry;
import gr.abiss.calipso.jpasearch.model.FormSchema;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class FormSchemaSerializer extends JsonSerializer<FormSchema> {

	private static final Logger LOGGER = LoggerFactory.getLogger(FormSchemaSerializer.class);

	private static final HashMap<String, String> CONFIG_CACHE = new HashMap<String, String>();

	@Override
	public void serialize(FormSchema schema, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonGenerationException {
		Class domainClass = schema.getDomainClass();

		if (null == domainClass) {
			// write the word 'null' if there's no value available
			jgen.writeNull();
		} else {

			PropertyDescriptor[] descriptors = new PropertyUtilsBean()
					.getPropertyDescriptors(domainClass);

			StringBuffer buf = new StringBuffer("{\n");

			for (int i = 0; i < descriptors.length; i++) {
				PropertyDescriptor descriptor = descriptors[i];
				String name = descriptor.getName();
				String fieldValue = getFormFieldConfig(domainClass, name,
						schema.getType());
				if (fieldValue != null && !fieldValue.equalsIgnoreCase("skip")) {
					if (i > 0) {
						buf.append(",");
					}
					buf.append("\n   \"").append(name).append("\": ")
							.append(fieldValue);
				}
			}
			buf.append("}");
			jgen.writeRaw(buf.toString());
		}
	}

	private static String getFormFieldConfig(Class domainClass,
			String fieldName, FormSchema.Type type) {
		Field field = null;
		String formConfig = null;
		String key = domainClass.getName() + "#" + fieldName;
		formConfig = CONFIG_CACHE.get(key);

		if (formConfig == null) {
			Class tmpClass = domainClass;
			do {
				for (Field tmpField : tmpClass.getDeclaredFields()) {
					String candidateName = tmpField.getName();
					if (candidateName.equals(fieldName)) {

						field = tmpField;
						FormSchemaEntry jsonConfig = null;
						if (field.isAnnotationPresent(FormSchemaEntry.class)) {
							jsonConfig = field.getAnnotation(FormSchemaEntry.class);
							if (FormSchema.Type.CREATE.equals(type)) {
								formConfig = jsonConfig.search();
							} else if (FormSchema.Type.UPDATE.equals(type)) {
								formConfig = jsonConfig.update();
							} else {
								formConfig = jsonConfig.search();
							}
							// skip if flagged as such
							formConfig = formConfig.equalsIgnoreCase("skip") ? null : formConfig;
						}
						else{
							formConfig = "\"Text\"";
						}
						break;
					}
				}
				tmpClass = tmpClass.getSuperclass();
			} while (tmpClass != null && field == null);
		}

		// HashMap handles null values so we can use containsKey to cach
		// invalid fields and hence skip the reflection scan
		CONFIG_CACHE.put(key, formConfig);
		return formConfig;
	}
}