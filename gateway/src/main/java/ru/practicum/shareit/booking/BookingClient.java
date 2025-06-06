package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.client.BaseClient;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createBooking(Long userId, BookingDtoInput dto) {
        return post("", userId, dto);
    }

    public ResponseEntity<Object> approveBooking(Long userId, Long bookingId, boolean approved) {
        String path = "/" + bookingId + "?approved=" + approved;
        System.out.println("BookingClient.approveBooking called with userId=" + userId + ", path=" + path);
        return patch(path, userId, null, null);
    }

    public ResponseEntity<Object> getBooking(Long userId, Long bookingId) {
        String path = "/" + bookingId;
        return get(path, userId);
    }

    public ResponseEntity<Object> getUserBookings(Long userId, String state) {
        String path = "?state=" + state;
        return get(path, userId);
    }

    public ResponseEntity<Object> getOwnerBookings(Long userId, String state) {
        String path = "/owner?state=" + state;
        return get(path, userId);
    }
}


