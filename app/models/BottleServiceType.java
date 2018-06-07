package models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by arkady on 04/03/16.
 */
public enum BottleServiceType {
	TABLE("Table"),
	STANDUP("Standup"),
	NONE("Guest list"),
	BAR("Bar");

	private String name;
	private static Map<String, String> typeToNameMapping;

	BottleServiceType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	public static Map<String, String> all() {
		if (typeToNameMapping == null) {
			initMapping();
		}

		return typeToNameMapping;
	}

	private static void initMapping() {
		typeToNameMapping = new HashMap<>();
		for (BottleServiceType s : values()) {
			typeToNameMapping.put(s.name(), s.name);
		}
	}
}
