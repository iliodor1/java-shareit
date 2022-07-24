package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Optional<Item> getItem(Long id);

    Item addItem(Long ownerId, Item item);

    Item update(Long itemId, Item item);

    List<Item> getOwnItems(Long userId);

    List<Item> searchItem(Long userId, String text);

}
