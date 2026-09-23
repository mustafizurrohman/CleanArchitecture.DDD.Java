package com.mustafizur.cleanarchitecture.domain.customer;

import com.mustafizur.cleanarchitecture.core.exception.DomainException;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public record Email(String value) {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,63}$",
            Pattern.CASE_INSENSITIVE);

    public Email {
        Objects.requireNonNull(value, "email must not be null");
        value = value.strip().toLowerCase(Locale.ROOT);
        if (value.length() > 320 || !EMAIL_PATTERN.matcher(value).matches()) {
            throw new DomainException("Invalid email address");
        }
    }

    public static Email of(String value) {
        return new Email(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
