import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.request.dto.ItemShortDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItGateway.class)
class ItemShortDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testDeserializeValidJson() throws Exception {
        String json = "{\"id\":2,\"name\":\"Short item name\",\"ownerId\":3}";

        ItemShortDto dto = objectMapper.readValue(json, ItemShortDto.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getName()).isEqualTo("Short item name");
        assertThat(dto.getOwnerId()).isEqualTo(3L);
    }

    @Test
    void testSerialize() throws Exception {
        ItemShortDto dto = new ItemShortDto(2L, "Short item name", 3L);

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":2");
        assertThat(json).contains("\"name\":\"Short item name\"");
        assertThat(json).contains("\"ownerId\":3");
    }

    @Test
    void testDeserializeNullName() throws Exception {
        String json = "{\"id\":2,\"name\":null,\"ownerId\":3}";

        ItemShortDto dto = objectMapper.readValue(json, ItemShortDto.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isNull();
    }

    @Test
    void testDeserializeMissingOwnerId() throws Exception {
        String json = "{\"id\":2,\"name\":\"Short item name\"}";

        ItemShortDto dto = objectMapper.readValue(json, ItemShortDto.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getOwnerId()).isNull();
    }
}
