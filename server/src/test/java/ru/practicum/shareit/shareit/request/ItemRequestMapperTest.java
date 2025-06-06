package ru.practicum.shareit.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemShortDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRequestMapperTest {

    private ItemRequestMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ItemRequestMapper();
    }

    @Test
    void toEntity_shouldMapDtoToEntity() {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("Test description");

        User requester = new User();
        requester.setId(10L);
        requester.setName("UserName");

        ItemRequest entity = mapper.toEntity(dto, requester);

        assertThat(entity).isNotNull();
        assertThat(entity.getDescription()).isEqualTo(dto.getDescription());
        assertThat(entity.getRequester()).isEqualTo(requester);
        assertThat(entity.getCreated()).isNotNull();
        // Проверим, что created установлен примерно сейчас (не больше чем 2 секунды назад)
        assertThat(entity.getCreated()).isAfter(LocalDateTime.now().minusSeconds(2));
    }

    @Test
    void toDto_shouldMapEntityToDto() {
        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Request description");
        request.setCreated(LocalDateTime.of(2025, 6, 6, 12, 30));

        ItemShortDto itemShortDto = new ItemShortDto(2L, "name", 3L, "ownerName");
        List<ItemShortDto> items = List.of(itemShortDto);

        ItemRequestDto dto = mapper.toDto(request, items);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(request.getId());
        assertThat(dto.getDescription()).isEqualTo(request.getDescription());
        assertThat(dto.getCreated()).isEqualTo(request.getCreated());
        assertThat(dto.getItems()).isEqualTo(items);
    }

    @Test
    void toItemShortDto_shouldMapItemToItemShortDto() {
        User owner = new User();
        owner.setId(5L);
        owner.setName("OwnerName");

        Item item = new Item();
        item.setId(100L);
        item.setName("ItemName");
        item.setOwner(owner);

        ItemShortDto dto = mapper.toItemShortDto(item);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(item.getId());
        assertThat(dto.getName()).isEqualTo(item.getName());
        assertThat(dto.getOwnerId()).isEqualTo(owner.getId());
        assertThat(dto.getOwnerName()).isEqualTo(owner.getName());
    }
}
