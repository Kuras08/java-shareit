package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Override
    public UserDto create(UserDto userDto) {
        log.info("Создание пользователя: {}", userDto);

        if (userRepository.existsByEmailIgnoreCase(userDto.getEmail())) {
            log.warn("Попытка создания пользователя с уже существующим email: {}", userDto.getEmail());
            throw new DuplicatedDataException("Пользователь с таким email уже существует");
        }

        User user = userMapper.toUser(userDto);
        User saved = userRepository.save(user);
        log.info("Пользователь создан с id={}", saved.getId());
        return userMapper.toUserDto(saved);
    }

    @Override
    public UserDto getById(Long id) {
        log.info("Получение пользователя по id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Пользователь с id={} не найден", id);
                    return new NotFoundException("Пользователь с id " + id + " не найден");
                });

        return userMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        log.info("Получение всех пользователей");
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        log.info("Обновление пользователя id {} данными: {}", id, userDto);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Попытка обновления несуществующего пользователя с id={}", id);
                    return new NotFoundException("Пользователь не найден");
                });

        if (userDto.getEmail() != null &&
                userRepository.existsByEmailIgnoreCaseAndIdNot(userDto.getEmail(), id)) {
            throw new DuplicatedDataException("Email уже используется другим пользователем");
        }

        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            existingUser.setEmail(userDto.getEmail());
        }

        User saved = userRepository.save(existingUser);
        log.info("Пользователь id={} успешно обновлён", saved.getId());
        return userMapper.toUserDto(saved);
    }

    @Override
    public void delete(Long id) {
        log.info("Удаление пользователя с id={}", id);
        userRepository.deleteById(id);
        log.info("Пользователь с id={} удалён", id);
    }
}

