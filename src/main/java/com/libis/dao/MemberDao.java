package com.libis.dao;

import com.libis.model.Member;

import java.util.List;
import java.util.Optional;

public interface MemberDao {

    List<Member> findAll();

    Optional<Member> findById(int id);

    Optional<Member> findByUsername(String username);

    Optional<Member> findByMemberNumber(String memberNumber);

    Optional<Member> findByEmail(String email);

    /** Matches full name or member number (case-insensitive, partial). */
    List<Member> search(String term);

    /** Inserts the member and returns the generated member_id. */
    int create(Member member);

    /**
     * Updates the member. If {@code member.getPassword()} is null or blank the stored
     * password is left unchanged.
     */
    boolean update(Member member);

    /** Deletes the member; ON DELETE CASCADE removes all their loan rows. */
    boolean delete(int id);
}
