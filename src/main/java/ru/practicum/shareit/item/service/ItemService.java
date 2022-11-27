package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Autowired
    public ItemService(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    public ItemDto createItem(ItemDto itemDto, int userId) {
        Item newItem = new Item();
        ItemMapper.toItem(newItem, itemDto);
        User owner = userStorage.getUserById(userId);
        newItem.setOwner(owner);
        Item createdItem = itemStorage.createItem(newItem);
        return ItemMapper.toItemDto(createdItem);
    }

    public ItemDto updateItem(ItemDto itemDto, int itemId, int userId) {
        Item updatedItem;
        Item item = new Item(itemStorage.getItemById(itemId));
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new ObjectNotFoundException("User is not owner of item!");
        }
        ItemMapper.toItem(item, itemDto);
        updatedItem = itemStorage.updateItem(item);
        return ItemMapper.toItemDto(updatedItem);
    }

    public List<ItemDto> findAll() {
        return itemStorage.findAll()
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public ItemDto getItemById(int id) {
        return ItemMapper.toItemDto(itemStorage.getItemById(id));
    }

    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemStorage.findAll()
                .stream()
                .filter(i -> i.getDescription().toLowerCase().contains(text.toLowerCase()) && i.getAvailable())
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public void removeItemById(int id) {
        itemStorage.getItemById(id);
        itemStorage.removeItemById(id);
    }

    public List<ItemDto> getItemsByUserId(int userId) {
        return itemStorage.findAll()
                .stream()
                .filter(i -> Objects.equals(i.getOwner().getId(), userId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}