/**
 * Copyright (c) 2007 - 2013 www.Abiss.gr
 *
 * This file is part of Calipso, a software platform by www.Abiss.gr.
 *
 * Calipso is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Calipso is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser Public License
 * along with Calipso. If not, see http://www.gnu.org/licenses/lgpl-3.0.txt
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