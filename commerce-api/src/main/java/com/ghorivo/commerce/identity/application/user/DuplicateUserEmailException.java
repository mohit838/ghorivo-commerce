package com.ghorivo.commerce.identity.application.user;

public class DuplicateUserEmailException extends RuntimeException {

    public DuplicateUserEmailException(String email) {
        super("User already exists with email: " + email);
    }
}
