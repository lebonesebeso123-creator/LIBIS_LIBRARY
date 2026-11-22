package com.libis.exception;

/** Thrown by LoanService when a borrowing rule is broken. The message is shown to the member. */
public class LoanException extends Exception {

    private static final long serialVersionUID = 1L;

    public LoanException(String reason) {
        super(reason);
    }
}
