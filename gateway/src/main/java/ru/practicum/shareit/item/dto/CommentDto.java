package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentDto {
    private Long id;
    @NotBlank(message = "Текст должен быть указан")
    private String text;
    private String authorName;
    private LocalDateTime created;
}