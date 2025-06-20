package ru.practicum.shareit.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Booking booking;
    private BookingDto bookingDto;
    private BookingDtoInput bookingDtoInput;
    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        item = new Item();
        item.setId(1L);
        item.setAvailable(true);
        item.setOwner(new User());
        item.getOwner().setId(2L); // другой владелец по умолчанию

        booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));

        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStatus(BookingStatus.WAITING);

        bookingDtoInput = new BookingDtoInput();
        bookingDtoInput.setItemId(1L);
        bookingDtoInput.setStart(booking.getStart());
        bookingDtoInput.setEnd(booking.getEnd());
    }

    @Test
    void createBooking_shouldReturnBookingDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingMapper.toDto(any())).thenReturn(bookingDto);

        BookingDto result = bookingService.createBooking(1L, bookingDtoInput);

        assertEquals(bookingDto.getId(), result.getId());
        verify(bookingRepository).save(any());
    }

    @Test
    void createBooking_shouldThrowValidationException_forOwnItem() {
        item.getOwner().setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ValidationException ex = assertThrows(ValidationException.class, () ->
                bookingService.createBooking(1L, bookingDtoInput));
        assertEquals("Нельзя бронировать свою вещь", ex.getMessage());
    }

    @Test
    void createBooking_shouldThrowValidationException_ifItemUnavailable() {
        item.setAvailable(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ValidationException ex = assertThrows(ValidationException.class, () ->
                bookingService.createBooking(1L, bookingDtoInput));
        assertEquals("Вещь недоступна для бронирования", ex.getMessage());
    }


    @Test
    void approveBooking_shouldReturnApprovedBookingDto() {
        booking.setStatus(BookingStatus.WAITING);
        item.getOwner().setId(1L);

        Booking approvedBooking = new Booking();
        approvedBooking.setId(1L);
        approvedBooking.setStatus(BookingStatus.APPROVED);

        BookingDto approvedDto = new BookingDto();
        approvedDto.setId(1L);
        approvedDto.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(approvedBooking);
        when(bookingMapper.toDto(any())).thenReturn(approvedDto);

        BookingDto result = bookingService.approveBooking(1L, 1L, true);

        assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    void approveBooking_shouldThrowForbiddenException_ifUserNotOwner() {
        booking.setStatus(BookingStatus.WAITING);
        item.getOwner().setId(2L);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        ForbiddenException ex = assertThrows(ForbiddenException.class, () ->
                bookingService.approveBooking(1L, 1L, true));
        assertEquals("Подтверждение может выполнять только владелец вещи", ex.getMessage());
    }

    @Test
    void approveBooking_shouldThrowValidationException_ifBookingAlreadyProcessed() {
        booking.setStatus(BookingStatus.APPROVED);
        item.getOwner().setId(1L);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        ValidationException ex = assertThrows(ValidationException.class, () ->
                bookingService.approveBooking(1L, 1L, true));
        assertEquals("Бронирование уже рассмотрено", ex.getMessage());
    }

    @Test
    void getBooking_shouldReturnBookingDto_forBooker() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(any())).thenReturn(bookingDto);

        BookingDto result = bookingService.getBooking(1L, 1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void getBooking_shouldThrowNotFoundException_ifUserNotBookerOrOwner() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        long otherUserId = 3L;

        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                bookingService.getBooking(otherUserId, 1L));
        assertEquals("Доступ запрещён", ex.getMessage());
    }

    @Test
    void getUserBookings_shouldThrowNotFound_ifUserNotExist() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () ->
                bookingService.getUserBookings(1L, "ALL"));
    }

    @Test
    void getOwnerBookings_shouldThrowNotFound_ifUserNotExist() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () ->
                bookingService.getOwnerBookings(1L, "ALL"));
    }

    @Test
    void getUserBookings_shouldReturnListByAllStates() {
        when(userRepository.existsById(1L)).thenReturn(true);
        LocalDateTime now = LocalDateTime.now();

        when(bookingRepository.findAllByBookerId(1L)).thenReturn(List.of(booking));
        when(bookingRepository.findCurrentByBookerId(eq(1L), any(LocalDateTime.class))).thenReturn(List.of());
        when(bookingRepository.findPastByBookerId(eq(1L), any(LocalDateTime.class))).thenReturn(List.of());
        when(bookingRepository.findFutureByBookerId(eq(1L), any(LocalDateTime.class))).thenReturn(List.of());
        when(bookingRepository.findWaitingByBookerId(1L)).thenReturn(List.of());
        when(bookingRepository.findRejectedByBookerId(1L)).thenReturn(List.of());

        when(bookingMapper.toDto(any())).thenReturn(bookingDto);

        for (String state : List.of("ALL", "CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED")) {
            List<BookingDto> result = bookingService.getUserBookings(1L, state);
            if (state.equals("ALL")) {
                assertFalse(result.isEmpty());
            } else {
                assertTrue(result.isEmpty());
            }
        }
    }

    @Test
    void getOwnerBookings_shouldReturnListByAllStates() {
        when(userRepository.existsById(1L)).thenReturn(true);
        LocalDateTime now = LocalDateTime.now();

        when(bookingRepository.findAllByOwnerId(1L)).thenReturn(List.of(booking));
        when(bookingRepository.findCurrentByOwnerId(eq(1L), any(LocalDateTime.class))).thenReturn(List.of());
        when(bookingRepository.findPastByOwnerId(eq(1L), any(LocalDateTime.class))).thenReturn(List.of());
        when(bookingRepository.findFutureByOwnerId(eq(1L), any(LocalDateTime.class))).thenReturn(List.of());
        when(bookingRepository.findWaitingByOwnerId(1L)).thenReturn(List.of());
        when(bookingRepository.findRejectedByOwnerId(1L)).thenReturn(List.of());

        when(bookingMapper.toDto(any())).thenReturn(bookingDto);

        for (String state : List.of("ALL", "CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED")) {
            List<BookingDto> result = bookingService.getOwnerBookings(1L, state);
            if (state.equals("ALL")) {
                assertFalse(result.isEmpty());
            } else {
                assertTrue(result.isEmpty());
            }
        }
    }

}


