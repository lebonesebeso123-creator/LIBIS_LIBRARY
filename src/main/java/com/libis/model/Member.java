package com.libis.model;

import java.io.Serializable;

/**
 * A library member or librarian (one row of the members table).
 * CA1/CA2 getters (getId, getFirstName, getLastName, getFullName, getEmail, getPassword) are kept;
 * CA3 adds memberNumber, username and role.
 */
public class Member implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String memberNumber;
    private String fullName;
    private String email;
    private String username;
    private String password;
    private Role role = Role.MEMBER;

    public Member() {
    }

    public Member(int id, String memberNumber, String fullName, String email,
                  String username, String password, Role role) {
        this.id = id;
        this.memberNumber = memberNumber;
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMemberNumber() { return memberNumber; }
    public void setMemberNumber(String memberNumber) { this.memberNumber = memberNumber; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isLibrarian() { return role == Role.LIBRARIAN; }

    /** Kept from CA1/CA2: first word of the full name. */
    public String getFirstName() {
        if (fullName == null || fullName.isBlank()) {
            return "";
        }
        return fullName.trim().split("\\s+", 2)[0];
    }

    /** Kept from CA1/CA2: everything after the first word of the full name. */
    public String getLastName() {
        if (fullName == null) {
            return "";
        }
        String[] parts = fullName.trim().split("\\s+", 2);
        return parts.length > 1 ? parts[1] : "";
    }
}
