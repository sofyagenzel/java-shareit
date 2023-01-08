package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long userId);

    ItemRequestDto getRequest(Long id, Long userId);

    List<ItemRequestDto> getUserRequests(Long userId, Integer from, Integer size);

    List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size);
}