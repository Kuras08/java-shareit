package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addRequest(Long userId, ItemRequestDto requestDto);

    List<ItemRequestDto> getOwnRequests(Long userId);

    List<ItemRequestDto> getAllRequests(Long userId, int from, int size);

    ItemRequestDto getRequestById(Long userId, Long requestId);
}