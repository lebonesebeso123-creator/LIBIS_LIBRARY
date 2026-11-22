package com.libis.service;

import com.libis.dao.BookDao;
import com.libis.dao.BookDaoImpl;
import com.libis.dao.LoanDao;
import com.libis.dao.LoanDaoImpl;
import com.libis.exception.LoanException;
import com.libis.model.Book;
import com.libis.model.Loan;
import com.libis.model.Member;

import java.time.LocalDate;
import java.util.List;

/** The borrowing rules. Controllers ask; only this class decides. */
public class LoanService {

    /** The loan limit, defined once. JSPs read it through the "maxLoans" request attribute. */
    public static final int MAX_LOANS = 3;

    private final LoanDao loanDao;
    private final BookDao bookDao;

    public LoanService() {
        this(new LoanDaoImpl(), new BookDaoImpl());
    }

    public LoanService(LoanDao loanDao, BookDao bookDao) {
        this.loanDao = loanDao;
        this.bookDao = bookDao;
    }

    /**
     * Borrows a book for the member.
     *
     * @return the borrowed book
     * @throws LoanException if the book does not exist, is already on loan, or the member is at the limit
     */
    public Book borrow(Member member, int bookId) throws LoanException {
        Book book = bookDao.findById(bookId)
                .orElseThrow(() -> new LoanException("That book does not exist in the catalogue."));

        if (loanDao.hasOpenLoan(bookId)) {
            throw new LoanException(book.getTitle() + " cannot be borrowed because it is already on loan.");
        }
        if (loanDao.countOpenByMember(member.getId()) >= MAX_LOANS) {
            throw new LoanException("You already have " + MAX_LOANS
                    + " books on loan, the maximum allowed. Return a book before borrowing another.");
        }
        loanDao.create(member.getId(), bookId, LocalDate.now());
        return book;
    }

    /**
     * Returns a book that is currently on loan to this member. The loan row is kept
     * (returned_on is set) so it becomes part of the borrowing history.
     *
     * @return the returned loan
     */
    public Loan returnBook(Member member, int bookId) throws LoanException {
        Loan loan = loanDao.findOpenByMember(member.getId()).stream()
                .filter(l -> l.getBookId() == bookId)
                .findFirst()
                .orElseThrow(() -> new LoanException(
                        bookDao.findById(bookId)
                                .map(b -> b.getTitle() + " is not currently on loan to you, so it cannot be returned.")
                                .orElse("That book is not currently on loan to you.")));

        if (!loanDao.markReturned(loan.getId(), LocalDate.now())) {
            throw new LoanException(loan.getBookTitle() + " has already been returned.");
        }
        return loan;
    }

    public List<Loan> currentLoans(Member member) {
        return loanDao.findOpenByMember(member.getId());
    }

    public List<Loan> history(Member member) {
        return loanDao.findHistoryByMember(member.getId());
    }
}
