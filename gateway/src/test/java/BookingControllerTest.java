import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.controller.BookingController;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@ContextConfiguration(classes = ShareItGateway.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingClient bookingClient;

    private ResponseEntity<Object> dummyResponse;

    @BeforeEach
    void setUp() {
        dummyResponse = ResponseEntity.ok().body("response");
    }

    @Test
    void createBooking_shouldReturnOk() throws Exception {
        when(bookingClient.createBooking(anyLong(), any())).thenReturn(dummyResponse);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .contentType("application/json")
                        .content("{\"itemId\":1,\"start\":\"2025-06-06T10:00:00\",\"end\":\"2025-06-07T10:00:00\"}")) // пример JSON, подставь актуальное
                .andExpect(status().isOk())
                .andExpect(content().string("response"));
    }

    @Test
    void approveBooking_shouldReturnOk() throws Exception {
        when(bookingClient.approveBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(dummyResponse);

        mockMvc.perform(patch("/bookings/5")
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string("response"));
    }

    @Test
    void getBooking_shouldReturnOk() throws Exception {
        when(bookingClient.getBooking(anyLong(), anyLong())).thenReturn(dummyResponse);

        mockMvc.perform(get("/bookings/5")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("response"));
    }

    @Test
    void getUserBookings_shouldReturnOk() throws Exception {
        when(bookingClient.getUserBookings(anyLong(), anyString())).thenReturn(dummyResponse);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(content().string("response"));
    }

    @Test
    void getOwnerBookings_shouldReturnOk() throws Exception {
        when(bookingClient.getOwnerBookings(anyLong(), anyString())).thenReturn(dummyResponse);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(content().string("response"));
    }
}
