package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Optional;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .request(item.getRequest())
                .build();
    }

    public static void toItem(Item item, ItemDto itemDto) {
        Optional.ofNullable(itemDto.getName()).ifPresent(x -> item.setName(itemDto.getName()));
        Optional.ofNullable(itemDto.getAvailable()).ifPresent(x -> item.setAvailable(itemDto.getAvailable()));
        Optional.ofNullable(itemDto.getDescription()).ifPresent(x -> item.setDescription(itemDto.getDescription()));
    }
}