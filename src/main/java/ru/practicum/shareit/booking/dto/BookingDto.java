package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Builder
@Data
public class BookingDto {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    StatusBooking status;
    Long itemId;
    Long bookerId;
    User booker;
    Item item;
}