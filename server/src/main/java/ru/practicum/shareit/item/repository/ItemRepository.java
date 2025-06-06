package ru.practicum.shareit.item.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    boolean existsByOwnerIdAndNameIgnoreCase(Long ownerId, String name);

    List<Item> findAllByOwnerId(Long ownerId);

    @Query("SELECT i FROM Item i " +
            "WHERE i.available = true " +
            "AND (LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%')))")
    List<Item> findAvailableByText(@Param("text") String text);


    List<Item> findByRequestId(Long requestId);

    List<Item> findByRequestIdIn(List<Long> requestIds);
}
