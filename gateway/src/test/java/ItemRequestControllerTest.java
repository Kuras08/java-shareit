import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@ContextConfiguration(classes = ShareItGateway.class)
class ItemRequestControllerTest {

    private final String HEADER_USER_ID = "X-Sharer-User-Id";
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestClient itemRequestClient;

    @Test
    void createRequest_ShouldReturnResponseEntity() throws Exception {
        Long userId = 1L;
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("Need a drill");

        // Мокаем возвращаемое значение клиента
        when(itemRequestClient.createRequest(eq(userId), any(ItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok().body("created"));

        mockMvc.perform(post("/requests")
                        .header(HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Need a drill\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("created"));

        verify(itemRequestClient).createRequest(eq(userId), any(ItemRequestDto.class));
    }

    @Test
    void getOwnRequests_ShouldReturnResponseEntity() throws Exception {
        Long userId = 2L;

        when(itemRequestClient.getOwnRequests(userId))
                .thenReturn(ResponseEntity.ok().body("ownRequests"));

        mockMvc.perform(get("/requests")
                        .header(HEADER_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().string("ownRequests"));

        verify(itemRequestClient).getOwnRequests(userId);
    }

    @Test
    void getAllRequests_ShouldReturnResponseEntity_WithDefaults() throws Exception {
        Long userId = 3L;

        when(itemRequestClient.getAllRequests(userId, 0, 10))
                .thenReturn(ResponseEntity.ok().body("allRequests"));

        mockMvc.perform(get("/requests/all")
                        .header(HEADER_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().string("allRequests"));

        verify(itemRequestClient).getAllRequests(userId, 0, 10);
    }

    @Test
    void getAllRequests_ShouldReturnResponseEntity_WithParams() throws Exception {
        Long userId = 3L;

        when(itemRequestClient.getAllRequests(userId, 5, 15))
                .thenReturn(ResponseEntity.ok().body("allRequestsPaged"));

        mockMvc.perform(get("/requests/all")
                        .header(HEADER_USER_ID, userId)
                        .param("from", "5")
                        .param("size", "15"))
                .andExpect(status().isOk())
                .andExpect(content().string("allRequestsPaged"));

        verify(itemRequestClient).getAllRequests(userId, 5, 15);
    }

    @Test
    void getRequestById_ShouldReturnResponseEntity() throws Exception {
        Long userId = 4L;
        Long requestId = 100L;

        when(itemRequestClient.getRequestById(userId, requestId))
                .thenReturn(ResponseEntity.ok().body("requestById"));

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(HEADER_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().string("requestById"));

        verify(itemRequestClient).getRequestById(userId, requestId);
    }
}
