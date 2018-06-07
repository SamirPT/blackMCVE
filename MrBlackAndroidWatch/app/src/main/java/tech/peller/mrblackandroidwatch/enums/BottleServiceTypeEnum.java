package tech.peller.mrblackandroidwatch.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by arkady on 04/03/16.
 */
public enum BottleServiceTypeEnum {
	TABLE("Table"),
	STANDUP("Standup"),
    BAR("Bar"),
    NONE("None");

	private static Map<String, String> typeToNameMapping;
    private String name;

	BottleServiceTypeEnum(String name) {
		this.name = name;
	}

	public static Map<String, String> all() {
		if (typeToNameMapping == null) {
			initMapping();
		}

		return typeToNameMapping;
	}

	private static void initMapping() {
		typeToNameMapping = new HashMap<>();
		for (BottleServiceTypeEnum s : values()) {
			typeToNameMapping.put(s.name(), s.name);
		}
    }

    @Override
    public String toString() {
        return name;
    }
}
