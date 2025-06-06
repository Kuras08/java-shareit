package ru.practicum.shareit.request;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
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
    private ItemRequestDto requestDto;
    private ItemRequest request;
    private Item item;
    private ItemShortDto itemShortDto;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);

        requestDto = new ItemRequestDto();
        requestDto.setDescription("Test request");

        request = new ItemRequest();
        request.setId(10L);
        request.setDescription("Test request");
        request.setRequester(user);

        item = new Item();
        item.setId(100L);
        item.setRequest(request);

        itemShortDto = new ItemShortDto();
        itemShortDto.setId(100L);
    }

    @Test
    void addRequest_UserExists_SavesAndReturnsDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(mapper.toEntity(requestDto, user)).thenReturn(request);
        when(requestRepository.save(request)).thenReturn(request);
        when(mapper.toDto(request, List.of())).thenReturn(requestDto);

        ItemRequestDto result = service.addRequest(1L, requestDto);

        assertNotNull(result);
        assertEquals(requestDto, result);

        verify(userRepository).findById(1L);
        verify(requestRepository).save(request);
        verify(mapper).toDto(request, List.of());
    }

    @Test
    void addRequest_UserNotFound_ThrowsNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
            service.addRequest(1L, requestDto);
        });

        assertEquals("User not found: 1", ex.getMessage());
        verify(requestRepository, never()).save(any());
    }

    @Test
    void getOwnRequests_UserExists_ReturnsRequestsWithItems() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(requestRepository.findByRequesterIdOrderByCreatedDesc(1L)).thenReturn(List.of(request));
        when(itemRepository.findByRequestIdIn(List.of(10L))).thenReturn(List.of(item));
        when(mapper.toItemShortDto(item)).thenReturn(itemShortDto);
        when(mapper.toDto(request, List.of(itemShortDto))).thenReturn(requestDto);

        List<ItemRequestDto> result = service.getOwnRequests(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(requestDto, result.get(0));

        verify(userRepository).existsById(1L);
        verify(requestRepository).findByRequesterIdOrderByCreatedDesc(1L);
        verify(itemRepository).findByRequestIdIn(List.of(10L));
        verify(mapper).toItemShortDto(item);
        verify(mapper).toDto(request, List.of(itemShortDto));
    }

    @Test
    void getOwnRequests_UserNotFound_ThrowsNotFoundException() {
        when(userRepository.existsById(1L)).thenReturn(false);

        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.getOwnRequests(1L));
        assertEquals("User not found: 1", ex.getMessage());

        verify(requestRepository, never()).findByRequesterIdOrderByCreatedDesc(anyLong());
    }

    @Test
    void getAllRequests_UserExists_ReturnsPaginatedRequestsWithItems() {
        when(userRepository.existsById(1L)).thenReturn(true);
        Page<ItemRequest> page = new PageImpl<>(List.of(request));
        when(requestRepository.findAllExcludingUser(eq(1L), any(Pageable.class))).thenReturn(page);
        when(itemRepository.findByRequestIdIn(List.of(10L))).thenReturn(List.of(item));
        when(mapper.toItemShortDto(item)).thenReturn(itemShortDto);
        when(mapper.toDto(request, List.of(itemShortDto))).thenReturn(requestDto);

        List<ItemRequestDto> result = service.getAllRequests(1L, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(requestDto, result.get(0));

        verify(userRepository).existsById(1L);
        verify(requestRepository).findAllExcludingUser(eq(1L), any(Pageable.class));
        verify(itemRepository).findByRequestIdIn(List.of(10L));
    }

    @Test
    void getRequestById_UserExistsAndRequestExists_ReturnsDtoWithItems() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(requestRepository.findById(10L)).thenReturn(Optional.of(request));
        when(itemRepository.findByRequestId(10L)).thenReturn(List.of(item));
        when(mapper.toItemShortDto(item)).thenReturn(itemShortDto);
        when(mapper.toDto(request, List.of(itemShortDto))).thenReturn(requestDto);

        ItemRequestDto result = service.getRequestById(1L, 10L);

        assertNotNull(result);
        assertEquals(requestDto, result);

        verify(userRepository).existsById(1L);
        verify(requestRepository).findById(10L);
        verify(itemRepository).findByRequestId(10L);
        verify(mapper).toDto(request, List.of(itemShortDto));
    }

    @Test
    void getRequestById_UserNotFound_ThrowsNotFoundException() {
        when(userRepository.existsById(1L)).thenReturn(false);

        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.getRequestById(1L, 10L));
        assertEquals("User not found: 1", ex.getMessage());

        verify(requestRepository, never()).findById(anyLong());
    }

    @Test
    void getRequestById_RequestNotFound_ThrowsNotFoundException() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(requestRepository.findById(10L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.getRequestById(1L, 10L));
        assertEquals("Request not found: 10", ex.getMessage());
    }
}
