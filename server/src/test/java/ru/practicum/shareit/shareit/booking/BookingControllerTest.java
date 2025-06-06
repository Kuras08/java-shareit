package ru.practicum.shareit.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = ShareItServer.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookingControllerTest {

    private static final String HEADER = "X-Sharer-User-Id";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;
    private BookingDto bookingDtoOutput;
    private BookingDtoInput bookingDtoInput;

    @BeforeEach
    void setUp() {
        bookingDtoOutput = new BookingDto();
        bookingDtoOutput.setId(1L);
        bookingDtoOutput.setStatus(BookingStatus.WAITING);

        bookingDtoInput = new BookingDtoInput();
        bookingDtoInput.setItemId(1L);
        bookingDtoInput.setStart(LocalDateTime.now().plusHours(1)); // всегда в будущем
        bookingDtoInput.setEnd(LocalDateTime.now().plusDays(1));
    }

    @Test
    @Order(1)
    @DisplayName("BookingController_createBooking")
    void testCreateBooking() throws Exception {
        when(bookingService.createBooking(anyLong(), any(BookingDtoInput.class)))
                .thenReturn(bookingDtoOutput);

        String json = objectMapper.writeValueAsString(bookingDtoInput);

        mockMvc.perform(post("/bookings")
                        .header(HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())  // в твоём контроллере не указан @ResponseStatus(CREATED), значит по умолчанию 200 OK
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    @Order(2)
    @DisplayName("BookingController_approveBooking")
    void testApproveBooking() throws Exception {
        bookingDtoOutput.setStatus(BookingStatus.APPROVED);

        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDtoOutput);

        mockMvc.perform(patch("/bookings/1")
                        .header(HEADER, 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @Order(3)
    @DisplayName("BookingController_getBookingById")
    void testGetBookingById() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(bookingDtoOutput);

        mockMvc.perform(get("/bookings/1")
                        .header(HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @Order(4)
    @DisplayName("BookingController_getUserBookings")
    void testGetUserBookings() throws Exception {
        when(bookingService.getUserBookings(anyLong(), anyString()))
                .thenReturn(Collections.singletonList(bookingDtoOutput));

        mockMvc.perform(get("/bookings")
                        .header(HEADER, 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @Order(5)
    @DisplayName("BookingController_getOwnerBookings")
    void testGetOwnerBookings() throws Exception {
        when(bookingService.getOwnerBookings(anyLong(), anyString()))
                .thenReturn(Collections.singletonList(bookingDtoOutput));

        mockMvc.perform(get("/bookings/owner")
                        .header(HEADER, 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}
