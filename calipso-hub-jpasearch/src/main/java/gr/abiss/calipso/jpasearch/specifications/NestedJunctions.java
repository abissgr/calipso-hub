package gr.abiss.calipso.jpasearch.specifications;

import java.util.HashMap;
import java.util.Map;

class NestedJunctions {
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
			GenericSpecifications.LOGGER.warn("Ignoring invalid path for nested/junctioned param: "
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