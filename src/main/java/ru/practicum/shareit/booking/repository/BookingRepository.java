package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Booking findFirstByItemAndBookerAndEndIsBefore(Item item, User user, LocalDateTime now);

    List<Booking> findAllByItemInAndStartLessThanOrderByEndDesc(
            List<Item> items, LocalDateTime now);

    List<Booking> findAllByItemInAndStartAfterOrderByStart(
            List<Item> items, LocalDateTime now);

    Booking findFirstByItemAndEndLessThanOrderByEndDesc(Item item, LocalDateTime now);

    Booking findFirstByItemAndStartIsAfterOrderByStart(Item item, LocalDateTime now);

    Page<Booking> findBookingsByBookerOrderByStartDesc(User booker, Pageable pageable);

    Page<Booking> findAllByBookerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(User booker, LocalDateTime dateTime, LocalDateTime dateTime1, Pageable pageable);

    Page<Booking> findAllByBookerAndEndIsBeforeAndStatusIsOrderByStartDesc(User booker, LocalDateTime dateTime, StatusBooking status, Pageable pageable);

    Page<Booking> findAllByBookerAndStartIsAfterOrderByStartDesc(User booker, LocalDateTime dateTime, Pageable pageable);

    Page<Booking> findAllByBookerAndStatusIsOrderByStartDesc(User booker, StatusBooking status, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId, Pageable pageable);

    Page<Booking> findAllByItemOwnerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(User owner, LocalDateTime dateTime, LocalDateTime dateTime1, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndEndIsBeforeAndStatusIsOrderByStartDesc(Long ownerId, LocalDateTime dateTime, StatusBooking status, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(Long ownerId, LocalDateTime dateTime, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStatusIsOrderByStartDesc(Long ownerId, StatusBooking status, Pageable pageable);
}