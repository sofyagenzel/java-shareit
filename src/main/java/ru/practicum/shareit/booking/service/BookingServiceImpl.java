package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.IllegalStateException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    Pageable pageable;

    public static Optional<State> checkState(String stateRequest) {
        for (State state : State.values()) {
            if (stateRequest.equals(state.toString())) {
                return Optional.of(State.valueOf(stateRequest));
            }
        }
        return Optional.empty();
    }

    @Transactional
    @Override
    public BookingResponseDto createBooking(BookingDto bookingDto, Long userId) {
        Booking newBooking = new Booking();
        BookingMapper.toBooking(newBooking, bookingDto);
        if (bookingDto.getEnd() == null || bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new BadRequestException("Не корректная дата окончания бронирования");
        }
        newBooking.setBooker(userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден:" + userId)));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ObjectNotFoundException("Вещь не найдена:" + bookingDto.getItemId()));
        if (item.getAvailable()) {
            if (!newBooking.getBooker().equals(item.getOwner())) {
                newBooking.setItem(item);
            } else {
                throw new ObjectNotFoundException("Вещь другого собственника:" + item.getOwner().getId());
            }
        } else {
            throw new BadRequestException("Вещь не доступна для бронирования:" + item.getId());
        }
        Booking createdBooking = bookingRepository.save(newBooking);
        return BookingMapper.toBookingResponseDto(createdBooking);
    }

    @Override
    public BookingResponseDto getBooking(Long id, Long userId) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Такого бронирования нет"));
        if (!Objects.equals(booking.getBooker().getId(), userId) &&
                !Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new ObjectNotFoundException("У вещи другой собственник:" + userId);
        }
        return BookingMapper.toBookingResponseDto(booking);
    }

    @Transactional
    @Override
    public BookingResponseDto updateBooking(Long bookingId, Long userId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден: " + userId));
        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new ObjectNotFoundException("У вещи другой собственник:" + userId);
        }
        if ((booking.getStatus() == StatusBooking.APPROVED && approved)
                || (booking.getStatus() == StatusBooking.REJECTED && !approved)) {
            throw new IllegalStateException("Не корректный статус:" + booking.getStatus());
        }
        if (approved) {
            booking.setStatus(StatusBooking.APPROVED);
        } else {
            booking.setStatus(StatusBooking.REJECTED);
        }
        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getAllBookingsByState(Long userId, String stringState, Integer from, Integer size) {
        pageable = PageRequest.of(from / size, size, Sort.by("start").descending());
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден: " + userId));
        State state = checkState(stringState).orElseThrow(() -> new IllegalStateException("Unknown state: " + stringState));
        return stateToRepository(booker, state, pageable)
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getAllBookingsByStateAndOwner(Long userId, String stringState, Integer from, Integer size) {
        pageable = PageRequest.of(from / size, size, Sort.by("start").descending());
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден: " + userId));
        State state = checkState(stringState).orElseThrow(() -> new IllegalStateException("Unknown state: " + stringState));
        return stateToRepositoryAndOwner(booker, state, pageable)
                .stream()
                .filter(b -> Objects.equals(b.getItem().getOwner().getId(), userId))
                .collect(Collectors.toList());
    }

    public List<BookingResponseDto> stateToRepositoryAndOwner(User owner, State state, Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> resultList = new ArrayList<>();
        Page<Booking> result = new PageImpl<>(resultList);
        switch (state) {
            case ALL:
                result = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(owner.getId(), pageable);
                break;
            case CURRENT:
                result = bookingRepository.findAllByItemOwnerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(owner, now, now, pageable);
                break;
            case PAST:
                result = bookingRepository.findAllByItemOwnerIdAndEndIsBeforeAndStatusIsOrderByStartDesc(owner.getId(), now, StatusBooking.APPROVED, pageable);
                break;
            case FUTURE:
                result = bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(owner.getId(), now, pageable);
                break;
            case WAITING:
                result = bookingRepository.findAllByItemOwnerIdAndStatusIsOrderByStartDesc(owner.getId(), StatusBooking.WAITING, pageable);
                break;
            case REJECTED:
                result = bookingRepository.findAllByItemOwnerIdAndStatusIsOrderByStartDesc(owner.getId(), StatusBooking.REJECTED, pageable);
                break;
        }
        return result.stream()
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    public List<BookingResponseDto> stateToRepository(User owner, State state, Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> resultList = new ArrayList<>();
        Page<Booking> result = new PageImpl<>(resultList);
        switch (state) {
            case ALL:
                result = bookingRepository.findBookingsByBookerOrderByStartDesc(owner, pageable);
                break;
            case CURRENT:
                result = bookingRepository.findAllByBookerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(owner, now, now, pageable);
                break;
            case PAST:
                result = bookingRepository.findAllByBookerAndEndIsBeforeAndStatusIsOrderByStartDesc(owner, now, StatusBooking.APPROVED, pageable);
                break;
            case FUTURE:
                result = bookingRepository.findAllByBookerAndStartIsAfterOrderByStartDesc(owner, now, pageable);
                break;
            case WAITING:
                result = bookingRepository.findAllByBookerAndStatusIsOrderByStartDesc(owner, StatusBooking.WAITING, pageable);
                break;
            case REJECTED:
                result = bookingRepository.findAllByBookerAndStatusIsOrderByStartDesc(owner, StatusBooking.REJECTED, pageable);
                break;
        }
        return result.stream()
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }
}