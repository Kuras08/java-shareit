package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(message = "Имя пользователя должно быть указано")
    private String name;
    @NotNull
    @Email(message = "Имейл должен содержать символ «@». Формат имейла: example@mail.com")
    private String email;
}
