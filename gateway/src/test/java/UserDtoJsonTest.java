import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItGateway.class)
class UserDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testDeserialize() throws Exception {
        String json = """
                {
                    "id": 1,
                    "name": "Alice",
                    "email": "alice@example.com"
                }
                """;

        UserDto dto = objectMapper.readValue(json, UserDto.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Alice");
        assertThat(dto.getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void testSerialize() throws Exception {
        UserDto dto = new UserDto(
                1L,
                "Alice",
                "alice@example.com"
        );

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Alice\"");
        assertThat(json).contains("\"email\":\"alice@example.com\"");
    }
}

