package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;


@Data
public class ItemRequest {
    private Long id;

    @NotBlank(message = "Описание запроса не может быть пустым")
    private String description;

    private User requestor;

    private LocalDateTime created;
}
