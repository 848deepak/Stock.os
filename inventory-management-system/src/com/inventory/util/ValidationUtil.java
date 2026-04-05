package com.inventory.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public final class ValidationUtil {
    private ValidationUtil() {
    }

    public static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
    }

    public static void requireNonNegative(double value, String fieldName) {
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative");
        }
    }

    public static void requirePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be greater than zero");
        }
    }

    public static void requireNonNegative(int value, String fieldName) {
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative");
        }
    }

    public static void validateIsoDateOrBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            return;
        }
        try {
            LocalDate.parse(value.trim());
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(fieldName + " must be in YYYY-MM-DD format");
        }
    }
}
