package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;
    Pageable pageable;

    @Transactional
    @Override
    public ItemResponseDto createItem(ItemDto itemDto, Long userId) {
        Item newItem = new Item();
        ItemMapper.toItem(newItem, itemDto);
        newItem.setOwner(userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Собственник не найден")));
        if (itemDto.getRequestId() != null) {
            var itemRequest = requestRepository.findById(itemDto.getRequestId());
            itemRequest.ifPresent(newItem::setRequest);
        }
        Item createdItem = itemRepository.save(newItem);
        return ItemMapper.toItemResponseDto(createdItem);
    }

    @Transactional
    @Override
    public ItemResponseDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("Вещь не найдена"));
        if (!userId.equals(item.getOwner().getId())) {
            throw new ObjectNotFoundException("Собственник не найден");
        }
        ItemMapper.toItem(item, itemDto);
        item.setId(itemId);
        return ItemMapper.toItemResponseDto(item);
    }

    @Override
    public ItemResponseDto getItemById(Long id, Long userId) {
        Item itemDb = itemRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Вещь не найдена", id)));
        return addBookingsCommentsItem(itemDb, userId);
    }

    @Override
    public List<ItemResponseDto> getItemsByUserId(Long userId, Integer from, Integer size) {
        pageable = PageRequest.of(from, size);
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Пользователь не найден" + userId)));
        List<Item> itemsDb = itemRepository.findAllByOwnerId(userId, pageable)
                .stream().filter(item -> item.getRequest() == null).collect(Collectors.toList());
        List<ItemResponseDto> itemResponseDtoList = new ArrayList<>();
        for (Item item : itemsDb) {
            ItemResponseDto itemResponseDto = ItemMapper.toItemResponseDto(item);
            itemResponseDto = addBookingsCommentsItem(item, userId);
            itemResponseDtoList.add(itemResponseDto);
        }
        return itemResponseDtoList;
    }

    @Transactional
    @Override
    public void removeItemById(Long id) {
        itemRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Вещь не найдена:" + id));
        itemRepository.deleteById(id);
    }

    public List<ItemResponseDto> searchItems(String text, Integer from, Integer size) {
        pageable = PageRequest.of(from / size, size);
        if (StringUtils.isBlank(text)) {
            return new ArrayList<>();
        }
        return itemRepository.search(text, pageable)
                .stream()
                .map(ItemMapper::toItemResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentResponseDto createComment(CommentDto commentDto, Long userId, Long itemId) {
        Comment newComment = new Comment();
        CommentMapper.toComment(newComment, commentDto);
        newComment.setAuthor(userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Пользователь не найден" + userId))));
        newComment.setItem(itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Вещь не найдена:" + itemId))));
        Booking booking = bookingRepository.findFirstByItemAndBookerAndEndIsBefore(newComment.getItem(), newComment.getAuthor(), LocalDateTime.now());
        if (booking == null) {
            throw new BadRequestException("Вещь не была забронирована");
        }
        Comment createdComment = commentRepository.save(newComment);
        return CommentMapper.toCommentResponseDto(createdComment);
    }

    private ItemResponseDto addBookingsCommentsItem(Item itemDto, Long userId) {
        ItemResponseDto itemResponseDto = ItemMapper.toItemResponseDto(itemDto);
        var lastBookings = bookingRepository.findFirstByItemAndEndLessThanOrderByEndDesc(itemDto, LocalDateTime.now());
        if (lastBookings != null && userId.equals(itemDto.getOwner().getId())) {
            itemResponseDto.setLastBooking(BookingMapper.toBookingResponseDto(lastBookings));
        }
        var nextBookings = bookingRepository.findFirstByItemAndStartIsAfterOrderByStart(itemDto, LocalDateTime.now());
        if (nextBookings != null && userId.equals(itemDto.getOwner().getId())) {
            itemResponseDto.setNextBooking(BookingMapper.toBookingResponseDto(nextBookings));
        }
        List<CommentResponseDto> comments = commentRepository.findAllByItem(itemDto).stream()
                .map(CommentMapper::toCommentResponseDto)
                .collect(Collectors.toList());
        itemResponseDto.setComments(comments);

        return itemResponseDto;
    }
}