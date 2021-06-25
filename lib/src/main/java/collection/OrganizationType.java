package collection;

import java.io.Serializable;

public enum OrganizationType implements Serializable {
    COMMERCIAL,
    GOVERNMENT,
    PRIVATE_LIMITED_COMPANY,
    OPEN_JOINT_STOCK_COMPANY;

    public static OrganizationType fromString(String s) {
        if (s != null) {
            for (OrganizationType type : OrganizationType.values()) {
                if (s.equals(type.toString())) {
                    return type;
                }
            }
        }
        throw new IllegalArgumentException();
    }

    public static String valueList() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < values().length; i++) {
            s.append(values()[i]).append((i != values().length - 1) ? ", " : "");
        }
        return s.toString();
    }
}