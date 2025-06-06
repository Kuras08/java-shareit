import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.booking.dto.BookingDtoInput;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItGateway.class)
class BookingDtoInputJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testDeserializeValidJson() throws Exception {
        String json = """
                {
                    "start": "2025-07-01T12:00:00",
                    "end": "2025-07-02T12:00:00",
                    "itemId": 123
                }
                """;

        BookingDtoInput dto = objectMapper.readValue(json, BookingDtoInput.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2025, 7, 1, 12, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2025, 7, 2, 12, 0));
        assertThat(dto.getItemId()).isEqualTo(123L);
    }

    @Test
    void testSerialize() throws Exception {
        BookingDtoInput dto = new BookingDtoInput();
        dto.setStart(LocalDateTime.of(2025, 7, 1, 12, 0));
        dto.setEnd(LocalDateTime.of(2025, 7, 2, 12, 0));
        dto.setItemId(123L);

        String json = objectMapper.writeValueAsString(dto);

        // Проверим, что JSON содержит ключи и значения в ISO формате
        assertThat(json).contains("\"start\":\"2025-07-01T12:00:00\"");
        assertThat(json).contains("\"end\":\"2025-07-02T12:00:00\"");
        assertThat(json).contains("\"itemId\":123");
    }

    @Test
    void testDeserializeInvalidDateFormat() {
        String json = """
                {
                    "start": "07-01-2025 12:00:00",
                    "end": "2025-07-02T12:00:00",
                    "itemId": 123
                }
                """;

        // Попытка десериализации с неверным форматом даты должна выбросить исключение
        org.junit.jupiter.api.Assertions.assertThrows(
                com.fasterxml.jackson.databind.exc.InvalidFormatException.class,
                () -> objectMapper.readValue(json, BookingDtoInput.class));
    }
}











