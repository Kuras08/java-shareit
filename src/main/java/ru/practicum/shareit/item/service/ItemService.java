package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOutput;

import java.util.List;

public interface ItemService {

    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    ItemDtoOutput getItemById(Long itemId, Long userId);

    List<ItemDtoOutput> getItemsByOwner(Long ownerId);

    List<ItemDto> searchItems(String text);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}