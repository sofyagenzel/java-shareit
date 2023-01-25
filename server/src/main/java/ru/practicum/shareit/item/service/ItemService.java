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

    ItemResponseDto updateItem(Long itemId, ItemDto itemDto, Long userId);

    List<ItemResponseDto> findAll(Long userId, Integer from, Integer size);

    ItemResponseDto getItem(Long id, Long userId);

    void removeItem(Long id);

    List<ItemResponseDto> searchItems(String text, Integer from, Integer size);

    CommentResponseDto createComment(CommentDto commentDto, Long userId, Long itemId);
}