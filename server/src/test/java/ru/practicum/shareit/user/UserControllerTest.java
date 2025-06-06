package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

    @Test
    void createUser_shouldReturnCreatedUser() throws Exception {
        UserDto input = new UserDto(null, "John", "john@example.com");
        UserDto returned = new UserDto(1L, "John", "john@example.com");

        when(userService.create(any(UserDto.class))).thenReturn(returned);

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(input))).andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(1L)).andExpect(jsonPath("$.name").value("John")).andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void getUser_shouldReturnUser() throws Exception {
        UserDto returned = new UserDto(1L, "John", "john@example.com");

        when(userService.getById(1L)).thenReturn(returned);

        mockMvc.perform(get("/users/1")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1L)).andExpect(jsonPath("$.name").value("John")).andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void getAllUsers_shouldReturnList() throws Exception {
        List<UserDto> users = List.of(new UserDto(1L, "John", "john@example.com"), new UserDto(2L, "Jane", "jane@example.com"));

        when(userService.getAll()).thenReturn(users);

        mockMvc.perform(get("/users")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2)).andExpect(jsonPath("$[0].id").value(1L)).andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        UserDto input = new UserDto(null, "John Updated", "john.updated@example.com");
        UserDto returned = new UserDto(1L, "John Updated", "john.updated@example.com");

        when(userService.update(eq(1L), any(UserDto.class))).thenReturn(returned);

        mockMvc.perform(patch("/users/1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(input))).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1L)).andExpect(jsonPath("$.name").value("John Updated")).andExpect(jsonPath("$.email").value("john.updated@example.com"));
    }

    @Test
    void deleteUser_shouldReturnNoContent() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/users/1")).andExpect(status().isNoContent());
    }
}
