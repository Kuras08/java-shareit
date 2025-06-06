package ru.practicum.shareit.request.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemShortDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemRequestMapper {

    public ItemRequest toEntity(ItemRequestDto dto, User requester) {
        ItemRequest request = new ItemRequest();
        request.setDescription(dto.getDescription());
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());
        return request;
    }

    public ItemRequestDto toDto(ItemRequest request, List<ItemShortDto> items) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        dto.setItems(items);
        return dto;
    }

    public ItemShortDto toItemShortDto(Item item) {
        return new ItemShortDto(
                item.getId(),
                item.getName(),
                item.getOwner().getId(),
                item.getOwner().getName()  // добавлено
        );
    }
}


