package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Optional;

public class RequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();

    }

    public static void toItemRequest(ItemRequest itemRequest, ItemRequestDto itemRequestDto) {
        itemRequest.setId(itemRequestDto.getId());
        Optional.ofNullable(itemRequestDto.getDescription()).ifPresent(itemRequest::setDescription);
    }
}
