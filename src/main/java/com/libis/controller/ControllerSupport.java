package com.libis.controller;

import com.libis.model.Member;
import com.libis.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/** Small helpers shared by the controllers. */
final class ControllerSupport {

    /** Session keys for one-time ("flash") messages shown after a redirect. */
    static final String FLASH = "flash";               // plain text (librarian pages)
    static final String FLASH_ERROR = "flashError";    // plain text error (librarian pages)
    static final String FLASH_KEY = "flashKey";        // resource-bundle key (member pages)
    static final String FLASH_ARG = "flashArg";        // argument for FLASH_KEY

    private ControllerSupport() {
    }

    /**
     * The signed-in member, identified by the CONTAINER (request.getRemoteUser()),
     * never by an id sent from the browser.
     */
    static Optional<Member> currentMember(HttpServletRequest request, MemberService memberService) {
        return memberService.findByUsername(request.getRemoteUser());
    }

    /** Parses an id parameter; returns -1 if missing or not a number. */
    static int intParam(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value == null || !value.trim().matches("\\d{1,9}")) {
            return -1;
        }
        return Integer.parseInt(value.trim());
    }

    /** Copies the named request parameters into a map so a form can be re-displayed with what was typed. */
    static Map<String, String> formFromRequest(HttpServletRequest request, String... fields) {
        Map<String, String> form = new LinkedHashMap<>();
        for (String f : fields) {
            String v = request.getParameter(f);
            form.put(f, v == null ? "" : v);
        }
        return form;
    }

    static void flash(HttpServletRequest request, String message) {
        request.getSession().setAttribute(FLASH, message);
    }

    static void flashError(HttpServletRequest request, String message) {
        request.getSession().setAttribute(FLASH_ERROR, message);
    }

    static void flashKey(HttpServletRequest request, String key, String arg) {
        HttpSession session = request.getSession();
        session.setAttribute(FLASH_KEY, key);
        session.setAttribute(FLASH_ARG, arg);
    }

    static String trimToEmpty(String s) {
        return s == null ? "" : s.trim();
    }
}
