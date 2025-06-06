import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItGateway.class)
class CommentDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testDeserializeValidJson() throws Exception {
        String json = "{\"id\":10,\"text\":\"Nice item\",\"authorName\":\"John\",\"created\":\"2025-07-01T12:00:00\"}";

        CommentDto dto = objectMapper.readValue(json, CommentDto.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getText()).isEqualTo("Nice item");
        assertThat(dto.getAuthorName()).isEqualTo("John");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2025, 7, 1, 12, 0));
    }

    @Test
    void testSerialize() throws Exception {
        CommentDto dto = new CommentDto(
                10L,
                "Nice item",
                "John",
                LocalDateTime.of(2025, 7, 1, 12, 0)
        );

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":10");
        assertThat(json).contains("\"text\":\"Nice item\"");
        assertThat(json).contains("\"authorName\":\"John\"");
        assertThat(json).contains("\"created\":\"2025-07-01T12:00:00\"");
    }

    @Test
    void testDeserializeEmptyText() throws Exception {
        String json = "{\"id\":10,\"text\":\"\",\"authorName\":\"John\",\"created\":\"2025-07-01T12:00:00\"}";

        CommentDto dto = objectMapper.readValue(json, CommentDto.class);
        // При пустой строке поле десериализуется, но валидация (NotBlank) не сработает здесь
        assertThat(dto.getText()).isEmpty();
    }
}


