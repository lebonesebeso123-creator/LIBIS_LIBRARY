package com.libis.model;

/** The two security roles. {@link #value()} is what is stored in members.role and used in web.xml. */
public enum Role {
    MEMBER("member"),
    LIBRARIAN("librarian");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    /** For EL: ${member.role.value} */
    public String getValue() {
        return value;
    }

    /** Converts a database / form value ("member", "librarian") to a Role, or null if unknown. */
    public static Role fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (Role r : values()) {
            if (r.value.equalsIgnoreCase(value.trim())) {
                return r;
            }
        }
        return null;
    }
}
