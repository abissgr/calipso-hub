/**
 * Copyright (c) 2007 - 2013 www.Abiss.gr
 *
 * This file is part of Calipso, a software platform by www.Abiss.gr.
 *
 * Calipso is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Calipso is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Calipso. If not, see http://www.gnu.org/licenses/agpl.html
 */
package gr.abiss.calipso.jpasearch.json.serializer;

import gr.abiss.calipso.jpasearch.annotation.FieldFormFieldAsJasonConfig;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;

import org.apache.commons.beanutils.PropertyUtilsBean;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ClassToFormSerializer extends JsonSerializer<Class> {

	private static final HashMap<String, String> CONFIG_CACHE = new HashMap<String, String>();

	@Override
	public void serialize(Class value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonGenerationException {

		if (null == value) {
			// write the word 'null' if there's no value available
			jgen.writeNull();
		} else {

			jgen.writeStartObject();
			PropertyDescriptor[] descriptors = new PropertyUtilsBean()
					.getPropertyDescriptors(value);
			for (int i = 0; i < descriptors.length; i++) {
				PropertyDescriptor descriptor = descriptors[i];
				String name = descriptor.getName();
				jgen.writeFieldName(name);
				jgen.writeRawValue(getFormFieldConfig(value, name));
			}
			jgen.writeEndObject();
		}
	}

	private static String getFormFieldConfig(Class<?> clazz, String fieldName) {
		Field field = null;
		String formConfig = null;
		String key = clazz.getName() + "#" + fieldName;
		formConfig = CONFIG_CACHE.get(key);

		if (formConfig == null) {
			Class<?> tmpClass = clazz;
			do {
				for (Field tmpField : tmpClass.getDeclaredFields()) {
					String candidateName = tmpField.getName();
					if (candidateName.equals(fieldName)) {

						field = tmpField;
						if (field
								.isAnnotationPresent(FieldFormFieldAsJasonConfig.class)) {
							FieldFormFieldAsJasonConfig jsonConfig = field
									.getAnnotation(FieldFormFieldAsJasonConfig.class);

							formConfig = jsonConfig.search();
						}
						break;
					}
				}
				tmpClass = tmpClass.getSuperclass();
			} while (tmpClass != null && field == null);
		}
		if (field == null) {
			// LOGGER.warn("Field '" + fieldName + "' not found on class "
			// + clazz);
			// HashMap handles null values so we can use containsKey to cach
			// invalid fields and hence skip the reflection scan
			CONFIG_CACHE.put(key, "'Text'");
		}
		return formConfig;
	}
}