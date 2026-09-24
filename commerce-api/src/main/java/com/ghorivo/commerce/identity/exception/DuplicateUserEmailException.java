package com.ghorivo.commerce.identity.exception;

public class DuplicateUserEmailException extends RuntimeException {

    public DuplicateUserEmailException(String email) {
        super("User already exists with email: " + email);
    }
}
