package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Service
public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId);

    List<ItemDto> getItemsByUserId(Long userId, Integer from, Integer size);

    ItemDto getItemById(Long id, Long userId);

    void removeItemById(Long id);

    List<ItemDto> searchItems(String text, Integer from, Integer size);

    CommentDto createComment(CommentDto commentDto, Long userId, Long itemId);
}




