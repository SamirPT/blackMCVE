package tech.peller.mrblackandroidwatch.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sam (salyasov@gmail.com) on 04.05.2018
 */

public enum WifiSignalStrengthEnum {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High");

    private String name;
    private static Map<String, String> typeToNameMapping;

    WifiSignalStrengthEnum(String name) {
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
        for (WifiSignalStrengthEnum s : values()) {
            typeToNameMapping.put(s.name(), s.name);
        }
    }
}
