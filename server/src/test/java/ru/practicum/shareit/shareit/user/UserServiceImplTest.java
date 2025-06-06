package ru.practicum.shareit.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@example.com");

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("John");
        userDto.setEmail("john@example.com");
    }

    @Test
    void create_whenEmailNotExists_thenSaveAndReturnDto() {
        when(userRepository.existsByEmailIgnoreCase(userDto.getEmail())).thenReturn(false);
        when(userMapper.toUser(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = userService.create(userDto);

        assertNotNull(result);
        assertEquals(userDto.getEmail(), result.getEmail());

        verify(userRepository).existsByEmailIgnoreCase(userDto.getEmail());
        verify(userRepository).save(user);
        verify(userMapper).toUser(userDto);
        verify(userMapper).toUserDto(user);
    }

    @Test
    void create_whenEmailExists_thenThrowDuplicatedDataException() {
        when(userRepository.existsByEmailIgnoreCase(userDto.getEmail())).thenReturn(true);

        DuplicatedDataException ex = assertThrows(DuplicatedDataException.class,
                () -> userService.create(userDto));
        assertEquals("Пользователь с таким email уже существует", ex.getMessage());

        verify(userRepository).existsByEmailIgnoreCase(userDto.getEmail());
        verify(userRepository, never()).save(any());
    }

    @Test
    void getById_whenUserExists_thenReturnDto() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = userService.getById(user.getId());

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());

        verify(userRepository).findById(user.getId());
        verify(userMapper).toUserDto(user);
    }

    @Test
    void getById_whenUserNotFound_thenThrowNotFoundException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> userService.getById(user.getId()));
        assertEquals("Пользователь с id " + user.getId() + " не найден", ex.getMessage());

        verify(userRepository).findById(user.getId());
        verify(userMapper, never()).toUserDto(any());
    }

    @Test
    void getAll_whenUsersExist_thenReturnListOfDtos() {
        List<User> users = List.of(user);
        List<UserDto> dtos = List.of(userDto);

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        List<UserDto> result = userService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userDto.getId(), result.get(0).getId());

        verify(userRepository).findAll();
        verify(userMapper, times(users.size())).toUserDto(any());
    }

    @Test
    void update_whenUserExistsAndEmailNotDuplicated_thenUpdateAndReturnDto() {
        UserDto updateDto = new UserDto();
        updateDto.setName("New Name");
        updateDto.setEmail("newemail@example.com");

        User updatedUser = new User();
        updatedUser.setId(user.getId());
        updatedUser.setName(updateDto.getName());
        updatedUser.setEmail(updateDto.getEmail());

        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setId(user.getId());
        updatedUserDto.setName(updateDto.getName());
        updatedUserDto.setEmail(updateDto.getEmail());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailIgnoreCaseAndIdNot(updateDto.getEmail(), user.getId())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toUserDto(updatedUser)).thenReturn(updatedUserDto);

        UserDto result = userService.update(user.getId(), updateDto);

        assertNotNull(result);
        assertEquals(updateDto.getName(), result.getName());
        assertEquals(updateDto.getEmail(), result.getEmail());

        verify(userRepository).findById(user.getId());
        verify(userRepository).existsByEmailIgnoreCaseAndIdNot(updateDto.getEmail(), user.getId());
        verify(userRepository).save(any(User.class));
        verify(userMapper).toUserDto(updatedUser);
    }

    @Test
    void update_whenUserNotFound_thenThrowNotFoundException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> userService.update(user.getId(), userDto));
        assertEquals("Пользователь не найден", ex.getMessage());

        verify(userRepository).findById(user.getId());
        verify(userRepository, never()).save(any());
    }

    @Test
    void update_whenEmailDuplicated_thenThrowDuplicatedDataException() {
        UserDto updateDto = new UserDto();
        updateDto.setEmail("existing@example.com");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailIgnoreCaseAndIdNot(updateDto.getEmail(), user.getId())).thenReturn(true);

        DuplicatedDataException ex = assertThrows(DuplicatedDataException.class,
                () -> userService.update(user.getId(), updateDto));
        assertEquals("Email уже используется другим пользователем", ex.getMessage());

        verify(userRepository).findById(user.getId());
        verify(userRepository).existsByEmailIgnoreCaseAndIdNot(updateDto.getEmail(), user.getId());
        verify(userRepository, never()).save(any());
    }

    @Test
    void delete_callsRepositoryDelete() {
        Long userId = 1L;

        userService.delete(userId);

        verify(userRepository).deleteById(userId);
    }
}

