package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null);
    }

    public static ItemResponseDto toItemResponseDto(Item item) {
        return new ItemResponseDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                new ArrayList<>(),
                item.getRequest() != null ? item.getRequest().getId() : null);
    }

    public static void toItem(Item item, ItemDto itemDto) {
        item.setId(itemDto.getId());
        Optional.ofNullable(itemDto.getName()).ifPresent(item::setName);
        Optional.ofNullable(itemDto.getAvailable()).ifPresent(item::setAvailable);
        Optional.ofNullable(itemDto.getDescription()).ifPresent(item::setDescription);
    }

    public static ItemResponseDto toMap(Item item, List<CommentResponseDto> comments, Booking lastBooking, Booking nextBooking) {
        if (lastBooking != null && nextBooking != null) {
            return new ItemResponseDto(
                    item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.getAvailable(),
                    BookingMapper.toBookingResponseDto(lastBooking),
                    BookingMapper.toBookingResponseDto(nextBooking),
                    comments,
                    item.getRequest() != null ? item.getRequest().getId() : null);
        } else {
            return new ItemResponseDto(
                    item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.getAvailable(),
                    null,
                    null,
                    comments,
                    item.getRequest() != null ? item.getRequest().getId() : null);

        }
    }
}