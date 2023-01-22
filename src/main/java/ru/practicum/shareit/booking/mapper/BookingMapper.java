package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static BookingResponseDto toBookingResponseDto(Booking booking) {
        return new BookingResponseDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                booking.getItem(),
                booking.getBooker(),
                booking.getBooker().getId());
    }

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId());
    }

    public static void toBooking(Booking booking, BookingDto bookingDto) {
        booking.setId(bookingDto.getId());
        Optional.ofNullable(bookingDto.getStart()).ifPresent(booking::setStart);
        Optional.ofNullable(bookingDto.getEnd()).ifPresent(booking::setEnd);
        booking.setStatus(StatusBooking.WAITING);
    }
}