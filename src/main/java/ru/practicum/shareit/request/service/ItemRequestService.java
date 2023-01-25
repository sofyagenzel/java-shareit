package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestResponseDto createRequest(ItemRequestDto itemRequestDto, Long userId);

    ItemRequestResponseDto getRequest(Long id, Long userId);

    List<ItemRequestResponseDto> getUserRequests(Long userId, Integer from, Integer size);

    List<ItemRequestResponseDto> getAllRequests(Long userId, Integer from, Integer size);
}