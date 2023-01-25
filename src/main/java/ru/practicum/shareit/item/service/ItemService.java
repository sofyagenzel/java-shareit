package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

@Service
public interface ItemService {
    ItemResponseDto createItem(ItemDto itemDto, Long userId);

    ItemResponseDto updateItem(ItemDto itemDto, Long itemId, Long userId);

    List<ItemResponseDto> getItemsByUserId(Long userId, Integer from, Integer size);

    ItemResponseDto getItemById(Long id, Long userId);

    void removeItemById(Long id);

    List<ItemResponseDto> searchItems(String text, Integer from, Integer size);

    CommentResponseDto createComment(CommentDto commentDto, Long userId, Long itemId);
}