package ru.practicum.shareit.item.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.DuplicatedDataException;
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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final CommentMapper commentMapper;
    private final ItemMapper itemMapper;

    @Transactional
    @Override
    public ItemDto addItem(Long userId, @Valid ItemDto itemDto) {
        log.info("Добавление вещи пользователем id={}, данные: {}", userId, itemDto);

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Пользователь с id={} не найден при добавлении вещи", userId);
                    return new NotFoundException("Пользователь не найден");
                });

        boolean exists = itemRepository.existsByOwnerIdAndNameIgnoreCase(owner.getId(), itemDto.getName());

        if (exists) {
            log.warn("Пользователь id={} пытался добавить вещь с дублирующим именем: {}", userId, itemDto.getName());
            throw new DuplicatedDataException("Вещь с таким именем уже существует");
        }


        Item item = itemMapper.toItem(itemDto, owner, null);

        Item saved = itemRepository.save(item);
        log.info("Вещь успешно добавлена с id={}", saved.getId());

        return itemMapper.toItemDto(saved);
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        log.info("Обновление вещи id={} пользователем id={}, данные: {}", itemId, userId, itemDto);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.warn("Вещь с id={} не найдена при попытке обновления пользователем id={}", itemId, userId);
                    return new NotFoundException("Вещь с id=" + itemId + " не найдена");
                });

        if (!item.getOwner().getId().equals(userId)) {
            log.warn("Пользователь id={} попытался обновить чужую вещь id={}", userId, itemId);
            throw new ForbiddenException("Пользователь не является владельцем вещи");
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        Item updated = itemRepository.save(item);
        log.info("Вещь id={} успешно обновлена", updated.getId());
        return itemMapper.toItemDto(updated);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDtoOutput getItemById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id: " + itemId + " не найдена."));

        List<Comment> comments = commentRepository.findByItemIdOrderByCreatedDesc(itemId);


        LocalDateTime lastBookingTime = null;
        LocalDateTime nextBookingTime = null;

        if (Objects.equals(item.getOwner().getId(), userId)) {
            LocalDateTime now = LocalDateTime.now();
            lastBookingTime = bookingRepository.findLastBookingTime(itemId, now);
            nextBookingTime = bookingRepository.findNextBookingTime(itemId, now);
        }


        return itemMapper.toItemDtoOutput(item, comments, lastBookingTime, nextBookingTime);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDtoOutput> getItemsByOwner(Long ownerId) {
        log.info("Получение всех вещей владельца с id={}", ownerId);

        if (!userRepository.existsById(ownerId)) {
            throw new NoSuchElementException("Пользователь не найден");
        }

        List<Item> items = itemRepository.findAllByOwnerId(ownerId);
        LocalDateTime now = LocalDateTime.now();

        return items.stream()
                .map(item -> {
                    List<Comment> comments = commentRepository.findByItemIdOrderByCreatedDesc(item.getId());
                    Optional<Booking> lastBooking = bookingRepository.findLastBookingEntity(item.getId(), now);
                    Optional<Booking> nextBooking = bookingRepository.findNextBookingEntity(item.getId(), now);

                    LocalDateTime lastBookingTime = lastBooking.map(Booking::getEnd).orElse(null);
                    LocalDateTime nextBookingTime = nextBooking.map(Booking::getStart).orElse(null);

                    return itemMapper.toItemDtoOutput(item, comments, lastBookingTime, nextBookingTime);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> searchItems(String text) {
        log.info("Поиск вещей по тексту запроса: '{}'", text);

        if (text == null || text.isBlank()) {
            log.info("Пустой текст поиска, возвращается пустой список");
            return Collections.emptyList();
        }

        List<Item> items = itemRepository.findAvailableByText(text);
        log.info("По запросу '{}' найдено {} доступных вещей", text, items.size());

        return items.stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDtoInput) {
        log.info("Добавление комментария пользователем id={} к вещи id={}, текст: {}", userId, itemId, commentDtoInput.getText());

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        boolean hasBooking = bookingRepository.existsBookingFinished(
                userId,
                itemId,
                BookingStatus.APPROVED,
                LocalDateTime.now()
        );

        if (!hasBooking) {
            throw new ValidationException("Можно оставлять отзывы только после завершённой аренды");
        }

        Comment comment = new Comment();
        comment.setText(commentDtoInput.getText());
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        Comment saved = commentRepository.save(comment);
        log.info("Комментарий успешно добавлен с id={}", saved.getId());

        return commentMapper.toDto(saved);
    }
}


