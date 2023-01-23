package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.item.dto.ItemResponseDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static BookingResponseDto toBookingResponseDto(Booking booking) {
        if (booking != null) {
            BookingResponseDto.Booker booker = new BookingResponseDto.Booker(booking.getBooker().getId(), booking.getBooker().getName());
            BookingResponseDto.Item item = new BookingResponseDto.Item(booking.getItem().getId(), booking.getItem().getName(), booking.getItem().getOwner().getId());
            return new BookingResponseDto(booking.getId(),
                    booking.getStart(),
                    booking.getEnd(),
                    booking.getStatus(),
                    booker,
                    item);
        } else {
            return null;
        }
    }

    public static ItemResponseDto.BookingDto toBookingDto(Booking booking) {
        return new ItemResponseDto.BookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getBooker().getId());
    }

    public static void toBooking(Booking booking, BookingDto bookingDto) {
        booking.setId(bookingDto.getId());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setStatus(StatusBooking.WAITING);
    }
}