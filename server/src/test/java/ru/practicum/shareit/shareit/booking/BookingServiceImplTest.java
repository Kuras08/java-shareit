package ru.practicum.shareit.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = ShareItServer.class)
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
        item.getOwner().setId(2L); // другой владелец

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
        item.getOwner().setId(1L); // тот же, что booker

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () ->
                bookingService.createBooking(1L, bookingDtoInput));
    }

    @Test
    void approveBooking_shouldReturnApprovedBookingDto() {
        booking.setStatus(BookingStatus.WAITING);
        item.getOwner().setId(1L); // владелец

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
    void getBooking_shouldReturnBookingDto_forBooker() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(any())).thenReturn(bookingDto);

        BookingDto result = bookingService.getBooking(1L, 1L);

        assertEquals(1L, result.getId());
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
}
