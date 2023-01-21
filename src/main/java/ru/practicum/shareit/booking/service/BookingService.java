package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(BookingDto bookingDto, Long userId);

    BookingResponseDto getBooking(Long id, Long userId);

    BookingResponseDto updateBooking(Long bookingId, Long userId, Boolean approved);

    List<BookingResponseDto> getAllBookingsByState(Long userId, String state, Integer from, Integer size);

    List<BookingResponseDto> getAllBookingsByStateAndOwner(Long userId, String stringState, Integer from, Integer size);
}