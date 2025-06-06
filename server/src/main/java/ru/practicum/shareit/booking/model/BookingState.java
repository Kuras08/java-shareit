package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exception.ValidationException;

public enum BookingState {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED;

    public static BookingState from(String value) {
        if (value == null) {
            throw new ValidationException("State must not be null");
        }

        try {
            return BookingState.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: " + value);
        }
    }
}

