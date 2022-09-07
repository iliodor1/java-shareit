package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@AllArgsConstructor
public class ItemMapper {

    public static ItemDto toInputDto(Item item) {
        Long id = item.getId();
        String name = item.getName();
        String description = item.getDescription();
        Boolean available = item.getAvailable();
        Long requestId = item.getRequest() == null ? null : item.getRequest()
                                                                .getId();

        return new ItemDto(id, name, description, available, requestId);
    }

    public static ItemResponseDto toOutputDto(Item item) {
        Long requestId = item.getRequest() == null ? null : item.getRequest().getId();

        return ItemResponseDto.builder()
                              .id(item.getId())
                              .name(item.getName())
                              .description(item.getDescription())
                              .available(item.getAvailable())
                              .requestId(requestId)
                              .build();
    }

    public static Item toItem(ItemDto itemDto, User owner) {
        Long id = itemDto.getId();
        String name = itemDto.getName();
        String description = itemDto.getDescription();
        Boolean available = itemDto.getAvailable();

        return new Item(id, name, description, available, owner);
    }

}
