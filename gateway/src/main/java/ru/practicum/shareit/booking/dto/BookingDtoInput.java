package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.booking.validation.ValidBookingTime;

import java.time.LocalDateTime;

@Data
@ValidBookingTime
public class BookingDtoInput {

    @NotNull(message = "Дата начала бронирования должна быть указана")
    @Future(message = "Дата и время начала бронирования не может быть в прошлом")
    private LocalDateTime start;

    @NotNull(message = "Дата конца бронирования должна быть указана")
    @Future(message = "Дата и время конца бронирования не может быть в прошлом")
    private LocalDateTime end;

    @NotNull(message = "Идентификатор вещи не может быть пустым")
    private Long itemId;
}

