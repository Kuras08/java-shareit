package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsByBookerIdAndItemIdAndStatusAndEndBefore(
            Long bookerId,
            Long itemId,
            BookingStatus status,
            LocalDateTime time
    );

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Booking b " +
            "WHERE b.booker.id = :bookerId AND b.item.id = :itemId AND b.status = :status AND b.end <= :now")
    boolean existsBookingFinished(@Param("bookerId") Long bookerId,
                                  @Param("itemId") Long itemId,
                                  @Param("status") BookingStatus status,
                                  @Param("now") LocalDateTime now);


    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId AND b.status = ru.practicum.shareit.booking.model.BookingStatus.APPROVED AND b.end < :now " +
            "ORDER BY b.end DESC")
    Optional<Booking> findLastBookingEntity(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId AND b.status = ru.practicum.shareit.booking.model.BookingStatus.APPROVED AND b.start > :now " +
            "ORDER BY b.start ASC")
    Optional<Booking> findNextBookingEntity(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    List<Booking> findByBooker_Id(Long bookerId, Sort sort);

    List<Booking> findByBooker_IdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    @Query("select b from Booking b where b.booker.id = ?1 and b.start <= ?2 and b.end >= ?2")
    List<Booking> findCurrentBookingsByBooker(Long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findByBooker_IdAndStartIsAfter(Long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findByItem_Owner_Id(Long ownerId, Sort sort);

    List<Booking> findByItem_Owner_IdAndStatus(Long ownerId, BookingStatus status, Sort sort);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start <= ?2 and b.end >= ?2")
    List<Booking> findCurrentBookingsByOwner(Long ownerId, LocalDateTime now, Sort sort);

    List<Booking> findByItem_Owner_IdAndEndIsBefore(Long ownerId, LocalDateTime now, Sort sort);

    List<Booking> findByItem_Owner_IdAndStartIsAfter(Long ownerId, LocalDateTime now, Sort sort);

    // Если тебе нужна только дата, можешь оставить и эти методы:
    @Query("SELECT MAX(b.end) FROM Booking b WHERE b.item.id = :itemId AND b.end < :now")
    LocalDateTime findLastBookingTime(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    @Query("SELECT MIN(b.start) FROM Booking b WHERE b.item.id = :itemId AND b.start > :now")
    LocalDateTime findNextBookingTime(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);
}


