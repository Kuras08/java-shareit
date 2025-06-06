import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = ShareItGateway.class)
class ItemControllerTest {

    @Mock
    private ItemClient itemClient;

    @InjectMocks
    private ItemController itemController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addItem_ShouldCallClientAndReturnResponse() {
        Long userId = 1L;
        ItemDto itemDto = new ItemDto();
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("Added");

        when(itemClient.addItem(eq(userId), eq(itemDto))).thenReturn(expectedResponse);

        ResponseEntity<Object> actual = itemController.addItem(userId, itemDto);

        assertEquals(expectedResponse, actual);
        verify(itemClient, times(1)).addItem(userId, itemDto);
    }

    @Test
    void updateItem_ShouldCallClientAndReturnResponse() {
        Long userId = 1L;
        Long itemId = 10L;
        ItemDto itemDto = new ItemDto();
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("Updated");

        when(itemClient.updateItem(eq(userId), eq(itemId), eq(itemDto))).thenReturn(expectedResponse);

        ResponseEntity<Object> actual = itemController.updateItem(userId, itemId, itemDto);

        assertEquals(expectedResponse, actual);
        verify(itemClient, times(1)).updateItem(userId, itemId, itemDto);
    }

    @Test
    void getItemById_ShouldCallClientAndReturnResponse() {
        Long userId = 1L;
        Long itemId = 10L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("Item");

        when(itemClient.getItemById(eq(userId), eq(itemId))).thenReturn(expectedResponse);

        ResponseEntity<Object> actual = itemController.getItemById(userId, itemId);

        assertEquals(expectedResponse, actual);
        verify(itemClient, times(1)).getItemById(userId, itemId);
    }

    @Test
    void getItemsByOwner_ShouldCallClientAndReturnResponse() {
        Long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("Items list");

        when(itemClient.getItemsByOwner(eq(userId))).thenReturn(expectedResponse);

        ResponseEntity<Object> actual = itemController.getItemsByOwner(userId);

        assertEquals(expectedResponse, actual);
        verify(itemClient, times(1)).getItemsByOwner(userId);
    }

    @Test
    void searchItems_ShouldCallClientAndReturnResponse() {
        String text = "query";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("Search results");

        when(itemClient.searchItems(eq(text))).thenReturn(expectedResponse);

        ResponseEntity<Object> actual = itemController.searchItems(text);

        assertEquals(expectedResponse, actual);
        verify(itemClient, times(1)).searchItems(text);
    }

    @Test
    void addComment_ShouldCallClientAndReturnResponse() {
        Long userId = 1L;
        Long itemId = 10L;
        CommentDto commentDto = new CommentDto();
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("Comment added");

        when(itemClient.addComment(eq(userId), eq(itemId), eq(commentDto))).thenReturn(expectedResponse);

        ResponseEntity<Object> actual = itemController.addComment(userId, itemId, commentDto);

        assertEquals(expectedResponse, actual);
        verify(itemClient, times(1)).addComment(userId, itemId, commentDto);
    }
}
