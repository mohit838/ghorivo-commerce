package com.ghorivo.commerce.common.exception;

import java.time.Instant;
import java.util.Map;

public record ApiErrorResponse(
        boolean success,
        Instant timestamp,
        int status,
        String code,
        String message,
        String path,
        Map<String, String> fieldErrors
) {
}
