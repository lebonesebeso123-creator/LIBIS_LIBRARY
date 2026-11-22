package com.libis.model;

import java.io.Serializable;

/**
 * A book in the catalogue (one row of the books table).
 * CA1/CA2 getters are kept; CA3 adds isbn and publishedYear.
 * {@code available} is NOT a database column: BookDao works it out from open loans.
 */
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String isbn;
    private String title;
    private String author;
    private String genre;
    private String description;
    private Integer publishedYear;
    private boolean available = true;

    public Book() {
    }

    public Book(int id, String isbn, String title, String author, String genre,
                String description, Integer publishedYear, boolean available) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.description = description;
        this.publishedYear = publishedYear;
        this.available = available;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getPublishedYear() { return publishedYear; }
    public void setPublishedYear(Integer publishedYear) { this.publishedYear = publishedYear; }

    /** EL reads ${book.available} through this getter. Filled by BookDao from the loans table. */
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
