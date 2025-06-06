import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItGateway.class)
class ItemRequestDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testDeserialize() throws Exception {
        String json = "{\"id\":5,\"description\":\"Need a drill\",\"created\":\"2025-07-01T15:30:00\",\"items\":[]}";

        ItemRequestDto dto = objectMapper.readValue(json, ItemRequestDto.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getDescription()).isEqualTo("Need a drill");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2025, 7, 1, 15, 30));
        assertThat(dto.getItems()).isNotNull();
        assertThat(dto.getItems()).isEmpty();
    }

    @Test
    void testSerialize() throws Exception {
        ItemRequestDto dto = new ItemRequestDto(
                5L,
                "Need a drill",
                LocalDateTime.of(2025, 7, 1, 15, 30),
                List.of()
        );

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":5");
        assertThat(json).contains("\"description\":\"Need a drill\"");
        assertThat(json).contains("\"created\":\"2025-07-01T15:30:00\"");
        assertThat(json).contains("\"items\":[]");
    }
}


