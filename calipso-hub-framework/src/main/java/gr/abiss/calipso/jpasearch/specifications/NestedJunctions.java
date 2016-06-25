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
package gr.abiss.calipso.jpasearch.specifications;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: this is half-done. To make sense, this needs to allow for different tests than simple equality
class NestedJunctions {
	private static final Logger LOGGER = LoggerFactory.getLogger(NestedJunctions.class);
	
	private final Map<String, Map<String, String[]>> andJunctions = new HashMap<String, Map<String, String[]>>();
	private final Map<String, Map<String, String[]>> orJunctions = new HashMap<String, Map<String, String[]>>();

	public NestedJunctions() {
		
	}
	public boolean addIfNestedJunction(String name, String[] values) {
		boolean added = false;
		if (name.startsWith("and:")) {
			addJunction(andJunctions, name.substring(4), values);
			added = true;
		} else if (name.startsWith("or:")) {
			addJunction(orJunctions, name.substring(3), values);
			added = true;
		}
		return added;
	}

	private void addJunction(Map<String, Map<String, String[]>> junctions,
			String path, String[] values) {
		String[] junctionKeyAndPropName = path.split(":");
		if (junctionKeyAndPropName.length != 2) {
			LOGGER.warn("Ignoring invalid path for nested/junctioned param: "
					+ path);
		} else {
			Map<String, String[]> groupedPrams = junctions
					.get(junctionKeyAndPropName[0]);
			if (groupedPrams == null) {
				groupedPrams = new HashMap<String, String[]>();
				junctions.put(junctionKeyAndPropName[0], groupedPrams);
			}
			groupedPrams.put(junctionKeyAndPropName[1], values);
		}

	}

	public Map<String, Map<String, String[]>> getAndJunctions() {
		return andJunctions;
	}

	public Map<String, Map<String, String[]>> getOrJunctions() {
		return orJunctions;
	}
}