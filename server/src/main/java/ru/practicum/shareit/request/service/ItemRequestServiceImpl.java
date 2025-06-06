package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemShortDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper mapper;

    @Override
    public ItemRequestDto addRequest(Long userId, ItemRequestDto dto) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        ItemRequest request = mapper.toEntity(dto, requester);
        ItemRequest saved = requestRepository.save(request);

        return mapper.toDto(saved, List.of());
    }

    @Override
    public List<ItemRequestDto> getOwnRequests(Long userId) {
        checkUserExists(userId);

        List<ItemRequest> requests = requestRepository.findByRequesterIdOrderByCreatedDesc(userId);
        return mapRequestsWithItems(requests);
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, int from, int size) {
        checkUserExists(userId);

        Pageable pageable = PageRequest.of(from / size, size);
        List<ItemRequest> requests = requestRepository.findAllExcludingUser(userId, pageable).getContent();

        return mapRequestsWithItems(requests);
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        checkUserExists(userId);

        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found: " + requestId));

        List<Item> items = itemRepository.findByRequestId(requestId);
        List<ItemShortDto> itemDtos = items.stream()
                .map(mapper::toItemShortDto)
                .collect(Collectors.toList());

        return mapper.toDto(request, itemDtos);
    }

    private List<ItemRequestDto> mapRequestsWithItems(List<ItemRequest> requests) {
        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        // Получаем все айтемы, у которых request_id в списке requestIds
        List<Item> items = itemRepository.findByRequestIdIn(requestIds);

        // Фильтруем айтемы с null request, чтобы не было NPE
        Map<Long, List<Item>> itemsByRequestId = items.stream()
                .filter(item -> item.getRequest() != null)
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        return requests.stream()
                .map(request -> {
                    List<ItemShortDto> itemDtos = itemsByRequestId.getOrDefault(request.getId(), List.of()).stream()
                            .map(mapper::toItemShortDto)
                            .collect(Collectors.toList());
                    return mapper.toDto(request, itemDtos);
                })
                .collect(Collectors.toList());
    }


    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found: " + userId);
        }
    }
}
