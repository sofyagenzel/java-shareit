package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

    BookingServiceImpl bookingService;
    ItemDto itemDto;
    Item item;
    User user;
    User user1;
    Booking booking;
    Booking booking1;
    Booking booking2;
    BookingDto bookingDto;
    BookingResponseDto bookingResponseDto;

    @BeforeEach
    void beforeEach() {
        bookingService = new BookingServiceImpl(
                itemRepository,
                userRepository,
                bookingRepository);
        user = new User(1L, "User1", "iuser1@mail.ru");
        user1 = new User(2L, "User1", "iuser1@mail.ru");
        item = new Item(1L, "Item", "Item description", true, user1, new ItemRequest());
        itemDto = new ItemDto(1L, "Item", "Item description", true, 1L);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        bookingDto = new BookingDto(1L, start, end, 1L);
        booking = new Booking(1L, start, end, item, user, StatusBooking.APPROVED);
        booking1 = new Booking(2L, start, end, item, user, StatusBooking.WAITING);
    }

    @Test
    public void createBookingTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);
        BookingResponseDto bookingDb = bookingService.createBooking(bookingDto, 1L);
        assertEquals(bookingDb.getId(), bookingDto.getId());
        assertEquals(bookingDb.getStart(), bookingDto.getStart());
        assertEquals(bookingDb.getEnd(), bookingDto.getEnd());
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ObjectNotFoundException.class, () -> bookingService.createBooking(bookingDto, 1L));
    }

    @Test
    public void updateBookingTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertThrows(IllegalStateException.class, () -> bookingService.updateBooking(1L, 2L, true));
        assertThrows(ObjectNotFoundException.class, () -> bookingService.updateBooking(1L, 5L, true));
        BookingResponseDto bookingDb = bookingService.updateBooking(bookingDto.getId(), 2L, false);
        assertEquals(bookingDb.getId(), bookingDto.getId());
        assertEquals(bookingDb.getStart(), bookingDto.getStart());
    }

    @Test
    public void getBookingTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        bookingResponseDto = bookingService.getBooking(1L, 1L);
        assertEquals(booking.getId(), bookingResponseDto.getId());
        assertEquals(booking.getItem().getId(), bookingResponseDto.getItem().getId());
        assertEquals(booking.getItem().getName(), bookingResponseDto.getItem().getName());
        assertEquals(booking.getStart(), bookingResponseDto.getStart());
        assertThrows(ObjectNotFoundException.class, () -> bookingService.getBooking(2L, 7L));
    }

    @Test
    public void getAllBookingsByStateTest() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        List<Booking> bookings = new ArrayList<>(Collections.singletonList(booking));
        Page<Booking> pagedResponse = new PageImpl(bookings);
        when(bookingRepository.findBookingsByBookerOrderByStartDesc(any(User.class), any(Pageable.class))).thenReturn(pagedResponse);
        List<BookingResponseDto> bookingDb = bookingService.getAllBookingsByState(1L, "ALL", 1, 1);
        assertEquals(1, bookingDb.size());
        assertThrows(IllegalStateException.class, () -> bookingService.getAllBookingsByState(1L, "FAIL", 1, 1));
        final IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class,
                () -> bookingService.getAllBookingsByState(1L, "FAIL", 1, 1));
        Assertions.assertEquals("Unknown state: FAIL", exception.getMessage());
    }

    @Test
    public void getAllBookingsByStateAndOwnerTest() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        List<Booking> bookings = new ArrayList<>(Collections.singletonList(booking));
        Page<Booking> pagedResponse = new PageImpl(bookings);
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(anyLong(), any(Pageable.class))).thenReturn(pagedResponse);
        List<BookingResponseDto> bookingDb = bookingService.getAllBookingsByStateAndOwner(2L, "ALL", 1, 1);
        System.out.println(bookings);
        assertEquals(1, bookingDb.size());
        assertThrows(IllegalStateException.class, () -> bookingService.getAllBookingsByStateAndOwner(1L, "FAIL", 1, 1));
        final IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class,
                () -> bookingService.getAllBookingsByStateAndOwner(1L, "FAIL", 1, 1));
        Assertions.assertEquals("Unknown state: FAIL", exception.getMessage());
    }

    @Test
    public void stateToRepositoryAndOwnerTest() {
        Pageable pageable = PageRequest.of(1, 1);
        List<Booking> bookings = new ArrayList<>(Collections.singletonList(booking));
        Page<Booking> pagedResponse = new PageImpl(bookings);
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(anyLong(), any(Pageable.class))).thenReturn(pagedResponse);
        when(bookingRepository.findAllByItemOwnerIdAndStatusIsOrderByStartDesc(anyLong(), any(StatusBooking.class), any(Pageable.class))).thenReturn(pagedResponse);
        BookingResponseDto bookingDb = bookingService.stateToRepositoryAndOwner(user, State.ALL, pageable).get(0);
        BookingResponseDto bookingDb1 = bookingService.stateToRepositoryAndOwner(user, State.REJECTED, pageable).get(0);
        assertEquals(booking.getId(), bookingDb.getId());
        assertEquals(booking.getItem().getId(), bookingDb.getItem().getId());
        assertEquals(booking.getItem().getName(), bookingDb.getItem().getName());
        assertNotNull(bookingDb1);
        when(bookingRepository.findAllByItemOwnerIdAndEndIsBeforeAndStatusIsOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(StatusBooking.class), any(Pageable.class))).thenReturn(pagedResponse);
        BookingResponseDto bookingDb2 = bookingService.stateToRepositoryAndOwner(user, State.PAST, pageable).get(0);
        assertNotNull(bookingDb2);
        List<Booking> bookings1 = new ArrayList<>(Collections.singletonList(booking1));
        Page<Booking> pagedResponse1 = new PageImpl(bookings);
        when(bookingRepository.findAllByItemOwnerIdAndStatusIsOrderByStartDesc(anyLong(), any(StatusBooking.class), any(Pageable.class))).thenReturn(pagedResponse1);
        BookingResponseDto bookingDb3 = bookingService.stateToRepositoryAndOwner(user, State.WAITING, pageable).get(0);
        assertNotNull(bookingDb3);
        when(bookingRepository.findAllByItemOwnerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(any(User.class), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(pagedResponse1);
        BookingResponseDto bookingDb4 = bookingService.stateToRepositoryAndOwner(user, State.CURRENT, pageable).get(0);
        assertNotNull(bookingDb4);
        when(bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(pagedResponse1);
        BookingResponseDto bookingDb5 = bookingService.stateToRepositoryAndOwner(user, State.FUTURE, pageable).get(0);
        assertNotNull(bookingDb5);
    }

    @Test
    public void stateToRepositoryTest() {
        Pageable pageable = PageRequest.of(1, 1);
        List<Booking> bookings = new ArrayList<>(Collections.singletonList(booking));
        Page<Booking> pagedResponse = new PageImpl(bookings);
        when(bookingRepository.findBookingsByBookerOrderByStartDesc(any(User.class), any(Pageable.class))).thenReturn(pagedResponse);
        when(bookingRepository.findAllByBookerAndStatusIsOrderByStartDesc(any(User.class), any(StatusBooking.class), any(Pageable.class))).thenReturn(pagedResponse);
        BookingResponseDto bookingDb = bookingService.stateToRepository(user, State.ALL, pageable).get(0);
        BookingResponseDto bookingDb1 = bookingService.stateToRepository(user, State.REJECTED, pageable).get(0);
        assertEquals(booking.getId(), bookingDb.getId());
        assertEquals(booking.getItem().getId(), bookingDb.getItem().getId());
        assertEquals(booking.getItem().getName(), bookingDb.getItem().getName());
        assertNotNull(bookingDb1);
        when(bookingRepository.findAllByBookerAndEndIsBeforeAndStatusIsOrderByStartDesc(any(User.class), any(LocalDateTime.class), any(StatusBooking.class), any(Pageable.class))).thenReturn(pagedResponse);
        BookingResponseDto bookingDb2 = bookingService.stateToRepository(user, State.PAST, pageable).get(0);
        assertNotNull(bookingDb2);
        List<Booking> bookings1 = new ArrayList<>(Collections.singletonList(booking1));
        Page<Booking> pagedResponse1 = new PageImpl(bookings);
        when(bookingRepository.findAllByBookerAndStatusIsOrderByStartDesc(any(User.class), any(StatusBooking.class), any(Pageable.class))).thenReturn(pagedResponse1);
        BookingResponseDto bookingDb3 = bookingService.stateToRepository(user, State.WAITING, pageable).get(0);
        assertNotNull(bookingDb3);
        when(bookingRepository.findAllByBookerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(any(User.class), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(pagedResponse1);
        BookingResponseDto bookingDb4 = bookingService.stateToRepository(user, State.CURRENT, pageable).get(0);
        assertNotNull(bookingDb4);
        when(bookingRepository.findAllByBookerAndStartIsAfterOrderByStartDesc(any(User.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(pagedResponse1);
        BookingResponseDto bookingDb5 = bookingService.stateToRepository(user, State.FUTURE, pageable).get(0);
        assertNotNull(bookingDb5);
    }
}