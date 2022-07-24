package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {

    public ItemDto toDto(Item item) {
        Long id = item.getId();
        String name = item.getName();
        String description = item.getDescription();
        boolean available = item.getAvailable();

        return new ItemDto(id, name, description, available);
    }

    public Item toItem(ItemDto itemDto, Long userId) {
        return new Item(itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                userId);
    }

}
