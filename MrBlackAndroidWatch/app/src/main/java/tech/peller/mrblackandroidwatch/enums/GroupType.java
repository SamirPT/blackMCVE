package tech.peller.mrblackandroidwatch.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by arkady on 04/03/16.
 */
public enum GroupType {
	MIX("Mix"),
	PROMO("Promo"),
	ALL_GIRLS("All girls"),
	ALL_GUYS("All guys"),
	NONE("None");

	private String name;
	private static Map<String, String> typeToNameMapping;

	GroupType(String name) {
		this.name = name;
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
		for (GroupType s : values()) {
			typeToNameMapping.put(s.name(), s.name);
		}
	}
}
