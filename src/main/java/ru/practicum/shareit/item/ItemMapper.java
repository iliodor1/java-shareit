package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

@Component
public class ItemMapper {

    public ItemDto toDto(Item item) {
        Long id = item.getId();
        String name = item.getName();
        String description = item.getDescription();
        Boolean available = item.getAvailable();
        ItemRequest request = item.getItemRequest();

        return new ItemDto(id, name, description, available, request);
    }

    public Item toItem(ItemDto itemDto, Long ownerId) {
        Long id = itemDto.getId();
        String name = itemDto.getName();
        String description = itemDto.getDescription();
        Boolean available = itemDto.getAvailable();
        ItemRequest request = itemDto.getItemRequest();

        return new Item(id, name, description, available, ownerId, request);
    }

}
