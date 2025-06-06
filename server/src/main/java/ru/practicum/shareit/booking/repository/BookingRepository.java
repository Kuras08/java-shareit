package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Все бронирования пользователя
    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId ORDER BY b.start DESC")
    List<Booking> findAllByBookerId(@Param("bookerId") Long bookerId);

    // Текущие бронирования пользователя
    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId AND b.start <= :now AND b.end >= :now " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentByBookerId(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now);

    // Прошедшие бронирования пользователя
    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId AND b.end < :now " +
            "ORDER BY b.start DESC")
    List<Booking> findPastByBookerId(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now);

    // Будущие бронирования пользователя
    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId AND b.start > :now " +
            "ORDER BY b.start DESC")
    List<Booking> findFutureByBookerId(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now);

    // Ожидающие подтверждения бронирования пользователя
    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId AND b.status = 'WAITING' " +
            "ORDER BY b.start DESC")
    List<Booking> findWaitingByBookerId(@Param("bookerId") Long bookerId);

    // Отклонённые бронирования пользователя
    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId AND b.status = 'REJECTED' " +
            "ORDER BY b.start DESC")
    List<Booking> findRejectedByBookerId(@Param("bookerId") Long bookerId);

    // Все бронирования владельца
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByOwnerId(@Param("ownerId") Long ownerId);

    // Текущие бронирования владельца
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId AND b.start <= :now AND b.end >= :now " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentByOwnerId(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

    // Прошедшие бронирования владельца
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId AND b.end < :now " +
            "ORDER BY b.start DESC")
    List<Booking> findPastByOwnerId(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

    // Будущие бронирования владельца
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId AND b.start > :now " +
            "ORDER BY b.start DESC")
    List<Booking> findFutureByOwnerId(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

    // Ожидающие подтверждения бронирования владельца
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId AND b.status = 'WAITING' " +
            "ORDER BY b.start DESC")
    List<Booking> findWaitingByOwnerId(@Param("ownerId") Long ownerId);

    // Отклонённые бронирования владельца
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId AND b.status = 'REJECTED' " +
            "ORDER BY b.start DESC")
    List<Booking> findRejectedByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.start < :now AND b.status = 'APPROVED' ORDER BY b.start DESC")
    Optional<Booking> findLastBookingEntity(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.start > :now AND b.status = 'APPROVED' ORDER BY b.start ASC")
    Optional<Booking> findNextBookingEntity(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    @Query("SELECT MAX(b.end) FROM Booking b WHERE b.item.id = :itemId AND b.start < :now AND b.status = 'APPROVED'")
    LocalDateTime findLastBookingTime(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    @Query("SELECT MIN(b.start) FROM Booking b WHERE b.item.id = :itemId AND b.start > :now AND b.status = 'APPROVED'")
    LocalDateTime findNextBookingTime(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.booker.id = :userId AND b.item.id = :itemId " +
            "AND b.status = :status AND b.end < :now")
    boolean existsBookingFinished(@Param("userId") Long userId,
                                  @Param("itemId") Long itemId,
                                  @Param("status") BookingStatus status,
                                  @Param("now") LocalDateTime now);
}



