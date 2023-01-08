package ru.practicum.shareit.item;

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
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private RequestRepository requestRepository;
    private ItemServiceImpl itemService;
    private ItemDto itemDto;
    private Item item;
    private User user;
    private Booking booking;
    private ItemRequest itemRequest;
    private Comment comment;
    private CommentDto commentDto;

    @BeforeEach
    void beforeEach() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository, requestRepository);
        user = new User(1L, "user", "user@email.ru");
        item = new Item(1L, "item", "item test", true, user, new ItemRequest());
        comment = new Comment(1L, "Comment", item, user, LocalDateTime.now());
        commentDto = new CommentDto(1L, "Comment", LocalDateTime.now(), "user");
        itemDto = new ItemDto(null, null, null, 1L, "item", "item test", true, 1L);
        booking = new Booking(2L, LocalDateTime.now(), LocalDateTime.now().minusDays(1), item, user, StatusBooking.APPROVED);
        itemRequest = new ItemRequest(1L, "description", user, LocalDateTime.now(), null);
    }

    @Test
    void createItemTest() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(requestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any())).thenReturn(item);
        ItemDto itemDb = itemService.createItem(itemDto, 1L);
        assertEquals(itemDb.getId(), itemDto.getId());
        assertEquals(itemDb.getName(), itemDto.getName());
        assertEquals(itemDb.getDescription(), itemDto.getDescription());
        assertThrows(ObjectNotFoundException.class, () -> itemService.createItem(itemDto, 2L));
    }

    @Test
    void updateItemTest() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);
        ItemDto itemDb = itemService.updateItem(itemDto, 1L, 1L);
        assertEquals(item.getId(), itemDb.getId());
        assertEquals(item.getName(), itemDb.getName());
        assertEquals(item.getDescription(), itemDb.getDescription());
        assertThrows(ObjectNotFoundException.class, () -> itemService.updateItem(itemDto, 1L, 2L));
    }

    @Test
    void getItemByIdTest() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        ItemDto itemResponseDto = itemService.getItemById(1L, 1L);
        assertEquals(item.getId(), itemResponseDto.getId());
        assertEquals(item.getName(), itemResponseDto.getName());
        assertEquals(item.getDescription(), itemResponseDto.getDescription());
        assertThrows(ObjectNotFoundException.class, () -> itemService.getItemById(2L, 2L));
    }

    @Test
    void getItemsByUserIdTest() {
        Pageable pageable = PageRequest.of(1, 1);
        Item item = new Item(1L, "item", "item test", true, user, null);
        List<Item> items = new ArrayList<>(Collections.singletonList(item));
        Page<Item> pagedResponse = new PageImpl(items);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(1L, pageable)).thenReturn(pagedResponse);
        final List<ItemDto> itemDtoList = itemService.getItemsByUserId(1L, 1, 1);
        assertNotNull(itemDtoList);
        assertEquals(1, itemDtoList.size());
        ItemDto itemDto = itemDtoList.get(0);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertThrows(ObjectNotFoundException.class, () -> itemService.getItemsByUserId(2L, 1, 1));
    }

    @Test
    void removeItemByIdTest() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.ofNullable(item));
        itemService.removeItemById(item.getId());
        assertThrows(ObjectNotFoundException.class, () -> itemService.removeItemById(7L));
    }

    @Test
    void searchItemsTest() {
        List<Item> items = new ArrayList<>(Collections.singletonList(item));
        Page<Item> pagedResponse = new PageImpl(items);
        Mockito
                .when(itemRepository.search(anyString(), any(Pageable.class)))
                .thenReturn(pagedResponse);
        List<ItemDto> itemDtoList = itemService.searchItems("item", 1, 1);
        assertNotNull(itemDtoList);
        assertEquals(1, itemDtoList.size());
        ItemDto itemDto = itemDtoList.get(0);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(0, itemService.searchItems("", 1, 1).size());
        assertThrows(ObjectNotFoundException.class, () -> itemService.getItemsByUserId(1L, 1, 1));
        assertNotNull(itemService.searchItems("test", 1, 1));
    }

    @Test
    void createComment() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemAndBookerAndEndIsBefore(any(Item.class), any(User.class), any(LocalDateTime.class))).thenReturn(booking);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        CommentDto comment1 = itemService.createComment(commentDto, 1L, 1L);
        assertEquals(commentDto.getId(), comment1.getId());
        assertEquals(commentDto.getText(), comment1.getText());
        assertEquals(user.getName(), comment1.getAuthorName());
        assertEquals(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")),
                comment1.getCreated().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")));
        assertThrows(ObjectNotFoundException.class, () -> itemService.createComment(commentDto, 1L, 2L));
    }
}