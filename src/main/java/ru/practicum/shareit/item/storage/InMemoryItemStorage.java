package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryItemStorage implements ItemStorage {
    private final HashMap<Integer, Item> items = new HashMap();
    private int idItem = 0;

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Item createItem(Item item) {
        idItem++;
        item.setId(idItem);
        items.put(idItem, item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public Item getItemById(int id) {
        if (items.get(id) != null) {
            return items.get(id);
        } else {
            throw new ObjectNotFoundException("Запись не добавлена");
        }
    }

    @Override
    public void removeItemById(int id) {
        if (items.get(id) != null) {
            items.remove(id);
        } else {
            throw new ObjectNotFoundException("Запись не удалена");
        }
    }
}