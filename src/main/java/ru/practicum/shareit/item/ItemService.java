package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto getItem(Long userId, Long id);

    List<ItemDto> getOwnItems(Long userId);

    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    List<ItemDto> searchItem(Long userId, String text);

}
