package com.libis.data;

import com.libis.model.Book;
import com.libis.model.Member;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class LibraryData {

    private static final List<Member> members = new ArrayList<>();
    private static final List<Book> books = new ArrayList<>();

    static {
        // Exactly 5 members
        members.add(new Member(1, "Thabo", "Molefe", "thabo.molefe@bac.ac.bw", "pass123"));
        members.add(new Member(2, "Keabetswe", "Dlamini", "kea.dlamini@bac.ac.bw", "secure45"));
        members.add(new Member(3, "Mpho", "Kgosi", "mpho.kgosi@bac.ac.bw", "library1"));
        members.add(new Member(4, "Naledi", "Sebego", "naledi.sebego@bac.ac.bw", "read2024"));
        members.add(new Member(5, "Tshepo", "Motsumi", "tshepo.motsumi@bac.ac.bw", "books99"));

        // Exactly 10 books
        books.add(new Book(1, "Things Fall Apart", "Chinua Achebe", "Fiction",
                "A classic novel about pre-colonial life in Nigeria and the arrival of Europeans.", true));
        books.add(new Book(2, "The Beautiful Ones Are Not Yet Born", "Ayi Kwei Armah", "Fiction",
                "A powerful story of corruption and disillusionment in post-independence Ghana.", true));
        books.add(new Book(3, "Nervous Conditions", "Tsitsi Dangarembga", "Fiction",
                "A coming-of-age story set in colonial Rhodesia exploring gender and race.", false));
        books.add(new Book(4, "Born a Crime", "Trevor Noah", "Memoir",
                "Trevor Noah's autobiographical account of growing up in South Africa under apartheid.", true));
        books.add(new Book(5, "The Alchemist", "Paulo Coelho", "Fiction",
                "A philosophical novel about a shepherd's journey to fulfil his personal legend.", true));
        books.add(new Book(6, "Sapiens", "Yuval Noah Harari", "Non-Fiction",
                "A brief history of humankind from the Stone Age to the modern age.", true));
        books.add(new Book(7, "Half of a Yellow Sun", "Chimamanda Ngozi Adichie", "Fiction",
                "A novel set during the Nigerian Civil War, exploring love, loyalty and loss.", false));
        books.add(new Book(8, "Atomic Habits", "James Clear", "Self-Help",
                "Practical strategies for forming good habits and breaking bad ones.", true));
        books.add(new Book(9, "The River Between", "Ngũgĩ wa Thiong'o", "Fiction",
                "A story of two rival villages in Kenya and the impact of colonialism.", true));
        books.add(new Book(10, "Long Walk to Freedom", "Nelson Mandela", "Autobiography",
                "The autobiography of Nelson Mandela, detailing his fight against apartheid.", true));
    }

    public static List<Member> getMembers() {
        return Collections.unmodifiableList(members);
    }

    public static List<Book> getBooks() {
        return Collections.unmodifiableList(books);
    }

    public static Member findMember(String email, String password) {
        if (email == null || password == null) {
            return null;
        }
        for (Member m : members) {
            if (email.equalsIgnoreCase(m.getEmail()) && password.equals(m.getPassword())) {
                return m;
            }
        }
        return null;
    }

    /** Returns books whose title, author or genre contains the query (case-insensitive). */
    public static List<Book> searchBooks(String query) {
        if (query == null || query.isBlank()) {
            return getBooks();
        }
        String q = query.trim().toLowerCase(Locale.ROOT);
        List<Book> results = new ArrayList<>();
        for (Book b : books) {
            if (b.getTitle().toLowerCase(Locale.ROOT).contains(q)
                    || b.getAuthor().toLowerCase(Locale.ROOT).contains(q)
                    || b.getGenre().toLowerCase(Locale.ROOT).contains(q)) {
                results.add(b);
            }
        }
        return results;
    }

    private LibraryData() { }
}
