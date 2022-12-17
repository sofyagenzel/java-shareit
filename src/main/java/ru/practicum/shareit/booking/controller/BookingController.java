package ru.practicum.shareit.booking.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping()
    public BookingDto createBooking(@Valid @RequestBody BookingDto bookingDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@PathVariable Long bookingId,
                                    @RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestParam(name = "approved") Boolean approved) {
        return bookingService.updateBooking(bookingId, userId, approved);

    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long bookingId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping()
    public List<BookingDto> getAllBookingsByState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {
        return bookingService.getAllBookingsByState(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsByStateAndOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {
        return bookingService.getAllBookingsByStateAndOwner(userId, state);
    }

}