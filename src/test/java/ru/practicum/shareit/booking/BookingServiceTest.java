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
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.BadRequestException;
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
        booking1 = new Booking(2L, start, end, item, user, StatusBooking.WAITING);
        booking2 = new Booking(1L, start, null, item, user, StatusBooking.APPROVED);
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
        assertThrows(BadRequestException.class, () -> bookingService.createBooking(BookingMapper.toBookingDto(booking2), 1L));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ObjectNotFoundException.class, () -> bookingService.createBooking(bookingDto, 1L));
    }

    @Test
    public void updateBookingTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertThrows(IllegalStateException.class, () -> bookingService.updateBooking(1L, 2L, true));
        assertThrows(ObjectNotFoundException.class, () -> bookingService.updateBooking(1L, 5L, true));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        BookingDto bookingDb = bookingService.updateBooking(bookingDto.getId(), 2L, false);
        assertEquals(bookingDb.getId(), bookingDto.getId());
        assertEquals(bookingDb.getStart(), bookingDto.getStart());
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
        List<Booking> bookings = new ArrayList<>(Collections.singletonList(booking));
        Page<Booking> pagedResponse = new PageImpl(bookings);
        when(bookingRepository.findBookingsByBooker(any(User.class), any(Pageable.class))).thenReturn(pagedResponse);
        List<BookingDto> bookingDb = bookingService.getAllBookingsByState(1L, "ALL", 1, 1);
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
        when(bookingRepository.findAllByItemOwnerId(anyLong(), any(Pageable.class))).thenReturn(pagedResponse);
        List<BookingDto> bookingDb = bookingService.getAllBookingsByStateAndOwner(2L, "ALL", 1, 1);
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
        when(bookingRepository.findAllByItemOwnerId(anyLong(), any(Pageable.class))).thenReturn(pagedResponse);
        when(bookingRepository.findAllByItemOwnerIdAndStatusIs(anyLong(), any(StatusBooking.class), any(Pageable.class))).thenReturn(pagedResponse);
        BookingDto bookingDb = bookingService.stateToRepositoryAndOwner(user, State.ALL, pageable).get(0);
        BookingDto bookingDb1 = bookingService.stateToRepositoryAndOwner(user, State.REJECTED, pageable).get(0);
        assertEquals(booking.getId(), bookingDb.getId());
        assertEquals(booking.getItem(), bookingDb.getItem());
        assertNotNull(bookingDb1);
        when(bookingRepository.findAllByItemOwnerIdAndEndIsBeforeAndStatusIs(anyLong(), any(LocalDateTime.class), any(StatusBooking.class), any(Pageable.class))).thenReturn(pagedResponse);
        BookingDto bookingDb2 = bookingService.stateToRepositoryAndOwner(user, State.PAST, pageable).get(0);
        assertNotNull(bookingDb2);
        List<Booking> bookings1 = new ArrayList<>(Collections.singletonList(booking1));
        Page<Booking> pagedResponse1 = new PageImpl(bookings);
        when(bookingRepository.findAllByItemOwnerIdAndStatusIs(anyLong(), any(StatusBooking.class), any(Pageable.class))).thenReturn(pagedResponse1);
        BookingDto bookingDb3 = bookingService.stateToRepositoryAndOwner(user, State.WAITING, pageable).get(0);
        assertNotNull(bookingDb3);
        when(bookingRepository.findAllByItemOwnerAndStartIsBeforeAndEndIsAfter(any(User.class), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(pagedResponse1);
        BookingDto bookingDb4 = bookingService.stateToRepositoryAndOwner(user, State.CURRENT, pageable).get(0);
        assertNotNull(bookingDb4);
        when(bookingRepository.findAllByItemOwnerIdAndStartIsAfter(anyLong(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(pagedResponse1);
        BookingDto bookingDb5 = bookingService.stateToRepositoryAndOwner(user, State.FUTURE, pageable).get(0);
        assertNotNull(bookingDb5);
    }

    @Test
    public void stateToRepositoryTest() {
        Pageable pageable = PageRequest.of(1, 1);
        List<Booking> bookings = new ArrayList<>(Collections.singletonList(booking));
        Page<Booking> pagedResponse = new PageImpl(bookings);
        when(bookingRepository.findBookingsByBooker(any(User.class), any(Pageable.class))).thenReturn(pagedResponse);
        when(bookingRepository.findAllByBookerAndStatusIs(any(User.class), any(StatusBooking.class), any(Pageable.class))).thenReturn(pagedResponse);
        BookingDto bookingDb = bookingService.stateToRepository(user, State.ALL, pageable).get(0);
        BookingDto bookingDb1 = bookingService.stateToRepository(user, State.REJECTED, pageable).get(0);
        assertEquals(booking.getId(), bookingDb.getId());
        assertEquals(booking.getItem(), bookingDb.getItem());
        assertNotNull(bookingDb1);
        when(bookingRepository.findAllByBookerAndEndIsBeforeAndStatusIs(any(User.class), any(LocalDateTime.class), any(StatusBooking.class), any(Pageable.class))).thenReturn(pagedResponse);
        BookingDto bookingDb2 = bookingService.stateToRepository(user, State.PAST, pageable).get(0);
        assertNotNull(bookingDb2);
        List<Booking> bookings1 = new ArrayList<>(Collections.singletonList(booking1));
        Page<Booking> pagedResponse1 = new PageImpl(bookings);
        when(bookingRepository.findAllByBookerAndStatusIs(any(User.class), any(StatusBooking.class), any(Pageable.class))).thenReturn(pagedResponse1);
        BookingDto bookingDb3 = bookingService.stateToRepository(user, State.WAITING, pageable).get(0);
        assertNotNull(bookingDb3);
        when(bookingRepository.findAllByBookerAndStartIsBeforeAndEndIsAfter(any(User.class), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(pagedResponse1);
        BookingDto bookingDb4 = bookingService.stateToRepository(user, State.CURRENT, pageable).get(0);
        assertNotNull(bookingDb4);
        when(bookingRepository.findAllByBookerAndStartIsAfter(any(User.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(pagedResponse1);
        BookingDto bookingDb5 = bookingService.stateToRepository(user, State.FUTURE, pageable).get(0);
        assertNotNull(bookingDb5);
    }
}