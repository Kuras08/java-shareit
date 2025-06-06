package ru.practicum.shareit.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ItemMapperTest {

    private CommentMapper commentMapper;
    private ItemMapper itemMapper;

    @BeforeEach
    void setup() {
        commentMapper = Mockito.mock(CommentMapper.class);
        itemMapper = new ItemMapper(commentMapper);
    }

    @Test
    void toItemDto_shouldReturnNullWhenItemIsNull() {
        assertThat(itemMapper.toItemDto(null)).isNull();
    }

    @Test
    void toItemDto_shouldMapFieldsCorrectly() {
        Item item = new Item();
        item.setId(1L);
        item.setName("ItemName");
        item.setDescription("ItemDescription");
        item.setAvailable(true);
        item.setRequest(null);

        var dto = itemMapper.toItemDto(item);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("ItemName");
        assertThat(dto.getDescription()).isEqualTo("ItemDescription");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getRequestId()).isNull();
        assertThat(dto.getComments()).isEmpty();
    }

    @Test
    void toItemDto_shouldSetRequestIdWhenRequestNotNull() {
        ItemRequest request = new ItemRequest();
        request.setId(42L);

        Item item = new Item();
        item.setId(1L);
        item.setName("ItemName");
        item.setDescription("ItemDescription");
        item.setAvailable(true);
        item.setRequest(request);

        var dto = itemMapper.toItemDto(item);

        assertThat(dto.getRequestId()).isEqualTo(42L);
    }

    @Test
    void toItemDtoOutput_shouldMapAllFieldsIncludingCommentsAndBookings() {
        Item item = new Item();
        item.setId(2L);
        item.setName("Another Item");
        item.setDescription("Desc");
        item.setAvailable(false);

        User owner = new User();
        owner.setName("OwnerName");
        item.setOwner(owner);

        Comment comment1 = new Comment();
        Comment comment2 = new Comment();

        LocalDateTime now = LocalDateTime.now();

        when(commentMapper.toDto(comment1))
                .thenReturn(new CommentDto(1L, "Text1", "Author1", now.minusDays(1)));
        when(commentMapper.toDto(comment2))
                .thenReturn(new CommentDto(2L, "Text2", "Author2", now.minusDays(2)));

        LocalDateTime lastBooking = LocalDateTime.of(2025, 6, 6, 12, 0);
        LocalDateTime nextBooking = LocalDateTime.of(2025, 6, 7, 12, 0);

        var dto = itemMapper.toItemDtoOutput(item, List.of(comment1, comment2), lastBooking, nextBooking);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getName()).isEqualTo("Another Item");
        assertThat(dto.getDescription()).isEqualTo("Desc");
        assertThat(dto.getAvailable()).isFalse();
        assertThat(dto.getOwnerName()).isEqualTo("OwnerName");
        assertThat(dto.getLastBooking()).isEqualTo(lastBooking);
        assertThat(dto.getNextBooking()).isEqualTo(nextBooking);

        assertThat(dto.getComments()).hasSize(2);
        assertThat(dto.getComments().get(0).getId()).isEqualTo(1L);
        assertThat(dto.getComments().get(0).getText()).isEqualTo("Text1");
        assertThat(dto.getComments().get(0).getAuthorName()).isEqualTo("Author1");
        assertThat(dto.getComments().get(0).getCreated()).isEqualTo(now.minusDays(1));

        assertThat(dto.getComments().get(1).getId()).isEqualTo(2L);
        assertThat(dto.getComments().get(1).getText()).isEqualTo("Text2");
        assertThat(dto.getComments().get(1).getAuthorName()).isEqualTo("Author2");
        assertThat(dto.getComments().get(1).getCreated()).isEqualTo(now.minusDays(2));

        verify(commentMapper, times(1)).toDto(comment1);
        verify(commentMapper, times(1)).toDto(comment2);
    }

    @Test
    void toItemDtoOutput_shouldHandleNullOwner() {
        Item item = new Item();
        item.setId(3L);
        item.setName("No Owner Item");
        item.setDescription("Desc");
        item.setAvailable(true);
        item.setOwner(null);

        List<Comment> comments = new ArrayList<>();

        var dto = itemMapper.toItemDtoOutput(item, comments, null, null);

        assertThat(dto.getOwnerName()).isNull();
        assertThat(dto.getComments()).isEmpty();
    }

    @Test
    void toItem_shouldReturnNullWhenDtoIsNull() {
        assertThat(itemMapper.toItem(null, null, null)).isNull();
    }

    @Test
    void toItem_shouldMapFieldsCorrectly() {
        ItemDto dto = new ItemDto(10L, "DtoName", "DtoDesc", true, List.of(), 5L);

        User owner = new User();
        owner.setId(100L);

        ItemRequest request = new ItemRequest();
        request.setId(5L);

        Item item = itemMapper.toItem(dto, owner, request);

        assertThat(item).isNotNull();
        assertThat(item.getId()).isEqualTo(10L);
        assertThat(item.getName()).isEqualTo("DtoName");
        assertThat(item.getDescription()).isEqualTo("DtoDesc");
        assertThat(item.getAvailable()).isTrue();
        assertThat(item.getOwner()).isEqualTo(owner);
        assertThat(item.getRequest()).isEqualTo(request);
    }
}
