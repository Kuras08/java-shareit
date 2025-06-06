package ru.practicum.shareit.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemShortDto;

import static org.junit.jupiter.api.Assertions.*;

public class ItemShortDtoTest {

    @Test
    public void testNoArgsConstructorAndSetters() {
        ItemShortDto item = new ItemShortDto();

        item.setId(1L);
        item.setName("Test item");
        item.setOwnerId(10L);
        item.setOwnerName("John Doe");

        assertEquals(1L, item.getId());
        assertEquals("Test item", item.getName());
        assertEquals(10L, item.getOwnerId());
        assertEquals("John Doe", item.getOwnerName());
    }

    @Test
    public void testAllArgsConstructorAndGetters() {
        ItemShortDto item = new ItemShortDto(2L, "Another item", 20L, "Jane Smith");

        assertEquals(2L, item.getId());
        assertEquals("Another item", item.getName());
        assertEquals(20L, item.getOwnerId());
        assertEquals("Jane Smith", item.getOwnerName());
    }

    @Test
    public void testEqualsAndHashCode() {
        ItemShortDto item1 = new ItemShortDto(3L, "Name", 30L, "Owner");
        ItemShortDto item2 = new ItemShortDto(3L, "Name", 30L, "Owner");
        ItemShortDto item3 = new ItemShortDto(4L, "Name2", 31L, "Owner2");

        assertEquals(item1, item2);
        assertNotEquals(item1, item3);

        assertEquals(item1.hashCode(), item2.hashCode());
        assertNotEquals(item1.hashCode(), item3.hashCode());
    }

    @Test
    public void testToString() {
        ItemShortDto item = new ItemShortDto(5L, "ItemName", 40L, "OwnerName");
        String str = item.toString();

        assertTrue(str.contains("id=5"));
        assertTrue(str.contains("name=ItemName"));
        assertTrue(str.contains("ownerId=40"));
        assertTrue(str.contains("ownerName=OwnerName"));
    }
}
