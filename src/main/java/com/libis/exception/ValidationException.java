package com.libis.exception;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Thrown by MemberService / BookService when a form fails validation.
 * Carries one message per field (key = form field name), plus "general" for non-field errors.
 */
public class ValidationException extends Exception {

    private static final long serialVersionUID = 1L;

    public static final String GENERAL = "general";

    private final transient Map<String, String> errors;

    public ValidationException(Map<String, String> errors) {
        super(String.join(" ", errors.values()));
        this.errors = Collections.unmodifiableMap(new LinkedHashMap<>(errors));
    }

    /** A single, non-field error (for example "You cannot delete your own account."). */
    public ValidationException(String generalMessage) {
        this(Map.of(GENERAL, generalMessage));
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
