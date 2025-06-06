package ru.practicum.shareit.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.ShareItServer;
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

@SpringBootTest(classes = ShareItServer.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_success() {
        UserDto userDto = new UserDto(null, "John", "john@example.com");
        User user = new User(null, "John", "john@example.com");
        User savedUser = new User(1L, "John", "john@example.com");
        UserDto savedUserDto = new UserDto(1L, "John", "john@example.com");

        when(userRepository.existsByEmailIgnoreCase(userDto.getEmail())).thenReturn(false);
        when(userMapper.toUser(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toUserDto(savedUser)).thenReturn(savedUserDto);

        UserDto result = userService.create(userDto);

        assertEquals(savedUserDto, result);

        verify(userRepository).existsByEmailIgnoreCase(userDto.getEmail());
        verify(userMapper).toUser(userDto);
        verify(userRepository).save(user);
        verify(userMapper).toUserDto(savedUser);
    }

    @Test
    void createUser_duplicateEmail_throwsException() {
        UserDto userDto = new UserDto(null, "John", "john@example.com");

        when(userRepository.existsByEmailIgnoreCase(userDto.getEmail())).thenReturn(true);

        DuplicatedDataException ex = assertThrows(DuplicatedDataException.class, () -> userService.create(userDto));
        assertEquals("Пользователь с таким email уже существует", ex.getMessage());

        verify(userRepository).existsByEmailIgnoreCase(userDto.getEmail());
        verify(userMapper, never()).toUser(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void getById_found() {
        User user = new User(1L, "John", "john@example.com");
        UserDto userDto = new UserDto(1L, "John", "john@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = userService.getById(1L);

        assertEquals(userDto, result);
        verify(userRepository).findById(1L);
        verify(userMapper).toUserDto(user);
    }

    @Test
    void getById_notFound_throwsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> userService.getById(1L));
        assertEquals("Пользователь с id 1 не найден", ex.getMessage());

        verify(userRepository).findById(1L);
        verify(userMapper, never()).toUserDto(any());
    }

    @Test
    void getAll_returnsUsers() {
        User user1 = new User(1L, "John", "john@example.com");
        User user2 = new User(2L, "Jane", "jane@example.com");

        UserDto dto1 = new UserDto(1L, "John", "john@example.com");
        UserDto dto2 = new UserDto(2L, "Jane", "jane@example.com");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(userMapper.toUserDto(user1)).thenReturn(dto1);
        when(userMapper.toUserDto(user2)).thenReturn(dto2);

        List<UserDto> result = userService.getAll();

        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));

        verify(userRepository).findAll();
        verify(userMapper).toUserDto(user1);
        verify(userMapper).toUserDto(user2);
    }

    @Test
    void updateUser_success() {
        Long id = 1L;
        UserDto inputDto = new UserDto(null, "John Updated", "john.updated@example.com");
        User existingUser = new User(id, "John", "john@example.com");
        User savedUser = new User(id, "John Updated", "john.updated@example.com");
        UserDto savedUserDto = new UserDto(id, "John Updated", "john.updated@example.com");

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailIgnoreCaseAndIdNot(inputDto.getEmail(), id)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toUserDto(savedUser)).thenReturn(savedUserDto);

        UserDto result = userService.update(id, inputDto);

        assertEquals(savedUserDto, result);
        assertEquals("John Updated", existingUser.getName());
        assertEquals("john.updated@example.com", existingUser.getEmail());

        verify(userRepository).findById(id);
        verify(userRepository).existsByEmailIgnoreCaseAndIdNot(inputDto.getEmail(), id);
        verify(userRepository).save(existingUser);
        verify(userMapper).toUserDto(savedUser);
    }

    @Test
    void updateUser_emailExists_throwsException() {
        Long id = 1L;
        UserDto inputDto = new UserDto(null, "John Updated", "john.updated@example.com");
        User existingUser = new User(id, "John", "john@example.com");

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailIgnoreCaseAndIdNot(inputDto.getEmail(), id)).thenReturn(true);

        DuplicatedDataException ex = assertThrows(DuplicatedDataException.class, () -> userService.update(id, inputDto));
        assertEquals("Email уже используется другим пользователем", ex.getMessage());

        verify(userRepository).findById(id);
        verify(userRepository).existsByEmailIgnoreCaseAndIdNot(inputDto.getEmail(), id);
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_notFound_throwsException() {
        Long id = 1L;
        UserDto inputDto = new UserDto(null, "John Updated", "john.updated@example.com");

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> userService.update(id, inputDto));
        assertEquals("Пользователь не найден", ex.getMessage());

        verify(userRepository).findById(id);
        verify(userRepository, never()).existsByEmailIgnoreCaseAndIdNot(anyString(), anyLong());
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_success() {
        Long id = 1L;

        doNothing().when(userRepository).deleteById(id);

        userService.delete(id);

        verify(userRepository).deleteById(id);
    }
}
