package tech.peller.mrblackandroidwatch.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sam (samir@peller.tech) on 31.10.2017
 */

public enum PaymentMethodEnum {
    CASH("CASH"),
    CREDIT("CREDIT"),
    BOTH("BOTH"),
    NONE("NONE");

    private String name;
    private static Map<String, String> typeToNameMapping;

    PaymentMethodEnum(String name) {
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
        for (PaymentMethodEnum s : values()) {
            typeToNameMapping.put(s.name(), s.name);
        }
    }
}
