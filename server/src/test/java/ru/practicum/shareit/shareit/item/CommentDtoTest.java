package ru.practicum.shareit.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentDtoTest {

    @Test
    void allArgsConstructor_ShouldSetAllFields() {
        LocalDateTime now = LocalDateTime.now();
        CommentDto comment = new CommentDto(1L, "Nice item", "Alice", now);

        assertEquals(1L, comment.getId());
        assertEquals("Nice item", comment.getText());
        assertEquals("Alice", comment.getAuthorName());
        assertEquals(now, comment.getCreated());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        CommentDto comment = new CommentDto();

        LocalDateTime now = LocalDateTime.now();

        comment.setId(2L);
        comment.setText("Great!");
        comment.setAuthorName("Bob");
        comment.setCreated(now);

        assertEquals(2L, comment.getId());
        assertEquals("Great!", comment.getText());
        assertEquals("Bob", comment.getAuthorName());
        assertEquals(now, comment.getCreated());
    }

    @Test
    void equalsAndHashCode_ShouldBeConsistent() {
        LocalDateTime now = LocalDateTime.now();

        CommentDto c1 = new CommentDto(1L, "Text", "Author", now);
        CommentDto c2 = new CommentDto(1L, "Text", "Author", now);
        CommentDto c3 = new CommentDto(2L, "Text", "Author", now);

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());

        assertNotEquals(c1, c3);
        assertNotEquals(c1.hashCode(), c3.hashCode());
    }

    @Test
    void toString_ShouldContainAllFields() {
        LocalDateTime now = LocalDateTime.now();
        CommentDto comment = new CommentDto(10L, "Sample", "User", now);

        String str = comment.toString();

        assertTrue(str.contains("id=10"));
        assertTrue(str.contains("text=Sample"));
        assertTrue(str.contains("authorName=User"));
        assertTrue(str.contains("created="));
    }
}
