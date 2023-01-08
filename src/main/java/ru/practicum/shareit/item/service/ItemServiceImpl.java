package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        Item newItem = new Item();
        ItemMapper.toItem(newItem, itemDto);
        var owner = userRepository.findById(userId);
        if (owner.isEmpty()) {
            throw new ObjectNotFoundException("Собственник не найден");
        }
        if (itemDto.getRequestId() != null) {
            var itemRequest = requestRepository.findById(itemDto.getRequestId());
            itemRequest.ifPresent(newItem::setRequest);
        }
        newItem.setOwner(owner.get());
        Item createdItem = itemRepository.save(newItem);
        return ItemMapper.toItemDto(createdItem);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        var item = itemRepository.findById(itemId);
        if (!Objects.equals(item.get().getOwner().getId(), userId)) {
            throw new ObjectNotFoundException("Пользователь не является собственником");
        }
        ItemMapper.toItem(item.get(), itemDto);
        item.get().setId(itemId);
        itemRepository.save(item.get());
        return ItemMapper.toItemDto(item.get());
    }

    @Override
    public ItemDto getItemById(Long id, Long userId) {
        var item = itemRepository.findById(id);
        if (item.isPresent()) {
            var itemDto = ItemMapper.toItemDto(item.get());
            if (item.get().getOwner().getId().equals(userId)) {
                addBookingsItem(itemDto);
            }
            addCommentsItem(itemDto);
            return itemDto;
        } else {
            throw new ObjectNotFoundException("Вещь не найдена:" + id);
        }
    }

    private ItemDto addBookingsItem(ItemDto dto) {
        Item item = new Item();
        ItemMapper.toItem(item, dto);
        var lastBookings = bookingRepository.findFirstByItemAndEndIsBeforeOrderByEndDesc(item, LocalDateTime.now());
        if (lastBookings != null) {
            dto.setLastBooking(BookingMapper.toBookingDto(lastBookings));
        }
        var nextBookings = bookingRepository.findFirstByItemAndStartIsAfterOrderByStartDesc(item, LocalDateTime.now());
        if (nextBookings != null) {
            dto.setNextBooking(BookingMapper.toBookingDto(nextBookings));
        }
        return dto;
    }

    @Override
    public List<ItemDto> getItemsByUserId(Long userId, Integer from, Integer size) {
        Pageable pageable;
        if (from == null || size == null) {
            pageable = Pageable.unpaged();
        } else {
            pageable = PageRequest.of(from, size);
        }
        var owner = userRepository.findById(userId);
        if (owner.isEmpty()) {
            throw new ObjectNotFoundException("Пользователь не найден:" + userId);
        }
        return itemRepository.findAllByOwnerId(userId, pageable)
                .stream()
                .map(ItemMapper::toItemDto)
                .map(this::addBookingsItem)
                .map(this::addCommentsItem)
                .sorted(Comparator.comparingLong(ItemDto::getId))
                .collect(Collectors.toList());
    }

    private ItemDto addCommentsItem(ItemDto dto) {
        Item item = new Item();
        ItemMapper.toItem(item, dto);
        var comments = commentRepository.findAllByItem(item);
        if (comments != null) {
            dto.setComments(comments.stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList()));
        } else {
            dto.setComments(new ArrayList<>());
        }
        return dto;
    }

    @Override
    public void removeItemById(Long id) {
        var item = itemRepository.findById(id);
        if (item.isEmpty()) {
            throw new ObjectNotFoundException("Вещь не найдена:" + id);
        }
        itemRepository.deleteById(id);
    }

    public List<ItemDto> searchItems(String text, Integer from, Integer size) {
        Pageable pageable;
        if (from == null || size == null) {
            pageable = Pageable.unpaged();
        } else {
            pageable = PageRequest.of(from / size, size);
        }
        if (StringUtils.isBlank(text)) {
            return new ArrayList<>();
        }
        return itemRepository.search(text, pageable)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, Long userId, Long itemId) {
        Comment newComment = new Comment();
        CommentMapper.toComment(newComment, commentDto);
        newComment.setCreated(LocalDateTime.now());
        var owner = userRepository.findById(userId);
        if (owner.isEmpty()) {
            throw new ObjectNotFoundException("Пользователь не найден:" + userId);
        } else {
            newComment.setAuthor(owner.get());
        }
        var item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new ObjectNotFoundException("Вещь не найдена:" + itemId);
        } else {
            newComment.setItem(item.get());
        }
        Booking booking = bookingRepository.findFirstByItemAndBookerAndEndIsBefore(item.get(), owner.get(), LocalDateTime.now());
        if (booking == null) {
            throw new BadRequestException("Вещь не была забронирована");
        }
        Comment createdComment = commentRepository.save(newComment);
        return CommentMapper.toCommentDto(createdComment);
    }
}