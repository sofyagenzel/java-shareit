package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Booking findFirstByItemAndBookerAndEndIsBefore(Item item, User user, LocalDateTime now);

    Booking findFirstByItemAndEndIsBeforeOrderByEndDesc(Item item, LocalDateTime now);

    Booking findFirstByItemAndStartIsAfterOrderByStartDesc(Item item, LocalDateTime now);

    List<Booking> findBookingsByBooker(User booker);

    List<Booking> findAllByBookerAndStartIsBeforeAndEndIsAfter(User booker, LocalDateTime dateTime, LocalDateTime dateTime1);

    List<Booking> findAllByBookerAndEndIsBeforeAndStatusIs(User booker, LocalDateTime dateTime, StatusBooking status);

    List<Booking> findAllByBookerAndStartIsAfter(User booker, LocalDateTime dateTime);

    List<Booking> findAllByBookerAndStatusIs(User booker, StatusBooking status);

    @Query(" select b from Booking b " +
            "where b.item.owner.id =:ownerId")
    List<Booking> findAllByOwnerOfItem(Long ownerId);

    @Query(" select b from Booking b " +
            "where (b.item.owner =:owner " +
            "and (:dateTime between b.start and b.end))")
    List<Booking> findAllByOwnerOfItemAndStartIsBeforeAndEndIsAfter(User owner, LocalDateTime dateTime);

    @Query(" select b from Booking b " +
            "where b.item.owner.id =:ownerId " +
            " and b.end<:dateTime " +
            "and b.status=:status")
    List<Booking> findAllByOwnerOfItemAndEndIsBeforeAndStatusIs(Long ownerId, LocalDateTime dateTime, StatusBooking status);

    @Query(" select b from Booking b " +
            "where b.item.owner.id =:ownerId " +
            "and b.start>:dateTime")
    List<Booking> findAllByOwnerOfItemAndStartIsAfter(Long ownerId, LocalDateTime dateTime);

    @Query(" select b from Booking b " +
            "where b.item.owner.id =:ownerId " +
            "and b.status = :status")
    List<Booking> findAllByOwnerOfItemAndStatusIs(Long ownerId, StatusBooking status);
}