package ru.practicum.shareit.shareit.request;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemShortDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemRequestMapper mapper;

    @InjectMocks
    private ItemRequestServiceImpl service;

    private User user;
    private ItemRequest request;
    private ItemRequestDto requestDto;
    private Item item;
    private ItemShortDto itemShortDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        request = new ItemRequest();
        request.setId(10L);
        request.setRequester(user);

        requestDto = new ItemRequestDto();
        requestDto.setId(10L);
        // Можно добавить поля по необходимости

        item = new Item();
        item.setId(100L);
        item.setName("ItemName");
        item.setOwner(user);
        item.setRequest(request);

        itemShortDto = new ItemShortDto(item.getId(), item.getName(), user.getId(), "OwnerName");
    }

    @Test
    void addRequest_whenUserExists_thenReturnsDto() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(mapper.toEntity(any(ItemRequestDto.class), eq(user))).thenReturn(request);
        when(requestRepository.save(request)).thenReturn(request);
        when(mapper.toDto(eq(request), anyList())).thenReturn(requestDto);

        ItemRequestDto result = service.addRequest(user.getId(), requestDto);

        assertNotNull(result);
        verify(userRepository).findById(user.getId());
        verify(requestRepository).save(request);
        verify(mapper).toDto(eq(request), anyList());
    }

    @Test
    void addRequest_whenUserNotFound_thenThrowNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.addRequest(user.getId(), requestDto));
        assertEquals("User not found: " + user.getId(), ex.getMessage());
    }

    @Test
    void getOwnRequests_whenUserExists_thenReturnsList() {
        List<ItemRequest> requests = List.of(request);

        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(requestRepository.findByRequesterIdOrderByCreatedDesc(user.getId())).thenReturn(requests);

        // Мокаем itemRepository.findByRequestIdIn и mapper.toDto для полного вызова mapRequestsWithItems
        when(itemRepository.findByRequestIdIn(List.of(request.getId()))).thenReturn(List.of(item));
        when(mapper.toItemShortDto(item)).thenReturn(itemShortDto);
        when(mapper.toDto(eq(request), anyList())).thenReturn(requestDto);

        List<ItemRequestDto> result = service.getOwnRequests(user.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).existsById(user.getId());
        verify(requestRepository).findByRequesterIdOrderByCreatedDesc(user.getId());
        verify(itemRepository).findByRequestIdIn(anyList());
    }

    @Test
    void getOwnRequests_whenUserNotExists_thenThrowNotFound() {
        when(userRepository.existsById(user.getId())).thenReturn(false);

        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.getOwnRequests(user.getId()));
        assertEquals("User not found: " + user.getId(), ex.getMessage());
    }

    @Test
    void getAllRequests_whenUserExists_thenReturnsList() {
        List<ItemRequest> requests = List.of(request);

        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(requestRepository.findAllExcludingUser(eq(user.getId()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(requests));

        when(itemRepository.findByRequestIdIn(List.of(request.getId()))).thenReturn(List.of(item));
        when(mapper.toItemShortDto(item)).thenReturn(itemShortDto);
        when(mapper.toDto(eq(request), anyList())).thenReturn(requestDto);

        List<ItemRequestDto> result = service.getAllRequests(user.getId(), 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(userRepository).existsById(user.getId());
        verify(requestRepository).findAllExcludingUser(eq(user.getId()), any(Pageable.class));
        verify(itemRepository).findByRequestIdIn(anyList());
    }

    @Test
    void getAllRequests_whenUserNotExists_thenThrowNotFound() {
        when(userRepository.existsById(user.getId())).thenReturn(false);

        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.getAllRequests(user.getId(), 0, 10));
        assertEquals("User not found: " + user.getId(), ex.getMessage());
    }

    @Test
    void getRequestById_whenUserAndRequestExist_thenReturnsDto() {
        List<Item> items = List.of(item);
        List<ItemShortDto> itemShortDtos = List.of(itemShortDto);

        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(requestRepository.findById(request.getId())).thenReturn(Optional.of(request));
        when(itemRepository.findByRequestId(request.getId())).thenReturn(items);
        when(mapper.toItemShortDto(item)).thenReturn(itemShortDto);
        when(mapper.toDto(request, itemShortDtos)).thenReturn(requestDto);

        ItemRequestDto result = service.getRequestById(user.getId(), request.getId());

        assertNotNull(result);

        verify(userRepository).existsById(user.getId());
        verify(requestRepository).findById(request.getId());
        verify(itemRepository).findByRequestId(request.getId());
        verify(mapper).toDto(request, itemShortDtos);
    }

    @Test
    void getRequestById_whenUserNotExists_thenThrowNotFound() {
        when(userRepository.existsById(user.getId())).thenReturn(false);

        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.getRequestById(user.getId(), request.getId()));
        assertEquals("User not found: " + user.getId(), ex.getMessage());
    }

    @Test
    void getRequestById_whenRequestNotFound_thenThrowNotFound() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(requestRepository.findById(request.getId())).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.getRequestById(user.getId(), request.getId()));
        assertEquals("Request not found: " + request.getId(), ex.getMessage());
    }
}


