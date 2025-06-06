package ru.practicum.shareit.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOutput;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private Item item;
    private ItemDto itemDto;
    private ItemRequest itemRequest;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setName("User");

        itemRequest = new ItemRequest();
        itemRequest.setId(100L);

        item = new Item();
        item.setId(10L);
        item.setName("Drill");
        item.setOwner(user);
        item.setAvailable(true);

        itemDto = new ItemDto();
        itemDto.setName("Drill");
        itemDto.setDescription("Electric drill");
        itemDto.setAvailable(true);
        itemDto.setRequestId(null);
    }

    @Test
    void addItem_Success() {
        itemDto.setRequestId(100L);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.existsByOwnerIdAndNameIgnoreCase(user.getId(), itemDto.getName())).thenReturn(false);
        when(itemRequestRepository.findById(100L)).thenReturn(Optional.of(itemRequest));
        when(itemMapper.toItem(itemDto, user, itemRequest)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        ItemDto result = itemService.addItem(user.getId(), itemDto);

        assertNotNull(result);
        assertEquals(itemDto.getName(), result.getName());

        verify(userRepository).findById(user.getId());
        verify(itemRepository).existsByOwnerIdAndNameIgnoreCase(user.getId(), itemDto.getName());
        verify(itemRequestRepository).findById(100L);
        verify(itemRepository).save(item);
        verify(itemMapper).toItemDto(item);
    }

    @Test
    void addItem_DuplicateName_Throws() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.existsByOwnerIdAndNameIgnoreCase(user.getId(), itemDto.getName())).thenReturn(true);

        DuplicatedDataException ex = assertThrows(DuplicatedDataException.class,
                () -> itemService.addItem(user.getId(), itemDto));
        assertEquals("Вещь с таким именем уже существует", ex.getMessage());

        verify(userRepository).findById(user.getId());
        verify(itemRepository).existsByOwnerIdAndNameIgnoreCase(user.getId(), itemDto.getName());
        verify(itemRepository, never()).save(any());
    }

    @Test
    void updateItem_Success() {
        itemDto.setName("New Drill");
        itemDto.setDescription("New description");
        itemDto.setAvailable(false);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(itemMapper.toItemDto(any(Item.class))).thenReturn(itemDto);

        ItemDto updated = itemService.updateItem(user.getId(), item.getId(), itemDto);

        assertEquals("New Drill", updated.getName());
        assertEquals("New description", updated.getDescription());
        assertFalse(updated.getAvailable());

        verify(itemRepository).findById(item.getId());
        verify(itemRepository).save(item);
        verify(itemMapper).toItemDto(item);
    }

    @Test
    void updateItem_NotOwner_Throws() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        Long anotherUserId = 999L;
        ForbiddenException ex = assertThrows(ForbiddenException.class,
                () -> itemService.updateItem(anotherUserId, item.getId(), itemDto));

        assertEquals("Пользователь не является владельцем вещи", ex.getMessage());
        verify(itemRepository).findById(item.getId());
        verify(itemRepository, never()).save(any());
    }

    @Test
    void getItemById_NonOwnerNoBookingTimes() {
        Long otherUserId = 999L;

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(commentRepository.findByItemIdOrderByCreatedDesc(item.getId())).thenReturn(Collections.emptyList());
        when(itemMapper.toItemDtoOutput(eq(item), anyList(), isNull(), isNull()))
                .thenReturn(new ItemDtoOutput());

        ItemDtoOutput result = itemService.getItemById(otherUserId, item.getId());

        assertNotNull(result);
        verify(itemRepository).findById(item.getId());
        verify(commentRepository).findByItemIdOrderByCreatedDesc(item.getId());
        verify(bookingRepository, never()).findLastBookingTime(anyLong(), any());
        verify(bookingRepository, never()).findNextBookingTime(anyLong(), any());
        verify(itemMapper).toItemDtoOutput(eq(item), anyList(), isNull(), isNull());
    }


    @Test
    void getItemsByOwner_UserNotFound_Throws() {
        when(userRepository.existsById(user.getId())).thenReturn(false);

        NoSuchElementException ex = assertThrows(NoSuchElementException.class,
                () -> itemService.getItemsByOwner(user.getId()));

        assertEquals("Пользователь не найден", ex.getMessage());

        verify(userRepository).existsById(user.getId());
        verify(itemRepository, never()).findAllByOwnerId(anyLong());
    }

    @Test
    void searchItems_EmptyText_ReturnsEmptyList() {
        List<ItemDto> result = itemService.searchItems("  ");

        assertTrue(result.isEmpty());
        verify(itemRepository, never()).findAvailableByText(anyString());
    }

    @Test
    void searchItems_FoundItems() {
        List<Item> items = List.of(item);
        List<ItemDto> itemDtos = List.of(itemDto);

        when(itemRepository.findAvailableByText("drill")).thenReturn(items);
        when(itemMapper.toItemDto(any())).thenReturn(itemDto);

        List<ItemDto> result = itemService.searchItems("drill");

        assertEquals(1, result.size());
        verify(itemRepository).findAvailableByText("drill");
        verify(itemMapper).toItemDto(any());
    }

    @Test
    void addComment_Success() {
        Long userId = user.getId();
        Long itemId = item.getId();
        LocalDateTime now = LocalDateTime.now();

        CommentDto commentDtoInput = new CommentDto(null, "Отличная вещь!", "Иван", now);

        Comment savedComment = new Comment();
        savedComment.setId(1L);
        savedComment.setText(commentDtoInput.getText());
        savedComment.setAuthor(user);
        savedComment.setItem(item);
        savedComment.setCreated(now);

        CommentDto expectedCommentDto = new CommentDto(1L, "Отличная вещь!", user.getName(), now);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.existsBookingFinished(eq(userId), eq(itemId), eq(BookingStatus.APPROVED), any(LocalDateTime.class))).thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);
        when(commentMapper.toDto(savedComment)).thenReturn(expectedCommentDto);

        CommentDto actual = itemService.addComment(userId, itemId, commentDtoInput);

        assertNotNull(actual);
        assertEquals(expectedCommentDto.getId(), actual.getId());
        assertEquals(expectedCommentDto.getText(), actual.getText());
        assertEquals(expectedCommentDto.getAuthorName(), actual.getAuthorName());
        assertEquals(expectedCommentDto.getCreated(), actual.getCreated());

        verify(userRepository).findById(userId);
        verify(itemRepository).findById(itemId);
        verify(bookingRepository).existsBookingFinished(eq(userId), eq(itemId), eq(BookingStatus.APPROVED), any(LocalDateTime.class));
        verify(commentRepository).save(any(Comment.class));
        verify(commentMapper).toDto(savedComment);
    }

    @Test
    void addComment_NoBooking_Throws() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.existsBookingFinished(anyLong(), anyLong(), any(), any())).thenReturn(false);

        ValidationException ex = assertThrows(ValidationException.class,
                () -> itemService.addComment(user.getId(), item.getId(), new CommentDto(null, "text", null, null)));

        assertEquals("Можно оставлять отзывы только после завершённой аренды", ex.getMessage());

        verify(userRepository).findById(user.getId());
        verify(itemRepository).findById(item.getId());
        verify(bookingRepository).existsBookingFinished(anyLong(), anyLong(), any(), any());
        verify(commentRepository, never()).save(any());
    }
}



