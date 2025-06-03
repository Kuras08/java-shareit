package ru.practicum.shareit.item.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepositoryImpl itemRepository;
    private final UserRepositoryImpl userRepository;

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

        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(owner);

        Item saved = itemRepository.save(item);
        log.info("Вещь успешно добавлена с id={}", saved.getId());
        return ItemMapper.toItemDto(saved);
    }

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

        Item updated = itemRepository.update(item);
        log.info("Вещь id={} успешно обновлена", updated.getId());
        return ItemMapper.toItemDto(updated);
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        log.info("Получение вещи по id={}, запрашивает пользователь id={}", itemId, userId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.warn("Вещь с id={} не найдена при получении пользователем id={}", itemId, userId);
                    return new NotFoundException("Вещь не найдена");
                });

        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long ownerId) {
        log.info("Получение всех вещей владельца с id={}", ownerId);

        if (!userRepository.existsById(ownerId)) {
            log.warn("Попытка получить вещи несуществующего пользователя id={}", ownerId);
            throw new NoSuchElementException("Пользователь не найден");
        }

        List<Item> items = itemRepository.findAllByOwnerId(ownerId);
        log.info("Найдено {} вещей у пользователя id={}", items.size(), ownerId);

        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

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
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
