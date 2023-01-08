package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Booking findFirstByItemAndBookerAndEndIsBefore(Item item, User user, LocalDateTime now);

    Booking findFirstByItemAndEndIsBeforeOrderByEndDesc(Item item, LocalDateTime now);

    Booking findFirstByItemAndStartIsAfterOrderByStartDesc(Item item, LocalDateTime now);

    Page<Booking> findBookingsByBooker(User booker, Pageable pageable);

    Page<Booking> findAllByBookerAndStartIsBeforeAndEndIsAfter(User booker, LocalDateTime dateTime, LocalDateTime dateTime1, Pageable pageable);

    Page<Booking> findAllByBookerAndEndIsBeforeAndStatusIs(User booker, LocalDateTime dateTime, StatusBooking status, Pageable pageable);

    Page<Booking> findAllByBookerAndStartIsAfter(User booker, LocalDateTime dateTime, Pageable pageable);

    Page<Booking> findAllByBookerAndStatusIs(User booker, StatusBooking status, Pageable pageable);

    Page<Booking> findAllByItemOwnerId(Long ownerId, Pageable pageable);

    Page<Booking> findAllByItemOwnerAndStartIsBeforeAndEndIsAfter(User owner, LocalDateTime dateTime, LocalDateTime dateTime1, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndEndIsBeforeAndStatusIs(Long ownerId, LocalDateTime dateTime, StatusBooking status, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStartIsAfter(Long ownerId, LocalDateTime dateTime, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStatusIs(Long ownerId, StatusBooking status, Pageable pageable);
}