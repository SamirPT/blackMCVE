package tech.peller.mrblackandroidwatch.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sam (samir@peller.tech) on 01.11.2016
 */

public enum VenueTypeEnum {
    VENUE("Venue"),
    EVENT("Event"),
    PROMOTER("Promoter"),
    RESTAURANT("Restaurant");

    private String name;
    private static Map<String, String> typeToNameMapping;

    VenueTypeEnum(String name) {
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
        for (VenueTypeEnum s : values()) {
            typeToNameMapping.put(s.name(), s.name);
        }
    }
}
