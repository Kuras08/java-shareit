package ru.practicum.shareit.item.repository;


import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item save(Item item);

    Optional<Item> findById(Long id);

    List<Item> findAllByOwnerId(Long ownerId);

    List<Item> findAvailableByText(String text);

    Item update(Item item);

    boolean existsByOwnerIdAndNameIgnoreCase(Long ownerId, String name);

    void deleteById(Long id);
}

