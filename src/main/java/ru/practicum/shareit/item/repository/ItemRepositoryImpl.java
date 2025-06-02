package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> storage = new HashMap<>();
    private long currentId = 0;

    @Override
    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(generateId());
        }
        storage.put(item.getId(), item);
        return item;
    }

    private long generateId() {
        return ++currentId;
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Item> findAllByOwnerId(Long ownerId) {
        return storage.values().stream()
                .filter(item -> item.getOwner() != null && ownerId.equals(item.getOwner().getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findAvailableByText(String text) {
        String lowerText = text.toLowerCase();
        return storage.values().stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .filter(item -> item.getName().toLowerCase().contains(lowerText)
                        || item.getDescription().toLowerCase().contains(lowerText))
                .collect(Collectors.toList());
    }

    @Override
    public Item update(Item item) {
        if (item.getId() == null || !storage.containsKey(item.getId())) {
            throw new NoSuchElementException("Item with id " + item.getId() + " not found");
        }
        storage.put(item.getId(), item);
        return item;
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }
}

