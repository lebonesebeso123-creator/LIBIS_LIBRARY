package com.libis.dao;

import com.libis.model.Loan;

import java.time.LocalDate;
import java.util.List;

public interface LoanDao {

    /** Records a borrow; returns the new loan_id. */
    int create(int memberId, int bookId, LocalDate borrowedOn);

    /** Sets returned_on on an open loan. Returns false if the loan was not open. */
    boolean markReturned(int loanId, LocalDate returnedOn);

    int countOpenByMember(int memberId);

    List<Loan> findOpenByMember(int memberId);

    /** Returned loans only (returned_on IS NOT NULL), newest first. */
    List<Loan> findHistoryByMember(int memberId);

    boolean hasOpenLoan(int bookId);
}
