import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemShortDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItGateway.class)
class ItemRequestDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testDeserializeValidJson() throws Exception {
        String json = "{\"id\":1,\"description\":\"Описание запроса\",\"created\":" +
                "\"2025-06-06T12:30:00\",\"items\":[{\"id\":2,\"name\":\"Short item name\",\"ownerId\":3}]}";

        ItemRequestDto dto = objectMapper.readValue(json, ItemRequestDto.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Описание запроса");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.parse("2025-06-06T12:30:00"));

        assertThat(dto.getItems()).isNotNull();
        assertThat(dto.getItems()).hasSize(1);
        assertThat(dto.getItems().get(0).getId()).isEqualTo(2L);
        assertThat(dto.getItems().get(0).getName()).isEqualTo("Short item name");
        assertThat(dto.getItems().get(0).getOwnerId()).isEqualTo(3L);
    }

    @Test
    void testSerialize() throws Exception {
        ItemShortDto itemShortDto = new ItemShortDto(2L, "Short item name", 3L);
        ItemRequestDto dto = new ItemRequestDto(1L, "Описание запроса",
                LocalDateTime.of(2025, 6, 6, 12, 30), List.of(itemShortDto));

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"description\":\"Описание запроса\"");
        assertThat(json).contains("\"created\":\"2025-06-06T12:30:00\"");
        assertThat(json).contains("\"items\"");
        assertThat(json).contains("\"id\":2");
        assertThat(json).contains("\"name\":\"Short item name\"");
        assertThat(json).contains("\"ownerId\":3");
    }

    @Test
    void testDeserializeEmptyItems() throws Exception {
        String json = "{\"id\":1,\"description\":\"Описание запроса\"," +
                "\"created\":\"2025-06-06T12:30:00\",\"items\":[]}";

        ItemRequestDto dto = objectMapper.readValue(json, ItemRequestDto.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getItems()).isEmpty();
    }

    @Test
    void testDeserializeMissingItems() throws Exception {
        String json = "{\"id\":1,\"description\":\"Описание запроса\",\"created\":\"2025-06-06T12:30:00\"}";

        ItemRequestDto dto = objectMapper.readValue(json, ItemRequestDto.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getItems()).isNotNull();
        assertThat(dto.getItems()).isEmpty();
    }

    @Test
    void testDeserializeNullDescription() throws Exception {
        String json = "{\"id\":1,\"description\":null,\"created\":\"2025-06-06T12:30:00\"}";

        ItemRequestDto dto = objectMapper.readValue(json, ItemRequestDto.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getDescription()).isNull();
    }
}


