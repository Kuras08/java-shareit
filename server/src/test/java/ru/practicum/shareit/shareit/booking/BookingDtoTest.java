package ru.practicum.shareit.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class BookingDtoTest {

    @Test
    void builder_ShouldCreateBookingDtoCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        UserDto userDto = new UserDto(1L, "John", "john@example.com");
        ItemDto itemDto = new ItemDto(2L, "Drill", "Electric drill", true, Collections.emptyList(), null);

        BookingDto bookingDto = BookingDto.builder()
                .id(100L)
                .start(now)
                .end(now.plusDays(1))
                .status(BookingStatus.APPROVED)
                .booker(userDto)
                .item(itemDto)
                .build();

        assertEquals(100L, bookingDto.getId());
        assertEquals(now, bookingDto.getStart());
        assertEquals(now.plusDays(1), bookingDto.getEnd());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
        assertEquals(userDto, bookingDto.getBooker());
        assertEquals(itemDto, bookingDto.getItem());
    }

    @Test
    void noArgsConstructor_ShouldCreateEmptyBookingDto() {
        BookingDto bookingDto = new BookingDto();
        assertNull(bookingDto.getId());
        assertNull(bookingDto.getStart());
        assertNull(bookingDto.getEnd());
        assertNull(bookingDto.getStatus());
        assertNull(bookingDto.getBooker());
        assertNull(bookingDto.getItem());
    }

    @Test
    void allArgsConstructor_ShouldSetAllFields() {
        LocalDateTime now = LocalDateTime.now();
        UserDto userDto = new UserDto(1L, "John", "john@example.com");
        ItemDto itemDto = new ItemDto(2L, "Drill", "Electric drill", true, Collections.emptyList(), null);

        BookingDto bookingDto = new BookingDto(
                100L,
                now,
                now.plusDays(1),
                BookingStatus.WAITING,
                userDto,
                itemDto
        );

        assertEquals(100L, bookingDto.getId());
        assertEquals(now, bookingDto.getStart());
        assertEquals(now.plusDays(1), bookingDto.getEnd());
        assertEquals(BookingStatus.WAITING, bookingDto.getStatus());
        assertEquals(userDto, bookingDto.getBooker());
        assertEquals(itemDto, bookingDto.getItem());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        BookingDto bookingDto = new BookingDto();

        LocalDateTime start = LocalDateTime.of(2025, 6, 6, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 6, 7, 10, 0);
        UserDto userDto = new UserDto(5L, "Alice", "alice@example.com");
        ItemDto itemDto = new ItemDto(7L, "Saw", "Hand saw", false, Collections.emptyList(), 42L);

        bookingDto.setId(55L);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        bookingDto.setStatus(BookingStatus.REJECTED);
        bookingDto.setBooker(userDto);
        bookingDto.setItem(itemDto);

        assertEquals(55L, bookingDto.getId());
        assertEquals(start, bookingDto.getStart());
        assertEquals(end, bookingDto.getEnd());
        assertEquals(BookingStatus.REJECTED, bookingDto.getStatus());
        assertEquals(userDto, bookingDto.getBooker());
        assertEquals(itemDto, bookingDto.getItem());
    }

    @Test
    void equalsAndHashCode_ShouldConsiderAllFields() {
        LocalDateTime start = LocalDateTime.of(2025, 6, 6, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 6, 7, 10, 0);
        UserDto userDto = new UserDto(1L, "John", "john@example.com");
        ItemDto itemDto = new ItemDto(2L, "Drill", "Electric drill", true, Collections.emptyList(), null);

        BookingDto booking1 = BookingDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .status(BookingStatus.APPROVED)
                .booker(userDto)
                .item(itemDto)
                .build();

        BookingDto booking2 = BookingDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .status(BookingStatus.APPROVED)
                .booker(userDto)
                .item(itemDto)
                .build();

        BookingDto booking3 = BookingDto.builder()
                .id(2L)
                .start(start)
                .end(end)
                .status(BookingStatus.WAITING)
                .booker(userDto)
                .item(itemDto)
                .build();

        assertEquals(booking1, booking2);
        assertEquals(booking1.hashCode(), booking2.hashCode());

        assertNotEquals(booking1, booking3);
        assertNotEquals(booking1.hashCode(), booking3.hashCode());
    }

    @Test
    void toString_ShouldContainAllFields() {
        LocalDateTime start = LocalDateTime.of(2025, 6, 6, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 6, 7, 10, 0);
        UserDto userDto = new UserDto(1L, "John", "john@example.com");
        ItemDto itemDto = new ItemDto(2L, "Drill", "Electric drill", true, Collections.emptyList(), null);

        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .status(BookingStatus.APPROVED)
                .booker(userDto)
                .item(itemDto)
                .build();

        String toString = bookingDto.toString();

        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("start="));
        assertTrue(toString.contains("end="));
        assertTrue(toString.contains("status=APPROVED"));
        assertTrue(toString.contains("booker="));
        assertTrue(toString.contains("item="));
    }
}

