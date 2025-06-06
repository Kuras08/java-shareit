import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItGateway.class)
class ItemDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testDeserializeValidJson() throws Exception {
        String json = """
                {
                    "id": 1,
                    "name": "Item name",
                    "description": "Item description",
                    "available": true,
                    "comments": [
                        {
                            "id": 10,
                            "text": "Nice item",
                            "authorName": "John"
                        }
                    ],
                    "requestId": 5
                }
                """;

        ItemDto dto = objectMapper.readValue(json, ItemDto.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Item name");
        assertThat(dto.getDescription()).isEqualTo("Item description");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getRequestId()).isEqualTo(5L);

        assertThat(dto.getComments()).isNotNull();
        assertThat(dto.getComments()).hasSize(1);
        assertThat(dto.getComments().get(0).getId()).isEqualTo(10L);
        assertThat(dto.getComments().get(0).getText()).isEqualTo("Nice item");
        assertThat(dto.getComments().get(0).getAuthorName()).isEqualTo("John");
    }

    @Test
    void testSerialize() throws Exception {
        CommentDto comment = new CommentDto(10L, "Nice item", "John", LocalDateTime.now());
        ItemDto dto = new ItemDto(1L, "Item name", "Item description", true, List.of(comment), 5L);

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Item name\"");
        assertThat(json).contains("\"description\":\"Item description\"");
        assertThat(json).contains("\"available\":true");
        assertThat(json).contains("\"requestId\":5");
        assertThat(json).contains("\"comments\"");
        assertThat(json).contains("Nice item");
        assertThat(json).contains("John");
    }

    @Test
    void testDeserializeNullName() {
        String json = """
                {
                    "id": 1,
                    "name": null,
                    "description": "Description",
                    "available": true
                }
                """;

        ItemDto dto = null;
        try {
            dto = objectMapper.readValue(json, ItemDto.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isNull();
    }

    @Test
    void testDeserializeMissingAvailable() throws Exception {
        String json = """
                {
                    "id": 1,
                    "name": "Item",
                    "description": "Description"
                }
                """;

        ItemDto dto = objectMapper.readValue(json, ItemDto.class);
        assertThat(dto.getAvailable()).isNull();
    }
}

