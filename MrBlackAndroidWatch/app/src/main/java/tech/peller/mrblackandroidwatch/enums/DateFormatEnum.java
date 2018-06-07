package tech.peller.mrblackandroidwatch.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sam (samir@peller.tech) on 29.12.2016
 */

public enum DateFormatEnum {
    SERVER("yyyy-MM-dd"),
    SINCE("dd/MM/yyyy"),
    HEADER("EEE, MMM dd, yyyy"),
    BIG_HEADER("EEEE, MMM dd, yyyy"),
    SHORT_HEADER("EEE, MMM dd"),
    DOB("dd.MM.yyyy");

    private static Map<String, String> typeToNameMapping;
    private String name;

    DateFormatEnum(String name) {
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
        for (DateFormatEnum s : values()) {
            typeToNameMapping.put(s.name(), s.name);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
