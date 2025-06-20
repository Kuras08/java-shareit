package ru.practicum.shareit.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ItemRequestControllerTest {

    @Mock
    private ItemRequestService requestService;

    @InjectMocks
    private ItemRequestController controller;

    private MockMvc mockMvc;
    private ObjectMapper mapper;

    private ItemRequestDto requestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        mapper = new ObjectMapper();

        requestDto = new ItemRequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("Need a drill");
    }

    @Test
    void createRequest_ReturnsCreatedRequest() throws Exception {
        when(requestService.addRequest(anyLong(), any())).thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDto.getId()))
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()));

        verify(requestService).addRequest(eq(1L), any(ItemRequestDto.class));
    }

    @Test
    void getOwnRequests_ReturnsList() throws Exception {
        when(requestService.getOwnRequests(1L)).thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestDto.getId()))
                .andExpect(jsonPath("$[0].description").value(requestDto.getDescription()));

        verify(requestService).getOwnRequests(1L);
    }

    @Test
    void getAllRequests_WithPagination_ReturnsList() throws Exception {
        when(requestService.getAllRequests(1L, 0, 10)).thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestDto.getId()))
                .andExpect(jsonPath("$[0].description").value(requestDto.getDescription()));

        verify(requestService).getAllRequests(1L, 0, 10);
    }

    @Test
    void getRequestById_ReturnsRequest() throws Exception {
        when(requestService.getRequestById(1L, 1L)).thenReturn(requestDto);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDto.getId()))
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()));

        verify(requestService).getRequestById(1L, 1L);
    }
}

