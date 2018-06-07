package tech.peller.mrblackandroidwatch.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sam (samir@peller.tech) on 16.02.2017
 */

public enum NotificationTypeEnum {
    BS_RESERVATION_MADE_WITH_NUMBER(""),
    NEW_APPROVAL_REQUEST_BS(""),
    NEW_APPROVAL_EMPLOYEE(""),
    NOTIFY_ME_ARRIVAL(""),
    NEW_ASSIGNMENT(""),
    NEW_TABLE_ASSIGNMENT(""),
    TABLE_ASSIGNMENT_REMOVED(""),
    TABLE_RELEASED(""),
    RESERVATION_I_CREATED_APPROVED(""),
    GL_RESERVATION_MADE_WITH_NUMBER(""),
    RESERVATION_I_CREATED_REJECTED(""),
    NEW_APPROVED_RESERVATION_WITH_TABLE("");

    private String name;
    private static Map<String, String> typeToNameMapping;

    NotificationTypeEnum(String name) {
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
        for (NotificationTypeEnum s : values()) {
            typeToNameMapping.put(s.name(), s.name);
        }
    }
}
