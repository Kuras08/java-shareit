package ru.practicum.shareit.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
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
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = ShareItServer.class)
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
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "user", "user@example.com");
        item = new Item(1L, "Item1", "Desc", true, user, null);
        itemDto = new ItemDto(1L, "itemName", "itemDesc", true, null, null);
    }

    @Test
    void addItem_userNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> itemService.addItem(1L, itemDto));
        assertEquals("Пользователь не найден", ex.getMessage());
    }

    @Test
    void updateItem_success() {
        itemDto.setName("NewName");
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);
        when(itemMapper.toItemDto(any())).thenReturn(itemDto);

        ItemDto result = itemService.updateItem(1L, 1L, itemDto);

        assertEquals("NewName", result.getName());
        verify(itemRepository).save(item);
    }

    @Test
    void updateItem_forbidden() {
        item.setOwner(new User(2L, "Other", "o@e.com")); // другой владелец
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ForbiddenException.class, () -> itemService.updateItem(1L, 1L, itemDto));
    }

    @Test
    void getItemById_asOwner() {
        item.setOwner(user);
        List<Comment> comments = List.of();
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemIdOrderByCreatedDesc(1L)).thenReturn(comments);
        when(bookingRepository.findLastBookingTime(any(), any())).thenReturn(null);
        when(bookingRepository.findNextBookingTime(any(), any())).thenReturn(null);

        ItemDtoOutput output = new ItemDtoOutput();
        when(itemMapper.toItemDtoOutput(any(), eq(comments), any(), any())).thenReturn(output);

        assertEquals(output, itemService.getItemById(1L, 1L));
    }

    @Test
    void searchItems_blankText_returnsEmptyList() {
        List<ItemDto> result = itemService.searchItems(" ");
        assertTrue(result.isEmpty());
        verify(itemRepository, never()).findAvailableByText(any());
    }

    @Test
    void addComment_success() {
        CommentDto input = new CommentDto(null, "Nice!", null, null);
        Comment saved = new Comment();
        saved.setId(10L);
        saved.setText("Nice!");
        saved.setAuthor(user);
        saved.setItem(item);
        saved.setCreated(LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsBookingFinished(eq(1L), eq(1L), eq(BookingStatus.APPROVED), any())).thenReturn(true);
        when(commentRepository.save(any())).thenReturn(saved);
        when(commentMapper.toDto(any())).thenReturn(new CommentDto(10L, "Nice!", "user", saved.getCreated()));

        CommentDto result = itemService.addComment(1L, 1L, input);

        assertEquals("Nice!", result.getText());
        assertEquals("user", result.getAuthorName());
    }

    @Test
    void addComment_noPastBooking_shouldThrow() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsBookingFinished(eq(1L), eq(1L), eq(BookingStatus.APPROVED), any())).thenReturn(false);

        assertThrows(ValidationException.class, () -> itemService.addComment(1L, 1L, new CommentDto(null, "Text", null, null)));
    }
}
