package com.libis.model;

import java.io.Serializable;
import java.time.LocalDate;

/** One row of the loans table, plus the book's title and author for display. */
public class Loan implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private int memberId;
    private int bookId;
    private String bookTitle;
    private String bookAuthor;
    private LocalDate borrowedOn;
    private LocalDate returnedOn;

    public Loan() {
    }

    public Loan(int id, int memberId, int bookId, String bookTitle, String bookAuthor,
                LocalDate borrowedOn, LocalDate returnedOn) {
        this.id = id;
        this.memberId = memberId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.borrowedOn = borrowedOn;
        this.returnedOn = returnedOn;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public String getBookAuthor() { return bookAuthor; }
    public void setBookAuthor(String bookAuthor) { this.bookAuthor = bookAuthor; }

    public LocalDate getBorrowedOn() { return borrowedOn; }
    public void setBorrowedOn(LocalDate borrowedOn) { this.borrowedOn = borrowedOn; }

    public LocalDate getReturnedOn() { return returnedOn; }
    public void setReturnedOn(LocalDate returnedOn) { this.returnedOn = returnedOn; }

    /** True while the book is still out (returned_on IS NULL). */
    public boolean isOpen() { return returnedOn == null; }
}
