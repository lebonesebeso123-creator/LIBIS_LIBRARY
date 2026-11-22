package com.libis.dao;

import com.libis.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookDao {

    /** All books, each with {@code available} worked out from open loans. */
    List<Book> findAll();

    Optional<Book> findById(int id);

    Optional<Book> findByIsbn(String isbn);

    /** Matches title or author (case-insensitive, partial), with availability. */
    List<Book> search(String term);

    int create(Book book);

    boolean update(Book book);

    /** Deletes the book; ON DELETE CASCADE removes its past loan rows. */
    boolean delete(int id);
}
