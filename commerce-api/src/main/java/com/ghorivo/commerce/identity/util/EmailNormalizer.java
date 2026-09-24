package com.ghorivo.commerce.identity.util;

import java.util.Locale;

public final class EmailNormalizer {

    private EmailNormalizer() {
    }

    public static String normalize(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "email must not be blank"
            );
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }
}
