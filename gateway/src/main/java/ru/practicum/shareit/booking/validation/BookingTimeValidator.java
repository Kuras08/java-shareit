package ru.practicum.shareit.booking.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.dto.BookingDtoInput;

import java.time.LocalDateTime;

public class BookingTimeValidator implements ConstraintValidator<ValidBookingTime, BookingDtoInput> {

    @Override
    public void initialize(ValidBookingTime constraintAnnotation) {
        // Нет инициализации
    }

    @Override
    public boolean isValid(BookingDtoInput dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }
        LocalDateTime start = dto.getStart();
        LocalDateTime end = dto.getEnd();
        if (start == null || end == null) {
            return true;
        }
        return start.isBefore(end);
    }
}
