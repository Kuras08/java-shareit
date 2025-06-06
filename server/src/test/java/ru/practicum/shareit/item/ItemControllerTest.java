package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOutput;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
public class ItemControllerTest {

    private static final String HEADER = "X-Sharer-User-Id";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;
    private ItemDto itemDto;
    private ItemDtoOutput itemDtoOutput;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);

        itemDtoOutput = new ItemDtoOutput();
        itemDtoOutput.setId(1L);
        itemDtoOutput.setName("Item");

        commentDto = new CommentDto(1L, "Great item!", "John", LocalDateTime.now());
        commentDto.setText("Great item!");
    }

    @Test
    @Order(1)
    void addItemTest() throws Exception {
        when(itemService.addItem(anyLong(), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header(HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Item"));
    }

    @Test
    @Order(2)
    void updateItemTest() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .header(HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Item"));
    }

    @Test
    @Order(3)
    void getItemByIdTest() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemDtoOutput);

        mockMvc.perform(get("/items/1")
                        .header(HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @Order(4)
    void getItemsByOwnerTest() throws Exception {
        when(itemService.getItemsByOwner(anyLong())).thenReturn(Collections.singletonList(itemDtoOutput));

        mockMvc.perform(get("/items")
                        .header(HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @Order(5)
    void searchItemsTest() throws Exception {
        when(itemService.searchItems("item")).thenReturn(Collections.singletonList(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "item"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Item"));
    }

    @Test
    @Order(6)
    void addCommentTest() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class))).thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .header(HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Great item!"));
    }
}
