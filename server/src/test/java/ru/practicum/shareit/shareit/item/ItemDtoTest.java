package ru.practicum.shareit.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemDtoTest {

    private CommentDto createComment(Long id) {
        return new CommentDto(id, "Comment text " + id, "Author " + id, LocalDateTime.now());
    }

    @Test
    void noArgsConstructor_ShouldCreateEmptyObject() {
        ItemDto item = new ItemDto();
        assertNull(item.getId());
        assertNull(item.getName());
        assertNull(item.getDescription());
        assertNull(item.getAvailable());
        assertNull(item.getComments());
        assertNull(item.getRequestId());
    }

    @Test
    void allArgsConstructor_ShouldSetAllFields() {
        List<CommentDto> comments = Arrays.asList(createComment(1L), createComment(2L));
        ItemDto item = new ItemDto(1L, "Hammer", "Heavy hammer", true, comments, 100L);

        assertEquals(1L, item.getId());
        assertEquals("Hammer", item.getName());
        assertEquals("Heavy hammer", item.getDescription());
        assertTrue(item.getAvailable());
        assertEquals(comments, item.getComments());
        assertEquals(100L, item.getRequestId());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        ItemDto item = new ItemDto();

        item.setId(10L);
        item.setName("Saw");
        item.setDescription("Hand saw");
        item.setAvailable(false);
        item.setComments(Arrays.asList(createComment(3L), createComment(4L)));
        item.setRequestId(200L);

        assertEquals(10L, item.getId());
        assertEquals("Saw", item.getName());
        assertEquals("Hand saw", item.getDescription());
        assertFalse(item.getAvailable());
        assertEquals(2, item.getComments().size());
        assertEquals(200L, item.getRequestId());
    }

    @Test
    void equalsAndHashCode_ShouldBeConsistent() {
        List<CommentDto> comments1 = List.of(createComment(1L));
        List<CommentDto> comments2 = List.of(createComment(1L));

        ItemDto item1 = new ItemDto(1L, "Drill", "Electric drill", true, comments1, 10L);
        ItemDto item2 = new ItemDto(1L, "Drill", "Electric drill", true, comments2, 10L);
        ItemDto item3 = new ItemDto(2L, "Drill", "Electric drill", true, comments2, 10L);

        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());

        assertNotEquals(item1, item3);
        assertNotEquals(item1.hashCode(), item3.hashCode());
    }

    @Test
    void toString_ShouldContainAllFields() {
        List<CommentDto> comments = List.of(createComment(5L));
        ItemDto item = new ItemDto(1L, "Drill", "Electric drill", true, comments, 42L);
        String str = item.toString();

        assertTrue(str.contains("id=1"));
        assertTrue(str.contains("name=Drill"));
        assertTrue(str.contains("description=Electric drill"));
        assertTrue(str.contains("available=true"));
        assertTrue(str.contains("comments="));
        assertTrue(str.contains("requestId=42"));
    }
}

