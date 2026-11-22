package com.libis.model;

import java.io.Serializable;

public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int id;
    private final String title;
    private final String author;
    private final String genre;
    private final String description;
    private boolean available;

    public Book(int id, String title, String author, String genre, String description, boolean available) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.description = description;
        this.available = available;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getGenre() { return genre; }
    public String getDescription() { return description; }

    // EL reads ${book.available} through this "is" getter
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
