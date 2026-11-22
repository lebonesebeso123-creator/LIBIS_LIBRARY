package com.libis.service;

import com.libis.dao.MemberDao;
import com.libis.dao.MemberDaoImpl;
import com.libis.exception.ValidationException;
import com.libis.model.Member;
import com.libis.model.Role;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/** Validation, duplicate checks and rules for member management. */
public class MemberService {

    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final MemberDao memberDao;

    public MemberService() {
        this(new MemberDaoImpl());
    }

    public MemberService(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    public List<Member> list(String term) {
        return term == null || term.isBlank() ? memberDao.findAll() : memberDao.search(term);
    }

    public Optional<Member> findById(int id) {
        return memberDao.findById(id);
    }

    public Optional<Member> findByUsername(String username) {
        return username == null ? Optional.empty() : memberDao.findByUsername(username);
    }

    /** Validates and inserts a new member; returns the new id. */
    public int create(Member member) throws ValidationException {
        normalise(member);
        Map<String, String> errors = validate(member, true);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        return memberDao.create(member);
    }

    /**
     * Self-registration from the public sign-up page.
     * The role is ALWAYS member (nobody can make themselves a librarian) and the
     * member number is assigned automatically.
     *
     * @return the new member, with its id and member number filled in
     */
    public Member register(Member member, String confirmPassword) throws ValidationException {
        member.setRole(Role.MEMBER);
        normalise(member);
        for (int attempt = 0; attempt < 2; attempt++) {
            member.setMemberNumber(memberDao.nextMemberNumber());
            Map<String, String> errors = validate(member, true);
            if (!errors.containsKey("password") && !isBlank(member.getPassword())
                    && !member.getPassword().equals(confirmPassword)) {
                errors.put("confirmPassword", "The two passwords do not match.");
            }
            // Someone else took the same number at the same moment: try the next one once more
            if (errors.size() == 1 && errors.containsKey("memberNumber") && attempt == 0) {
                continue;
            }
            errors.remove("memberNumber");   // not a field on the sign-up form
            if (!errors.isEmpty()) {
                throw new ValidationException(errors);
            }
            member.setId(memberDao.create(member));
            return member;
        }
        throw new ValidationException("A member number could not be assigned. Please try again.");
    }

    /**
     * Validates and updates a member. A blank password keeps the existing one.
     * A librarian cannot remove the librarian role from their own account.
     */
    public void update(Member member, String currentUsername) throws ValidationException {
        normalise(member);
        Member existing = memberDao.findById(member.getId())
                .orElseThrow(() -> new ValidationException("That member no longer exists."));

        Map<String, String> errors = validate(member, false);
        if (existing.getUsername().equals(currentUsername) && member.getRole() != Role.LIBRARIAN) {
            errors.put("role", "You cannot remove the librarian role from your own account.");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        memberDao.update(member);
    }

    /**
     * Deletes a member. ON DELETE CASCADE removes their whole borrowing history,
     * which also makes any book they still held available again.
     *
     * @return the deleted member (for the confirmation message)
     */
    public Member delete(int id, String currentUsername) throws ValidationException {
        Member member = memberDao.findById(id)
                .orElseThrow(() -> new ValidationException("That member no longer exists."));
        if (member.getUsername().equals(currentUsername)) {
            throw new ValidationException(
                    "You cannot delete the account you are currently signed in with.");
        }
        memberDao.delete(id);
        return member;
    }

    // ------------------------------------------------------------------ helpers

    private Map<String, String> validate(Member m, boolean creating) {
        Map<String, String> errors = new LinkedHashMap<>();

        if (isBlank(m.getMemberNumber())) {
            errors.put("memberNumber", "Member number is required.");
        } else if (m.getMemberNumber().length() > 20) {
            errors.put("memberNumber", "Member number must be 20 characters or fewer.");
        }
        if (isBlank(m.getFullName())) {
            errors.put("fullName", "Full name is required.");
        } else if (m.getFullName().length() > 100) {
            errors.put("fullName", "Full name must be 100 characters or fewer.");
        }
        if (isBlank(m.getEmail())) {
            errors.put("email", "Email is required.");
        } else if (m.getEmail().length() > 120 || !EMAIL.matcher(m.getEmail()).matches()) {
            errors.put("email", "Enter a valid email address, for example name@bac.ac.bw.");
        }
        if (isBlank(m.getUsername())) {
            errors.put("username", "Username is required.");
        } else if (!m.getUsername().matches("[A-Za-z0-9._-]{3,50}")) {
            errors.put("username",
                    "Username must be 3 to 50 characters: letters, numbers, dot, dash or underscore.");
        }
        if (creating && isBlank(m.getPassword())) {
            errors.put("password", "Password is required for a new member.");
        } else if (!isBlank(m.getPassword()) && m.getPassword().length() < 6) {
            errors.put("password", "Password must be at least 6 characters.");
        }
        if (m.getRole() == null) {
            errors.put("role", "Choose a role: member or librarian.");
        }

        // Duplicate checks through the DAO (the UNIQUE constraints are only a safety net)
        if (!errors.containsKey("memberNumber")) {
            memberDao.findByMemberNumber(m.getMemberNumber())
                    .filter(other -> other.getId() != m.getId())
                    .ifPresent(other -> errors.put("memberNumber",
                            "Member number " + m.getMemberNumber() + " is already used by "
                                    + other.getFullName() + "."));
        }
        if (!errors.containsKey("email")) {
            memberDao.findByEmail(m.getEmail())
                    .filter(other -> other.getId() != m.getId())
                    .ifPresent(other -> errors.put("email",
                            "The email " + m.getEmail() + " is already used by another member."));
        }
        if (!errors.containsKey("username")) {
            memberDao.findByUsername(m.getUsername())
                    .filter(other -> other.getId() != m.getId())
                    .ifPresent(other -> errors.put("username",
                            "The username " + m.getUsername() + " is already taken."));
        }
        return errors;
    }

    private static void normalise(Member m) {
        m.setMemberNumber(trim(m.getMemberNumber()));
        m.setFullName(trim(m.getFullName()));
        m.setEmail(trim(m.getEmail()));
        m.setUsername(trim(m.getUsername()));
        // passwords are not trimmed on purpose
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
