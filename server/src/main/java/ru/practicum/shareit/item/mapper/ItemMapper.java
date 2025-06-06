package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOutput;
import ru.practicum.shareit.item.dto.ItemDtoResponseRequest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapper {

    public ItemDto toItemDto(Item item) {
        if (item == null) return null;

        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                new ArrayList<>(), // comments
                item.getRequest() != null ? item.getRequest().getId() : null // requestId
        );
    }


    public ItemDtoOutput toItemDtoOutput(Item item, List<Comment> comments,
                                         LocalDateTime lastBookingTime, LocalDateTime nextBookingTime) {
        ItemDtoOutput dto = new ItemDtoOutput();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwnerName(item.getOwner() != null ? item.getOwner().getName() : null); // <-- добавил проверку

        dto.setLastBooking(lastBookingTime);
        dto.setNextBooking(nextBookingTime);

        dto.setComments(comments.stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList()));

        return dto;
    }

    public Item toItem(ItemDto itemDto, User owner, ItemRequest request) {
        if (itemDto == null) return null;

        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner,
                request
        );
    }

    public ItemDtoResponseRequest toItemDtoResponseRequest(Item item) {

        final ItemDtoResponseRequest itemDtoResponseRequest = new ItemDtoResponseRequest();

        itemDtoResponseRequest.setItemId(item.getId());
        itemDtoResponseRequest.setOwnerId(item.getOwner().getId());
        itemDtoResponseRequest.setName(item.getName());

        return itemDtoResponseRequest;

    }

    private final CommentMapper commentMapper;

    public ItemMapper(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }
}




