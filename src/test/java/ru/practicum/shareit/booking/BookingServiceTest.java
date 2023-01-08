package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.IllegalStateException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    BookingRepository bookingRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;

    BookingService bookingService;
    ItemDto itemDto;
    Item item;
    User user;
    User user1;
    Booking booking;
    BookingDto bookingDto;

    @BeforeEach
    void beforeEach() {
        bookingService = new BookingServiceImpl(
                itemRepository,
                userRepository,
                bookingRepository);
        user = new User(1L, "User1", "iuser1@mail.ru");
        user1 = new User(2L, "User1", "iuser1@mail.ru");
        item = new Item(1L, "Item", "Item description", true, user1, new ItemRequest());
        itemDto = new ItemDto(null, null, null, 1L, "Item",
                "Item description", true, 1L);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        bookingDto = new BookingDto(1L, start, end, StatusBooking.APPROVED, 1L, 2L, user, item);
        booking = new Booking(1L, start, end, item, user, StatusBooking.APPROVED);
    }

    @Test
    public void createBookingTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto bookingDb = bookingService.createBooking(bookingDto, 1L);
        assertEquals(bookingDb.getId(), bookingDto.getId());
        assertEquals(bookingDb.getStart(), bookingDto.getStart());
        assertEquals(bookingDb.getEnd(), bookingDto.getEnd());
    }

    @Test
    public void updateBookingTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertThrows(IllegalStateException.class, () -> bookingService.updateBooking(1L, 2L, true));
    }

    @Test
    public void getBookingTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        bookingDto = bookingService.getBooking(1L, 1L);
        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getItem(), bookingDto.getItem());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertThrows(ObjectNotFoundException.class, () -> bookingService.getBooking(2L, 7L));
    }

    @Test
    public void getAllBookingsByStateTest() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        assertThrows(IllegalStateException.class, () -> bookingService.getAllBookingsByState(1L, "FAIL", 1, 1));
    }

    @Test
    public void getAllBookingsByStateAndOwnerTest() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        assertThrows(ObjectNotFoundException.class, () -> bookingService.getAllBookingsByStateAndOwner(3L, "WAITING", 1, 1));
    }
}