import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ContextConfiguration(classes = ShareItGateway.class)
class UserControllerTest {

    private static final String BASE_URL = "/users";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    @Test
    void createUser_ShouldReturnCreatedUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("John");
        userDto.setEmail("john@example.com");

        when(userClient.create(any(UserDto.class)))
                .thenReturn(ResponseEntity.ok("createdUser"));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John\",\"email\":\"john@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("createdUser"));

        verify(userClient).create(any(UserDto.class));
    }

    @Test
    void getUser_ShouldReturnUser() throws Exception {
        Long userId = 1L;

        when(userClient.getById(userId))
                .thenReturn(ResponseEntity.ok("userById"));

        mockMvc.perform(get(BASE_URL + "/" + userId))
                .andExpect(status().isOk())
                .andExpect(content().string("userById"));

        verify(userClient).getById(userId);
    }

    @Test
    void getAllUsers_ShouldReturnUserList() throws Exception {
        when(userClient.getAll())
                .thenReturn(ResponseEntity.ok("allUsers"));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(content().string("allUsers"));

        verify(userClient).getAll();
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        Long userId = 2L;
        UserDto userDto = new UserDto();
        userDto.setName("UpdatedName");
        userDto.setEmail("updated@example.com");

        when(userClient.update(eq(userId), any(UserDto.class)))
                .thenReturn(ResponseEntity.ok("updatedUser"));

        mockMvc.perform(patch(BASE_URL + "/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"UpdatedName\",\"email\":\"updated@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("updatedUser"));

        verify(userClient).update(eq(userId), any(UserDto.class));
    }

    @Test
    void deleteUser_ShouldReturnConfirmation() throws Exception {
        Long userId = 3L;

        when(userClient.delete(userId))
                .thenReturn(ResponseEntity.ok("deleted"));

        mockMvc.perform(delete(BASE_URL + "/" + userId))
                .andExpect(status().isOk())
                .andExpect(content().string("deleted"));

        verify(userClient).delete(userId);
    }
}
